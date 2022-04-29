package com.budi.setiawan.storyappbudisetiawan.view.maps

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.budi.setiawan.storyappbudisetiawan.data.faker.DataFaker
import com.budi.setiawan.storyappbudisetiawan.data.item.UserItems
import com.budi.setiawan.storyappbudisetiawan.data.repository.StoryRepository
import com.budi.setiawan.storyappbudisetiawan.data.repository.UserRepository
import com.budi.setiawan.storyappbudisetiawan.error.StoryError
import com.budi.setiawan.storyappbudisetiawan.util.TestCoroutineRule
import com.budi.setiawan.storyappbudisetiawan.util.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.doThrow

@OptIn(ExperimentalCoroutinesApi::class)
class MapsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val userRepo: UserRepository = Mockito.mock(UserRepository::class.java)
    private val storyRepo: StoryRepository = Mockito.mock(StoryRepository::class.java)

    private lateinit var mapsViewModel: MapsViewModel

    @Before
    fun setUp() {
        Mockito.`when`(userRepo.userItems).thenReturn(
            flow {
                emit(UserItems(email = "data.faker@mail.com", password = "12345678"))
            }
        )
        mapsViewModel = MapsViewModel(userRepo, storyRepo)
    }

    @Test
    fun `when userModel observed return UserItem`() = testCoroutineRule.runBlockingTest {
        val actuallyUser = mapsViewModel.userItems.getOrAwaitValue()
        Assert.assertNotNull(actuallyUser)
    }

    @Test
    fun `when getStoriesWithLocation success not throw error`() = testCoroutineRule.runBlockingTest {
        val token = "token"
        val actuallyStories = DataFaker.getListStoryItems()

        Mockito.`when`(storyRepo.getStoriesWithLocation(token)).thenReturn(actuallyStories)
        mapsViewModel.getStoriesWithLocation(token)

        Mockito.verify(storyRepo).getStoriesWithLocation(token)

        val storiesResult = mapsViewModel.stories.getOrAwaitValue()

        Assert.assertNotNull(storiesResult)
        Assert.assertTrue(storiesResult[0].name == "name-0")
    }

    @Test
    fun `when getStoriesWithLocation failed throw error`() = testCoroutineRule.runBlockingTest {
        val token = "token"

        doThrow(StoryError("Error")).`when`(storyRepo).getStoriesWithLocation(token)
        mapsViewModel.getStoriesWithLocation(token)

        Mockito.verify(storyRepo).getStoriesWithLocation(token)
        Assert.assertTrue(mapsViewModel.errorMessage.getOrAwaitValue() == "Error")
    }
}