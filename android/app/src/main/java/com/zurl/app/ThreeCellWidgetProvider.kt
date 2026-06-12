package com.zurl.app

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.RemoteViews

class ThreeCellWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val prefs = context.getSharedPreferences("widget_data", Context.MODE_PRIVATE)

        for (appWidgetId in appWidgetIds) {
            val title = prefs.getString("widget_title_$appWidgetId", null)
            val url = prefs.getString("widget_url_$appWidgetId", "") ?: ""
            val lowerUrl = url.lowercase()

            if (title == null) continue // Skip if configuration data stream isn't saved yet

            val views = RemoteViews(context.packageName, R.layout.three_by_one_widget_layout)
            views.setTextViewText(R.id.threeCellWidgetTitle, title)

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

            views.setImageViewResource(R.id.threeCellWidgetIcon, iconRes)
            // SAFEGUARD: Clear any hidden solid backgrounds under the vector icon frame
            views.setInt(R.id.threeCellWidgetIcon, "setBackgroundColor", android.graphics.Color.TRANSPARENT)
            views.setInt(R.id.threeCellWidgetRoot, "setBackgroundColor", bgColor)

            if (url.isNotBlank()) {
                val clickIntent = Intent(context, LinkClickReceiver::class.java).apply {
                    // FIXED: Aligned action to match the new package architecture
                    action = "com.zurl.app.ACTION_OPEN_URL"
                    putExtra("EXTRA_URL", url)
                    data = Uri.parse("study://threecell/$appWidgetId")
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    appWidgetId,
                    clickIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                )
                views.setOnClickPendingIntent(R.id.threeCellWidgetRoot, pendingIntent)
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        val prefs = context.getSharedPreferences("widget_data", Context.MODE_PRIVATE).edit()
        for (appWidgetId in appWidgetIds) {
            prefs.remove("widget_title_$appWidgetId")
            prefs.remove("widget_url_$appWidgetId")
        }
        prefs.apply()
    }
}