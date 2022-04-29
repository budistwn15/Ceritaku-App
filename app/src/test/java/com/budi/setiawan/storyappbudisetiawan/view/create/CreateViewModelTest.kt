package com.budi.setiawan.storyappbudisetiawan.view.create

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.budi.setiawan.storyappbudisetiawan.data.item.UserItems
import com.budi.setiawan.storyappbudisetiawan.data.repository.StoryRepository
import com.budi.setiawan.storyappbudisetiawan.data.repository.UserRepository
import com.budi.setiawan.storyappbudisetiawan.error.StoryError
import com.budi.setiawan.storyappbudisetiawan.util.TestCoroutineRule
import com.budi.setiawan.storyappbudisetiawan.util.getOrAwaitValue
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class CreateViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val userRepo: UserRepository = Mockito.mock(UserRepository::class.java)
    private val storyRepo: StoryRepository = Mockito.mock(StoryRepository::class.java)

    private lateinit var createViewModel: CreateViewModel

    @Before
    fun setUp() {
        Mockito.`when`(userRepo.userItems).thenReturn(
            flow {
                emit(UserItems(email = "data.faker@mail.com", password = "12345678"))
            }
        )

        createViewModel = CreateViewModel(userRepo, storyRepo)
    }

    @Test
    fun `when userItems observed return UserItem`() = testCoroutineRule.runBlockingTest {
        val actuallyUser = createViewModel.userItem.getOrAwaitValue()
        assertNotNull(actuallyUser)
    }

    @Test
    fun `when uploadImage success not throw error and isSuccess`() = testCoroutineRule.runBlockingTest {
        val token = "token"
        val file = MultipartBody.Part.createFormData("file", "dummyfile")
        val desc = "desc".toRequestBody("text/plain".toMediaType())
        val latLng = LatLng(0.0, 0.0)

        createViewModel.uploadImage(token, file, desc, latLng)
        Mockito.verify(storyRepo).postStory(token, file, desc, latLng)
        assertTrue(createViewModel.isSuccess.getOrAwaitValue())
    }

    @Test
    fun `when uploadImage success throw error`() = testCoroutineRule.runBlockingTest {
        val token = "token"
        val file = MultipartBody.Part.createFormData("file", "dummyfile")
        val desc = "desc".toRequestBody("text/plain".toMediaType())
        val latLng = LatLng(0.0, 0.0)

        Mockito.doThrow(StoryError("Error")).`when`(storyRepo).postStory(token, file, desc, latLng)
        createViewModel.uploadImage(token, file, desc, latLng)
        Mockito.verify(storyRepo).postStory(token, file, desc, latLng)
        assertTrue(createViewModel.errorMessage.getOrAwaitValue() == "Error")
    }
}