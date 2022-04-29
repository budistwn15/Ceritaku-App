package com.budi.setiawan.storyappbudisetiawan.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.budi.setiawan.storyappbudisetiawan.data.repository.StoryRepository
import com.budi.setiawan.storyappbudisetiawan.data.repository.UserRepository
import com.budi.setiawan.storyappbudisetiawan.injection.Injection
import com.budi.setiawan.storyappbudisetiawan.view.create.CreateViewModel
import com.budi.setiawan.storyappbudisetiawan.view.home.HomeViewModel
import com.budi.setiawan.storyappbudisetiawan.view.login.LoginViewModel
import com.budi.setiawan.storyappbudisetiawan.view.maps.MapsViewModel
import com.budi.setiawan.storyappbudisetiawan.view.register.RegisterViewModel
import com.budi.setiawan.storyappbudisetiawan.view.welcome.WelcomeViewModel

class ViewModelFactory(private val userRepository: UserRepository, private val storyRepository: StoryRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(WelcomeViewModel::class.java) -> {
                WelcomeViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(userRepository, storyRepository) as T
            }
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(userRepository, storyRepository) as T
            }
            modelClass.isAssignableFrom(CreateViewModel::class.java) -> {
                CreateViewModel(userRepository, storyRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideUserRepository(context), Injection.provideStoryRepository(context))
            }.also { instance = it }
    }
}