package com.budi.setiawan.storyappbudisetiawan.api.apiinterface

import com.budi.setiawan.storyappbudisetiawan.data.response.RegisterResponse
import com.budi.setiawan.storyappbudisetiawan.data.response.UserLoginResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface UserInterface {

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<UserLoginResponse>

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<RegisterResponse>
}