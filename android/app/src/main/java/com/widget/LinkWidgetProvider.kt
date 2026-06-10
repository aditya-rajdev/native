package com.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews

class LinkWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_layout)

            // 1. Adapter Setup: ListView ko data feed karne ke liye
            val intent = Intent(context, LinkWidgetService::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                data = Uri.parse(toUri(Intent.URI_INTENT_SCHEME))
            }
            views.setRemoteAdapter(R.id.widgetListView, intent)

            // 2. Global PendingIntent Template Setup
            // Hum ek base broadcast intent banate hain jo click hone par Receiver ko call karega
            val clickIntent = Intent(context, LinkClickReceiver::class.java).apply {
                action = "com.widget.ACTION_OPEN_URL"
            }
            
            // FIX: System cache bypass karne ke liye unique identifier generator implementation
            val uniqueRequestCode = (System.currentTimeMillis() and 0xfffffff).toInt()
            
            // Android 14+ / 16 ke liye FLAG_MUTABLE strictly mandatory hai taaki FillInIntent merge ho sake
            val clickPendingIntent = PendingIntent.getBroadcast(
                context,
                uniqueRequestCode, 
                clickIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
            
            // Template ko pure ListView widget container par assign kiya
            views.setPendingIntentTemplate(R.id.widgetListView, clickPendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }
}