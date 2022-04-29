package com.budi.setiawan.storyappbudisetiawan.view.create

import androidx.lifecycle.*
import com.budi.setiawan.storyappbudisetiawan.data.repository.StoryRepository
import com.budi.setiawan.storyappbudisetiawan.data.repository.UserRepository
import com.budi.setiawan.storyappbudisetiawan.error.StoryError
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class CreateViewModel(userRepository: UserRepository, private val storyRepository: StoryRepository) : ViewModel() {

    val userItem = userRepository.userItems.asLiveData()

    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess: LiveData<Boolean> = _isSuccess

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun uploadImage(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        latLng: LatLng? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                storyRepository.postStory(token, file, description, latLng)
                _isSuccess.value = true
            } catch (e: StoryError) {
                _errorMessage.value = e.message
                _isSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }
}