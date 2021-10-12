package com.bignerdbranch.android.photogallery

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Build

const val NOTIFICATION_CHANNEL_ID = "flickr_poll"
class PhotoGalleryApplication : Application() {
    companion object {
        lateinit var instance: Application
        lateinit var res: Resources

        /**
         * This function will return an Intent instancethat can be used to start PhotoGalleryActivity
         */
        fun newIntent(context: Context): Intent {
            return Intent(context, PhotoGalleryActivity::class.java)
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        res = resources
        //create the notification channel and object
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notification_channel_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel =
                NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance)
            val notificationManager: NotificationManager =
                getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}