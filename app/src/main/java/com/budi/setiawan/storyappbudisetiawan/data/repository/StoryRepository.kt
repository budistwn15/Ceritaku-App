package com.budi.setiawan.storyappbudisetiawan.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.budi.setiawan.storyappbudisetiawan.data.database.StoryDatabase
import com.budi.setiawan.storyappbudisetiawan.data.item.StoryItems
import com.budi.setiawan.storyappbudisetiawan.data.remote.StoryRemoteDataSource
import com.budi.setiawan.storyappbudisetiawan.data.remote.StoryRemoteMediator
import com.budi.setiawan.storyappbudisetiawan.error.StoryError
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository(
    private val storyRemoteDataSource: StoryRemoteDataSource,
    private val storyDatabase: StoryDatabase
) {

    @OptIn(ExperimentalPagingApi::class)
    fun getStories(token: String): LiveData<PagingData<StoryItems>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            remoteMediator = StoryRemoteMediator(token, storyRemoteDataSource, storyDatabase),
            pagingSourceFactory = {
                storyDatabase.storyDao().getStories()
            }).liveData
    }

    suspend fun getStoriesWithLocation(token: String): List<StoryItems> {
        val stories = mutableListOf<StoryItems>()
        withContext(Dispatchers.IO) {
            try {
                val response = storyRemoteDataSource.getStoriesWithLocation(token, 1, 20)
                if (response.isSuccessful) {
                    response.body()?.listStory?.forEach {
                        stories.add(
                            StoryItems(
                                id = it.id,
                                name = it.name,
                                createdAt = it.createdAt,
                                imageUrl = it.photoUrl,
                                description = it.description,
                                lat = it.lat,
                                lon = it.lon
                            )
                        )
                    }
                }
            } catch (e: Throwable) {
                throw StoryError(e.message.toString())
            }
        }

        return stories
    }

    suspend fun postStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        latLng: LatLng?
    ) {
        withContext(Dispatchers.IO) {
            try {
                val response = if (latLng != null) {
                    storyRemoteDataSource.postStories(
                        token,
                        file,
                        description,
                        latLng.latitude,
                        latLng.longitude
                    )
                } else {
                    storyRemoteDataSource.postStories(token, file, description)
                }

                if (!response.isSuccessful) {
                    throw StoryError(response.message())
                }
            } catch (e: Throwable) {
                throw StoryError(e.message.toString())
            }
        }
    }
}