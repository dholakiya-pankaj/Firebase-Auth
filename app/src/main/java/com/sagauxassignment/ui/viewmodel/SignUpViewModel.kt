package com.sagauxassignment.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sagauxassignment.domain.AppRepository
import com.sagauxassignment.domain.ResultDataState
import com.sagauxassignment.domain.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {

    private val _showLoader = MutableLiveData<Boolean>()
    val showLoader: LiveData<Boolean> get() = _showLoader

    private val _isSigneUpSuccessfully = MutableLiveData<Boolean?>()
    val isSigneUpSuccessfully: LiveData<Boolean?> get() = _isSigneUpSuccessfully

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun signUpWithFirebase(user: User) {
        viewModelScope.launch {
            _showLoader.value = true
            when (val response = repository.signUpWithFirebase(user)) {
                is ResultDataState.Success -> {
                    _showLoader.value = false
                    _isSigneUpSuccessfully.value = response.data
                }
                is ResultDataState.ErrorMessage -> {
                    _showLoader.value = false
                    _errorMessage.value = response.errorMessage
                }
                else -> {
                    _errorMessage.value = "Unexpected error."
                }
            }
        }
    }
}