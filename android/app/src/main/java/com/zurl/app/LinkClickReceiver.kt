package com.zurl.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast

class LinkClickReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("LinkClickReceiver", "Broadcast Received! Action: ${intent.action}")

        // FIXED: Aligned intent filter check string to match new package naming architecture
        if ("com.zurl.app.ACTION_OPEN_URL" == intent.action) {
            val url = intent.getStringExtra("EXTRA_URL")
            Log.d("LinkClickReceiver", "Opening exact URL: $url")

            if (!url.isNullOrBlank()) {
                try {
                    // Agar URL me http/https miss ho gaya ho toh auto-correct karein
                    val formattedUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) {
                        "https://$url"
                    } else {
                        url
                    }

                    // Browser launch karne ka explicit action view intent
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(formattedUrl)).apply {
                        // Background state/receiver se task generate karne ke liye ye flag strictly mandatory hai
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(browserIntent)
                    
                } catch (e: Exception) {
                    Log.e("LinkClickReceiver", "Could not open browser for URL: $url", e)
                    Toast.makeText(context, "Browser kholne me dikkat aayi", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.w("LinkClickReceiver", "URL extra khali (null/blank) mila.")
            }
        }
    }
}