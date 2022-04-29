package com.budi.setiawan.storyappbudisetiawan.view.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.budi.setiawan.storyappbudisetiawan.data.repository.UserRepository
import com.budi.setiawan.storyappbudisetiawan.error.AuthError
import kotlinx.coroutines.launch

class RegisterViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess: LiveData<Boolean> = _isSuccess

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                userRepository.register(name, email, password)
                _isSuccess.value = true
            } catch (e: AuthError) {
                _errorMessage.value = e.message
                _isSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }
}