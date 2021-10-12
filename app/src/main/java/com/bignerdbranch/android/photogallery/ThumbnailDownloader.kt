package com.bignerdbranch.android.photogallery

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import java.util.concurrent.ConcurrentHashMap


private const val TAG = "ThumbnailDownloader"
private const val MESSAGE_DOWNLOAD = 0

/**
 * This class receive and process download requests one at a time using Threads, and it will provide the resulting image for each individual request as the corresponding download completes
 *
 * <>
 *
 * Implementing LifecycleObserver means you can register ThumbnailDownloader to receive lifecycle callbacks from any LifecycleOwner e.g [Activity] and [Fragment]
 *
 * <>
 *@param responseHandler: The Handler of the Fragment calling this class
 *@param onThumbnailDownloaded: A function to assign the downloaded bitmap image to the PhotoHolder
 */
class ThumbnailDownloader<in T>(
    private val responseHandler: Handler,
    private val onThumbnailDownloaded: (T, Bitmap) -> Unit
) : HandlerThread(TAG){

    /**
     * signals when your thread has qui
     */
    private var hasQuit = false
    private lateinit var requestHandler: Handler
    private val requestMap = ConcurrentHashMap<T, String>()
    private val flickrFetchr = FlickrFetchr()

    /**
     * This [LifecycleObserver] is used to solve the bug on configuration change for the [PhotoGalleryFragment]
     */
    val fragmentLifecycleObserver: LifecycleObserver =
        object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
            fun setup() {
                Log.i(TAG, "Starting background thread")
                start()
                looper
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun tearDown() {
                Log.i(TAG, "Destroying background thread")
                quit()
            }
        }

    /**
     * This [LifecycleObserver] is used to solve the bug on configuration change for the [PhotoGalleryFragment] Views
     */
    val viewLifecycleObserver: LifecycleObserver =
        object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun clearQueue() {
                Log.i(TAG, "Clearing all requests from queue")
                requestHandler.removeMessages(MESSAGE_DOWNLOAD)
                requestMap.clear()
            }
        }

    override fun quit(): Boolean {
        hasQuit = true
        return super.quit()
    }

    @Suppress("UNCHECKED_CAST")
    @SuppressLint("HandlerLeak")
    override fun onLooperPrepared() {
        requestHandler = object : Handler() {
            override fun handleMessage(msg: Message) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                    val target = msg.obj as T
                    Log.i(TAG, "Got a request for URL: ${requestMap[target!!]}")
                    handleRequest(target)
                }
            }
        }


    }

    fun queueThumbnail(target: T, url: String) {
        Log.i(TAG, "Got a URL: $url")
        requestMap[target] = url
        requestHandler.obtainMessage(MESSAGE_DOWNLOAD, target).sendToTarget()

    }

    private fun handleRequest(target: T) {
        val url = requestMap[target!!] ?: return
        val bitmap = flickrFetchr.fetchPhoto(url) ?: return
        Log.d(TAG, "Image successfully downloaded")

        /*update the bitmap image of the PhotoHolder to the new bitmap
        Because responseHandler is associated with the main thread’s Looper, all of the code inside of
        Runnable’s run() will be executed on the main thread.
         First, you double-check the requestMap. This is necessary because the
        RecyclerView recycles its views*/
        responseHandler.post(Runnable {
            if (requestMap[target] != url || hasQuit) {
                return@Runnable
            }
            requestMap.remove(target)
            onThumbnailDownloaded(target, bitmap)
        })
    }
}