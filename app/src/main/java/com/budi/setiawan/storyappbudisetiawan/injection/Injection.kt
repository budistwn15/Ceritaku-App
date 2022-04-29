package com.budi.setiawan.storyappbudisetiawan.injection

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.budi.setiawan.storyappbudisetiawan.api.ApiConfig
import com.budi.setiawan.storyappbudisetiawan.data.database.StoryDatabase
import com.budi.setiawan.storyappbudisetiawan.data.preferences.SharedPrefUserLogin
import com.budi.setiawan.storyappbudisetiawan.data.remote.StoryRemoteDataSource
import com.budi.setiawan.storyappbudisetiawan.data.remote.UserRemoteDataSource
import com.budi.setiawan.storyappbudisetiawan.data.repository.StoryRepository
import com.budi.setiawan.storyappbudisetiawan.data.repository.UserRepository

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

object Injection {
    fun provideUserRepository(context: Context): UserRepository {
        val userApiInterface = ApiConfig.getUserApi()
        val userRemoteDataSource = UserRemoteDataSource(userApiInterface)
        return UserRepository(context, userRemoteDataSource, SharedPrefUserLogin.getInstance(context.dataStore))
    }

    fun provideStoryRepository(context: Context): StoryRepository {
        val storyApiInterface = ApiConfig.getStoryApi()
        val storyRemoteDataSource = StoryRemoteDataSource(storyApiInterface)
        return StoryRepository(storyRemoteDataSource, StoryDatabase.getDatabase(context))
    }
}