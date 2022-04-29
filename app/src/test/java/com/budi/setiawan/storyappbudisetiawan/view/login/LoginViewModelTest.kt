package com.budi.setiawan.storyappbudisetiawan.view.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.budi.setiawan.storyappbudisetiawan.data.item.UserItems
import com.budi.setiawan.storyappbudisetiawan.data.repository.UserRepository
import com.budi.setiawan.storyappbudisetiawan.error.AuthError
import com.budi.setiawan.storyappbudisetiawan.util.TestCoroutineRule
import com.budi.setiawan.storyappbudisetiawan.util.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.doThrow
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @Mock
    private lateinit var userRepo: UserRepository
    private lateinit var loginViewModel: LoginViewModel

    @Before
    fun setUp() {
        Mockito.`when`(userRepo.userItems).thenReturn(
            flow {
                emit(UserItems(email = "data.faker@mail.com", password = "12345678"))
            }
        )

        loginViewModel = LoginViewModel(userRepo)
    }

    @Test
    fun `when userModel observed should return UserItem`() = testCoroutineRule.runBlockingTest {
        val actuallyUser = loginViewModel.userItem.getOrAwaitValue()
        assertNotNull(actuallyUser)
    }

    @Test
    fun `when login called should not throw error`() = testCoroutineRule.runBlockingTest {
        loginViewModel.login("data.faker@mail.com", "password")
        Mockito.verify(userRepo).login("data.faker@mail.com", "password")
    }

    @Test
    fun `when login failed should throw error`() = testCoroutineRule.runBlockingTest {
        val email = "data.faker@mail.com"
        val password = "qwerty12345678"

        doThrow(AuthError("Error")).`when`(userRepo).login(email, password)
        loginViewModel.login(email, password)

        Mockito.verify(userRepo).login(email, password)
        assertTrue(loginViewModel.errorMessage.getOrAwaitValue() == "Error")
    }
}