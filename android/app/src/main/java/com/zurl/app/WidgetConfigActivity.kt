package com.zurl.app

import android.app.Activity
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.RemoteViews
import android.widget.TextView
import android.widget.Toast
import org.json.JSONArray
import java.util.ArrayList

class WidgetConfigActivity : Activity() {

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResult(RESULT_CANCELED)

        val extras = intent.extras
        if (extras != null) {
            appWidgetId = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID
            )
        }

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        setContentView(R.layout.activity_widget_config)
        val listView = findViewById<ListView>(R.id.configListView)

        val sharedPref = getSharedPreferences("widget_data", Context.MODE_PRIVATE)
        val jsonStr = sharedPref.getString("links", "[]") ?: "[]"
        
        val linksList = ArrayList<LinkData>()

        try {
            val jsonArray = JSONArray(jsonStr)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                linksList.add(
                    LinkData(
                        title = obj.optString("title", "No Title"),
                        url = obj.optString("url", ""),
                        customImage = obj.optString("customImage", "")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (linksList.isEmpty()) {
            Toast.makeText(this, "Pehle app me links add karein!", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val configAdapter = IntelligentConfigAdapter(this, linksList)
        listView.adapter = configAdapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val context = this@WidgetConfigActivity
            val selectedItem = linksList[position]
            val lowerUrl = selectedItem.url.lowercase()

            // 1. SharedPreferences me write backup pass perfectly
            val prefs = context.getSharedPreferences("widget_data", Context.MODE_PRIVATE).edit()
            prefs.putString("widget_title_$appWidgetId", selectedItem.title)
            prefs.putString("widget_url_$appWidgetId", selectedItem.url)
            prefs.putString("widget_img_$appWidgetId", selectedItem.customImage)
            prefs.apply()

            val appWidgetManager = AppWidgetManager.getInstance(context)

            // HARD FIX: Extract widget info to identify if current dropped layout component is 3x1 or 1x1
            val widgetInfo = appWidgetManager.getAppWidgetInfo(appWidgetId)
            val isThreeCell = widgetInfo?.provider?.className?.contains("ThreeCellWidgetProvider") == true

            val iconRes = when {
                lowerUrl.contains("youtube") || lowerUrl.contains("youtu.be") -> R.drawable.youtube
                lowerUrl.contains("github") -> R.drawable.github
                lowerUrl.contains("linkedin") -> R.drawable.linkedin
                lowerUrl.contains("leetcode") -> R.drawable.leetcode
                lowerUrl.contains("instagram") -> R.drawable.instagram
                else -> R.drawable.link
            }

            val bgColor = when {
                lowerUrl.contains("youtube") || lowerUrl.contains("youtu.be") -> android.graphics.Color.parseColor("#de0000")
                lowerUrl.contains("github") -> android.graphics.Color.parseColor("#000000")
                lowerUrl.contains("linkedin") -> android.graphics.Color.parseColor("#006de1")
                lowerUrl.contains("leetcode") -> android.graphics.Color.parseColor("#ec9c08")
                lowerUrl.contains("instagram") -> android.graphics.Color.parseColor("#df088d")
                else -> android.graphics.Color.parseColor("#1A1A1A")
            }

            // 2. Widget UI configuration layout rendering routing block
            if (isThreeCell) {
                // CASE A: $3 \times 1$ Horizontal Ribbon View Architecture
                val views = RemoteViews(context.packageName, R.layout.three_by_one_widget_layout)
                views.setTextViewText(R.id.threeCellWidgetTitle, selectedItem.title)
                views.setImageViewResource(R.id.threeCellWidgetIcon, iconRes)
                views.setInt(R.id.threeCellWidgetRoot, "setBackgroundColor", bgColor)

                val clickIntent = Intent(context, LinkClickReceiver::class.java).apply {
                    // FIXED: Aligned intent filter action string
                    action = "com.zurl.app.ACTION_OPEN_URL"
                    putExtra("EXTRA_URL", selectedItem.url)
                    data = Uri.parse("study://threecell/$appWidgetId")
                }
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    appWidgetId,
                    clickIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                )
                views.setOnClickPendingIntent(R.id.threeCellWidgetRoot, pendingIntent)
                appWidgetManager.updateAppWidget(appWidgetId, views)

            } else {
                // CASE B: $1 \times 1$ Standard Icon Square View Architecture
                val views = RemoteViews(context.packageName, R.layout.one_cell_widget_layout)
                views.setViewVisibility(R.id.userCustomImageView, View.GONE)
                views.setViewVisibility(R.id.defaultWidgetLayout, View.VISIBLE)
                views.setTextViewText(R.id.oneCellWidgetTitle, selectedItem.title)
                views.setImageViewResource(R.id.oneCellWidgetIcon, iconRes)
                
                // FIXED: Clear any hidden solid tint backgrounds on 1x1 configuration setup phase
                views.setInt(R.id.oneCellWidgetIcon, "setBackgroundColor", android.graphics.Color.TRANSPARENT)
                views.setInt(R.id.oneCellWidgetRoot, "setBackgroundColor", bgColor)

                val clickIntent = Intent(context, LinkClickReceiver::class.java).apply {
                    // FIXED: Aligned intent filter action string
                    action = "com.zurl.app.ACTION_OPEN_URL"
                    putExtra("EXTRA_URL", selectedItem.url)
                    data = Uri.parse("study://onecell/$appWidgetId")
                }
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    appWidgetId,
                    clickIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                )
                views.setOnClickPendingIntent(R.id.oneCellWidgetRoot, pendingIntent)
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }

            // 3. HARD FIX: Target precise dynamic component to send immediate broadcast (Bypasses all latency delays)
            val updateIntent = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(appWidgetId))
                component = if (isThreeCell) {
                    ComponentName(context, ThreeCellWidgetProvider::class.java)
                } else {
                    ComponentName(context, OneCellWidgetProvider::class.java)
                }
            }
            context.sendBroadcast(updateIntent)

            // Notify launcher that setup engine process complete
            val resultValue = Intent().apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
            setResult(RESULT_OK, resultValue)
            finish()
        }
    }

    data class LinkData(val title: String, val url: String, val customImage: String)

    private class IntelligentConfigAdapter(val context: Context, val items: List<LinkData>) : BaseAdapter() {
        override fun getCount(): Int = items.size
        override fun getItem(position: Int): Any = items[position]
        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.widget_config_item, parent, false)
            
            val item = items[position]
            val iconView = view.findViewById<ImageView>(R.id.configItemIcon)
            val titleView = view.findViewById<TextView>(R.id.configItemTitle)
            val subView = view.findViewById<TextView>(R.id.configItemSub)

            titleView.text = item.title
            
            val cleanedSubText = item.url.replace("https://", "").replace("http://", "").split("/")[0]
            subView.text = cleanedSubText

            val lowerUrl = item.url.lowercase()

            val iconRes = when {
                lowerUrl.contains("youtube") || lowerUrl.contains("youtu.be") -> R.drawable.youtube
                lowerUrl.contains("github") -> R.drawable.github
                lowerUrl.contains("linkedin") -> R.drawable.linkedin
                lowerUrl.contains("leetcode") -> R.drawable.leetcode
                lowerUrl.contains("instagram") -> R.drawable.instagram
                else -> R.drawable.link
            }

            if (!item.customImage.isNullOrBlank()) {
                iconView.setImageURI(Uri.parse(item.customImage))
            } else {
                iconView.setImageResource(iconRes)
            }

            return view
        }
    }
}