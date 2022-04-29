package com.budi.setiawan.storyappbudisetiawan.api.apiinterface

import com.budi.setiawan.storyappbudisetiawan.data.response.StoryCreateResponse
import com.budi.setiawan.storyappbudisetiawan.data.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface StoryInterface {
    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 10,
        @Query("location") location: Int = 0
    ): Response<StoryResponse>

    @Multipart
    @POST("stories")
    suspend fun postStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Double? = null,
        @Part("lon") lon: Double? = null
    ): Response<StoryCreateResponse>
}