package com.zurl.app

import android.content.Intent
import android.widget.RemoteViewsService

class LinkWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        // Factory context instantiation handle karein
        return LinkWidgetFactory(this.applicationContext, intent)
    }
}