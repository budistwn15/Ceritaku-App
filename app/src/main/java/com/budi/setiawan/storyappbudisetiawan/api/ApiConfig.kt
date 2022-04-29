package com.budi.setiawan.storyappbudisetiawan.api

import androidx.viewbinding.BuildConfig
import com.budi.setiawan.storyappbudisetiawan.api.apiinterface.StoryInterface
import com.budi.setiawan.storyappbudisetiawan.api.apiinterface.UserInterface
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConfig {
    private val loggingInterceptor = if (BuildConfig.DEBUG) {
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    } else {
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://story-api.dicoding.dev/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    fun getUserApi(): UserInterface {
        return retrofit.create(UserInterface::class.java)
    }

    fun getStoryApi(): StoryInterface {
        return retrofit.create(StoryInterface::class.java)
    }
}