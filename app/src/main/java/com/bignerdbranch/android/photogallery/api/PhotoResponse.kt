package com.bignerdbranch.android.photogallery.api

import com.bignerdbranch.android.photogallery.GalleryItem
import com.google.gson.annotations.SerializedName

/**
 * A class to map to the "photos" object in the JSON data
 */
class PhotoResponse {
    @SerializedName("photo")
    lateinit var galleryItems: List<GalleryItem>
}