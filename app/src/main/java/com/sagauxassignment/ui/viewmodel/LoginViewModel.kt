package com.sagauxassignment.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sagauxassignment.domain.AppRepository
import com.sagauxassignment.domain.ResultDataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AppRepository
): ViewModel() {

    private val _showLoading = MutableLiveData<Boolean>()
    val showLoading: LiveData<Boolean> get() = _showLoading

    private val _isLoggedIn = MutableLiveData<Boolean?>()
    val isLoggedIn: LiveData<Boolean?> get() = _isLoggedIn

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun loginWithFirebase(email: String, password: String) {
        viewModelScope.launch {
            _showLoading.value = true
            when(val result = repository.loginWithFirebase(email, password)) {
                is ResultDataState.Success -> {
                    _showLoading.value = false
                    _isLoggedIn.value = result.data
                }
                is ResultDataState.ErrorMessage -> {
                    _showLoading.value = false
                    _errorMessage.value = result.errorMessage
                }
                else -> {
                    _errorMessage.value = "Unexpected error"
                }
            }
        }
    }
}