package com.bignerdbranch.android.photogallery.api

/**
 * This class will map to the outermost
object in the JSON data (the one at the top of the JSON object hierarchy, denoted by the outermost
{ }).
 */
class FlickrResponse {
    lateinit var photos: PhotoResponse
}