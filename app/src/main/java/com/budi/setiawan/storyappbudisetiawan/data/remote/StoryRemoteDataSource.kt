package com.budi.setiawan.storyappbudisetiawan.data.remote

import com.budi.setiawan.storyappbudisetiawan.api.apiinterface.StoryInterface
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRemoteDataSource(private val storyInterface: StoryInterface) {

    suspend fun getStories(token: String, page: Int, size: Int) =
        storyInterface.getStories("Bearer $token", page, size)

    suspend fun getStoriesWithLocation(token: String, page: Int, size: Int) =
        storyInterface.getStories("Bearer $token", page, size, 1)

    suspend fun postStories(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: Double? = null,
        lon: Double? = null
    ) = storyInterface.postStory("Bearer $token", file, description, lat, lon)
}