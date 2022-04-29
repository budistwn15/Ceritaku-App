package com.budi.setiawan.storyappbudisetiawan.view.maps

import androidx.lifecycle.*
import com.budi.setiawan.storyappbudisetiawan.data.item.StoryItems
import com.budi.setiawan.storyappbudisetiawan.data.repository.StoryRepository
import com.budi.setiawan.storyappbudisetiawan.data.repository.UserRepository
import com.budi.setiawan.storyappbudisetiawan.error.StoryError
import kotlinx.coroutines.launch

class MapsViewModel(userRepository: UserRepository, private val storyRepository: StoryRepository) :
    ViewModel() {

    val userItems = userRepository.userItems.asLiveData()

    private val _stories = MutableLiveData<List<StoryItems>>()
    val stories: LiveData<List<StoryItems>> = _stories

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun getStoriesWithLocation(token: String) {
        viewModelScope.launch {
            try {
                val stories = storyRepository.getStoriesWithLocation(token = token)
                _stories.value = stories
            } catch (e: StoryError) {
                _errorMessage.value = e.message.toString()
            }
        }
    }

}