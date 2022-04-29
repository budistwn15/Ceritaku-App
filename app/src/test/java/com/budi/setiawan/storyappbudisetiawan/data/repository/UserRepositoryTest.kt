package com.budi.setiawan.storyappbudisetiawan.data.repository

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.budi.setiawan.storyappbudisetiawan.data.faker.UserApiFaker
import com.budi.setiawan.storyappbudisetiawan.data.item.UserItems
import com.budi.setiawan.storyappbudisetiawan.data.preferences.SharedPrefUserLogin
import com.budi.setiawan.storyappbudisetiawan.data.remote.UserRemoteDataSource
import com.budi.setiawan.storyappbudisetiawan.error.AuthError
import com.budi.setiawan.storyappbudisetiawan.util.TestCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class UserRepositoryTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @Mock
    private lateinit var pref: SharedPrefUserLogin
    private lateinit var context: Context
    private lateinit var userRDSource: UserRemoteDataSource
    private lateinit var userRepo: UserRepository

    @Before
    fun setUp() {
        context = Mockito.mock(Context::class.java)
        userRDSource = UserRemoteDataSource(UserApiFaker())
        userRepo = UserRepository(context, userRDSource, pref)
    }

    @Test
    fun `when login success should not throw error`() = testCoroutineRule.runBlockingTest {
        val email = "data.faker@mail.com"
        val password = "password"
        val name = "name"
        val token = "token"
        val id = "id"

        val hopeUser = UserItems(
            id = id,
            name = name,
            email = email,
            password = password,
            token = token,
            isLoggedIn = true
        )

        userRepo.login(email, password)
        Mockito.verify(pref).updateUser(hopeUser)
    }

    @Test
    fun `when login failed should throw error`() {
        val email = "data.faker@mail.com"
        val password = "password"

        userRDSource = UserRemoteDataSource(UserApiFaker().apply { mustThrowError = true })
        userRepo = UserRepository(context, userRDSource, pref)

        Assert.assertThrows(AuthError::class.java) {
            runBlockingTest {
                userRepo.login(email, password)
            }
        }
    }

    @Test
    fun `when signup failed should throw error`() {
        val email = "data.faker@mail.com"
        val password = "password"
        val name = "name"

        userRDSource = UserRemoteDataSource(UserApiFaker().apply { mustThrowError = true })
        userRepo = UserRepository(context, userRDSource, pref)

        Assert.assertThrows(AuthError::class.java) {
            runBlockingTest {
                userRepo.register(name, email, password)
            }
        }
    }

    @Test
    fun `when logout should update user data in datastore with null token and false isLoggedin`() = testCoroutineRule.runBlockingTest {
        userRepo.logout()
        Mockito.verify(pref).updateUser(UserItems(token = null, isLoggedIn = false))
    }
}