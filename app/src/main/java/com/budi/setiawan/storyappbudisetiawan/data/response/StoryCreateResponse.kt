package com.budi.setiawan.storyappbudisetiawan.data.response

import com.google.gson.annotations.SerializedName

data class StoryCreateResponse(
    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)
