package com.budi.setiawan.storyappbudisetiawan.data.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.budi.setiawan.storyappbudisetiawan.data.database.StoryDatabase
import com.budi.setiawan.storyappbudisetiawan.data.item.RemoteItems
import com.budi.setiawan.storyappbudisetiawan.data.item.StoryItems
import com.budi.setiawan.storyappbudisetiawan.resource.wrapEspressoIdlingResource
import retrofit2.HttpException
import java.io.IOException

private const val INITIAL_PAGE_INDEX = 1

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val token: String,
    private val storyRemoteDataSource: StoryRemoteDataSource,
    private val storyDatabase: StoryDatabase,
) : RemoteMediator<Int, StoryItems>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StoryItems>,
    ): MediatorResult {
        wrapEspressoIdlingResource {
            val page = when (loadType) {
                LoadType.REFRESH -> {
                    val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                    remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
                }
                LoadType.PREPEND -> {
                    val remoteKeys = getRemoteKeyForFirstItem(state)
                    val prevKey = remoteKeys?.prevKey
                        ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                    prevKey
                }
                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    val nextKey = remoteKeys?.nextKey
                        ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                    nextKey
                }
            }

            val stories = mutableListOf<StoryItems>()
            try {
                val responses = storyRemoteDataSource.getStories(
                    token,
                    page,
                    state.config.pageSize
                )

                val endOfPaginationReached = responses.body()?.listStory.isNullOrEmpty()
                responses.body()?.listStory?.forEach {
                    stories.add(
                        StoryItems(
                            id = it.id,
                            name = it.name,
                            createdAt = it.createdAt,
                            imageUrl = it.photoUrl,
                            description = it.description
                        )
                    )
                }

                storyDatabase.withTransaction {
                    if (loadType == LoadType.REFRESH) {
                        storyDatabase.remoteItemsDao().deleteRemoteKeys()
                        storyDatabase.storyDao().deleteAllStories()
                    }
                    val prevKey = if (page == 1) null else page - 1
                    val nextKey = if (endOfPaginationReached) null else page + 1
                    val keys = stories.map {
                        RemoteItems(id = it.id, prevKey = prevKey, nextKey = nextKey)
                    }
                    storyDatabase.remoteItemsDao().insertAll(keys)
                    storyDatabase.storyDao().insertStory(stories)
                }
                return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
            } catch (e: IOException) {
                return MediatorResult.Error(e)
            } catch (e: HttpException) {
                return MediatorResult.Error(e)
            }
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, StoryItems>): RemoteItems? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            storyDatabase.remoteItemsDao().getRemoteKeysById(data.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, StoryItems>): RemoteItems? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            storyDatabase.remoteItemsDao().getRemoteKeysById(data.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, StoryItems>): RemoteItems? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                storyDatabase.remoteItemsDao().getRemoteKeysById(id)
            }
        }
    }
}