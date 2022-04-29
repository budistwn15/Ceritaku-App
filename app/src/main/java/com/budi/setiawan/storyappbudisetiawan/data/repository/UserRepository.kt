package com.budi.setiawan.storyappbudisetiawan.data.repository

import android.content.Context
import com.budi.setiawan.storyappbudisetiawan.R
import com.budi.setiawan.storyappbudisetiawan.data.item.UserItems
import com.budi.setiawan.storyappbudisetiawan.data.preferences.SharedPrefUserLogin
import com.budi.setiawan.storyappbudisetiawan.data.remote.UserRemoteDataSource
import com.budi.setiawan.storyappbudisetiawan.error.AuthError

open class UserRepository(
    private val context: Context,
    private val userRemote: UserRemoteDataSource,
    private val prefs: SharedPrefUserLogin
) {

    val userItems = prefs.getUser()

    suspend fun login(email: String, password: String) {
        try {
            val responses = userRemote.login(email, password)
            if (responses.isSuccessful && responses.body() != null) {
                val result = responses.body()?.loginResult
                result?.let {
                    val user = UserItems(
                        id = it.userId,
                        email = email,
                        name = it.name,
                        password = password,
                        token = it.token,
                        isLoggedIn = true
                    )
                    prefs.updateUser(user)
                }
            } else {
                throw AuthError(context.getString(R.string.error_login))
            }
        } catch (e: Throwable) {
            throw AuthError(e.message.toString())
        }
    }

    suspend fun register(name: String, email: String, password: String) {
        try {
            val responses = userRemote.register(name, email, password)
            if (!responses.isSuccessful) {
                throw AuthError(
                    responses.body()?.message ?: context.getString(R.string.signup_error)
                )
            }
        } catch (e: Throwable) {
            throw AuthError(e.message.toString())
        }
    }

    suspend fun logout() {
        try {
            prefs.updateUser(UserItems(token = null, isLoggedIn = false))
        } catch (e: Throwable) {
            throw AuthError(e.message.toString())
        }
    }
}