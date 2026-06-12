package com.zurl.app

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.RemoteViews

class OneCellWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val prefs = context.getSharedPreferences("widget_data", Context.MODE_PRIVATE)

        for (appWidgetId in appWidgetIds) {
            val title = prefs.getString("widget_title_$appWidgetId", null)
            val url = prefs.getString("widget_url_$appWidgetId", "") ?: ""
            val customImg = prefs.getString("widget_img_$appWidgetId", "") ?: ""
            val lowerUrl = url.lowercase()

            
            if (title == null) {
                continue
            }

            val views = RemoteViews(context.packageName, R.layout.one_cell_widget_layout)

            // Custom Image rendering logic evaluation
            if (!customImg.isNullOrBlank()) {
                // CASE A: Custom image is present -> Hide text container, show full layout image
                views.setViewVisibility(R.id.defaultWidgetLayout, View.GONE)
                views.setViewVisibility(R.id.userCustomImageView, View.VISIBLE)
                views.setImageViewUri(R.id.userCustomImageView, Uri.parse(customImg))
            } else {
                // CASE B: Default rule setup -> Link domain dynamic coloring
                views.setViewVisibility(R.id.userCustomImageView, View.GONE)
                views.setViewVisibility(R.id.defaultWidgetLayout, View.VISIBLE)
                
                views.setTextViewText(R.id.oneCellWidgetTitle, title)

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

                views.setImageViewResource(R.id.oneCellWidgetIcon, iconRes)
                views.setInt(R.id.oneCellWidgetIcon, "setBackgroundColor", android.graphics.Color.TRANSPARENT)
                views.setInt(R.id.oneCellWidgetRoot, "setBackgroundColor", bgColor)
            }

            // Click handling pending intent mapping
            if (url.isNotBlank()) {
                val clickIntent = Intent(context, LinkClickReceiver::class.java).apply {
                    // FIXED: Aligned intent filters action to sync with new package architecture
                    action = "com.zurl.app.ACTION_OPEN_URL"
                    putExtra("EXTRA_URL", url)
                    data = Uri.parse("study://onecell/$appWidgetId")
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    appWidgetId,
                    clickIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                )
                views.setOnClickPendingIntent(R.id.oneCellWidgetRoot, pendingIntent)
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        val prefs = context.getSharedPreferences("widget_data", Context.MODE_PRIVATE).edit()
        for (appWidgetId in appWidgetIds) {
            prefs.remove("widget_title_$appWidgetId")
            prefs.remove("widget_url_$appWidgetId")
            prefs.remove("widget_img_$appWidgetId")
        }
        prefs.apply()
    }
}