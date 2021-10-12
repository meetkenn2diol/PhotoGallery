package com.bignerdbranch.android.photogallery.api


import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface FlickrApi {

    /**
     * Gson will use FlickrResponse to deserialize the JSON response data.
     */
    @GET("services/rest/?method=flickr.interestingness.getList")
    fun fetchPhotos(): Call<FlickrResponse>

    /**
     * Call :An invocation of a Retrofit method that sends a request to a webserver and returns a response.
     *
     * @return Response<ResponseBody!>
     */
    @GET
    fun fetchUrlBytes(@Url url: String): Call<ResponseBody>

    /**
     * Search for a photo in Flickr.com.
     *
     * NOTE: This method is called before the interceptors are invoked
     */
    @GET("services/rest?method=flickr.photos.search")
    fun searchPhotos(@Query("text") query: String): Call<FlickrResponse>
}