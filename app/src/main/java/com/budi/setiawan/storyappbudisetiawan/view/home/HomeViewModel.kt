package com.budi.setiawan.storyappbudisetiawan.view.home

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.budi.setiawan.storyappbudisetiawan.data.item.StoryItems
import com.budi.setiawan.storyappbudisetiawan.data.repository.StoryRepository
import com.budi.setiawan.storyappbudisetiawan.data.repository.UserRepository
import com.budi.setiawan.storyappbudisetiawan.error.AuthError
import kotlinx.coroutines.launch

class HomeViewModel(
    private val userRepository: UserRepository,
    private val storyRepository: StoryRepository
) : ViewModel() {

    val userItems = userRepository.userItems.asLiveData()
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    val stories: LiveData<PagingData<StoryItems>> =
        userItems.switchMap {
            storyRepository.getStories(
                it.token ?: ""
            ).cachedIn(viewModelScope)
        }

    fun logout() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                userRepository.logout()
            } catch (e: AuthError) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}