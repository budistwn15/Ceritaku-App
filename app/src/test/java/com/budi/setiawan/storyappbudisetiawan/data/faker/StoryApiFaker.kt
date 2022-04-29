package com.budi.setiawan.storyappbudisetiawan.data.faker

import com.budi.setiawan.storyappbudisetiawan.api.apiinterface.StoryInterface
import com.budi.setiawan.storyappbudisetiawan.data.response.StoryCreateResponse
import com.budi.setiawan.storyappbudisetiawan.data.response.StoryResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

class StoryApiFaker : StoryInterface {

    var mustThrowError = false

    override suspend fun getStories(
        token: String,
        page: Int,
        size: Int,
        location: Int
    ): Response<StoryResponse> {
        return if (mustThrowError) {
            Response.error(401, "".toResponseBody("text/plain".toMediaType()))
        } else {
            Response.success(
                StoryResponse(
                    listStory = DataFaker.dataStoryItems(),
                    error = false,
                    message = "Success"
                )
            )
        }
    }

    override suspend fun postStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: Double?,
        lon: Double?
    ): Response<StoryCreateResponse> {
        return if (mustThrowError) {
            Response.error(401, "".toResponseBody("text/plain".toMediaType()))
        } else {
            Response.success(StoryCreateResponse(error = false, message = "Success"))
        }
    }
}