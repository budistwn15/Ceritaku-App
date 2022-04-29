package com.budi.setiawan.storyappbudisetiawan.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import com.budi.setiawan.storyappbudisetiawan.adapter.StoryAdapter
import com.budi.setiawan.storyappbudisetiawan.data.database.StoryDatabase
import com.budi.setiawan.storyappbudisetiawan.data.faker.DataFaker
import com.budi.setiawan.storyappbudisetiawan.data.faker.StoryApiFaker
import com.budi.setiawan.storyappbudisetiawan.data.item.StoryItems
import com.budi.setiawan.storyappbudisetiawan.data.remote.StoryRemoteDataSource
import com.budi.setiawan.storyappbudisetiawan.util.TestCoroutineRule
import com.budi.setiawan.storyappbudisetiawan.util.getOrAwaitValue
import com.budi.setiawan.storyappbudisetiawan.view.home.noopListUpdateCallback
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class StoryRepositoryTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @Mock
    private lateinit var storyDb: StoryDatabase
    private lateinit var storyRDSource: StoryRemoteDataSource
    private lateinit var storyRepo: StoryRepository

    @Before
    fun setUp() {
        storyRDSource = StoryRemoteDataSource(StoryApiFaker())
        storyRepo = StoryRepository(storyRDSource, storyDb)
    }

    @Test
    fun `when getStories not return empty PagingData`() = testCoroutineRule.runBlockingTest {
        storyRepo = Mockito.mock(StoryRepository::class.java)
        val token = "token"
        val hopeStories = DataFaker.getListStoryItems()
        val hovePagingData = DataFaker.pagingDataStoryItems()
        val mutableLiveData = MutableLiveData<PagingData<StoryItems>>().apply {
            value = hovePagingData
        }
        Mockito.`when`(storyRepo.getStories(token)).thenReturn(mutableLiveData)
        val actuallyPagingData = storyRepo.getStories(token)
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            mainDispatcher = testCoroutineRule.dispatcher,
            workerDispatcher = testCoroutineRule.dispatcher
        )
        differ.submitData(actuallyPagingData.getOrAwaitValue())
        advanceUntilIdle()
        assertNotNull(differ.snapshot())
        assertEquals(hopeStories.size, differ.snapshot().size)
    }

    @Test
    fun `when getStoriesWithLocation not return empty list`() = runBlocking {
        val token = "token"
        val hopeStories = DataFaker.dataStoryItems()
        val actuallyStories = storyRepo.getStoriesWithLocation(token)
        assertTrue(actuallyStories.isNotEmpty())
        assertEquals(hopeStories[0].id, actuallyStories[0].id)
    }

    @Test
    fun `when getStoriesWithLocation return empty list`() = runBlocking {
        storyRDSource =
            StoryRemoteDataSource(StoryApiFaker().apply { mustThrowError = true })
        storyRepo = StoryRepository(storyRDSource, storyDb)
        val token = "token"
        val actuallyStories = storyRepo.getStoriesWithLocation(token)
        print(actuallyStories)
        assertTrue(actuallyStories.isNullOrEmpty())
    }
}