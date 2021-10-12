package com.bignerdbranch.android.photogallery

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations

class PhotoGalleryViewModel(private val app: Application) : AndroidViewModel(app) {

    private val flickrFetchr = FlickrFetchr()
    val galleryItemLiveData: LiveData<List<GalleryItem>>
    private val mutableSearchTerm = MutableLiveData<String>()
    /**
     * For a little bit of polish, pre-populate the search text box with the saved query when the user presses
     *the search icon to expand the search view.
     */
    val searchTerm: String
        get() = mutableSearchTerm.value ?: ""
    init {
        mutableSearchTerm.value = QueryPreferences.getStoredQuery(app)
        // Since both the search term and gallery item lists are wrapped in LiveData, you use
        //Transformations.switchMap(trigger: LiveData<X>, transformFunction: Function<X,
        //LiveData<Y>>)
        galleryItemLiveData = Transformations.switchMap(mutableSearchTerm) { searchTerm ->
            if (searchTerm.isBlank()) {
                flickrFetchr.fetchPhotos()
            } else {
                flickrFetchr.searchPhotos(searchTerm)
            }
        }
    }

    fun fetchPhotos(query: String = "") {
        QueryPreferences.setStoredQuery(app, query)
        mutableSearchTerm.value = query
    }

}