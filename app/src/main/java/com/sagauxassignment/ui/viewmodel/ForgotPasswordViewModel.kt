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
class ForgotPasswordViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {

    private val _showLoading = MutableLiveData<Boolean>()
    val showLoading: LiveData<Boolean> get() = _showLoading

    private val _isResetPassLinkSent = MutableLiveData<Boolean?>()
    val isResetPassLinkSent: LiveData<Boolean?> get() = _isResetPassLinkSent

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun resetPasswordWithFirebase(email: String) {
        viewModelScope.launch {
            _showLoading.value = true
            when(val result = repository.resetPasswordWithFirebase(email)) {
                is ResultDataState.Success -> {
                    _showLoading.value = false
                    _isResetPassLinkSent.value = result.data
                }
                is ResultDataState.ErrorMessage -> {
                    _showLoading.value = false
                    _errorMessage.value = result.errorMessage
                }
                else -> {
                    _showLoading.value = false
                    _errorMessage.value = "Unexpected error"
                }
            }
        }
    }
}