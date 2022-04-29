package com.budi.setiawan.storyappbudisetiawan.view.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.budi.setiawan.storyappbudisetiawan.adapter.StoryAdapter
import com.budi.setiawan.storyappbudisetiawan.data.faker.DataFaker
import com.budi.setiawan.storyappbudisetiawan.data.item.StoryItems
import com.budi.setiawan.storyappbudisetiawan.util.TestCoroutineRule
import com.budi.setiawan.storyappbudisetiawan.util.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @Mock
    private lateinit var homeViewModel: HomeViewModel

    @Test
    fun `when stories not empty`() = testCoroutineRule.runBlockingTest {
        val hopeStories = DataFaker.getListStoryItems()
        val detail = PagedTestDataSources.snapshot(hopeStories)
        val mutableLiveData = MutableLiveData<PagingData<StoryItems>>().apply { value = detail }

        Mockito.`when`(homeViewModel.stories).thenReturn(mutableLiveData)
        val actuallyHome = homeViewModel.stories.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            mainDispatcher = testCoroutineRule.dispatcher,
            workerDispatcher = testCoroutineRule.dispatcher
        )

        differ.submitData(actuallyHome)
        advanceUntilIdle()

        Mockito.verify(homeViewModel).stories
        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(hopeStories.size, differ.snapshot().size)
    }

}

class PagedTestDataSources private constructor() :
    PagingSource<Int, LiveData<List<StoryItems>>>() {
    companion object {
        fun snapshot(items: List<StoryItems>): PagingData<StoryItems> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, LiveData<List<StoryItems>>>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<StoryItems>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}