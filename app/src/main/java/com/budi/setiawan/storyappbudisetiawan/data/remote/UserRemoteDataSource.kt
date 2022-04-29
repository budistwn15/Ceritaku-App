package com.budi.setiawan.storyappbudisetiawan.data.remote

import com.budi.setiawan.storyappbudisetiawan.api.apiinterface.UserInterface

class UserRemoteDataSource(private val userInterface: UserInterface) {

    suspend fun login(email: String, password: String) = userInterface.login(email, password)

    suspend fun register(name: String, email: String, password: String) =
        userInterface.register(name, email, password)
}