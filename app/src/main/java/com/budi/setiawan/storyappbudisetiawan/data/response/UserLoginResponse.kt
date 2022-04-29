package com.budi.setiawan.storyappbudisetiawan.data.response

import com.google.gson.annotations.SerializedName

data class UserLoginResponse(

    @field:SerializedName("loginResult")
    val loginResult: UserLoginResult? = null,

    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)

data class UserLoginResult(

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("userId")
    val userId: String? = null,

    @field:SerializedName("token")
    val token: String? = null
)