package com.sagauxassignment.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sagauxassignment.domain.AppRepository
import com.sagauxassignment.domain.ResultDataState
import com.sagauxassignment.domain.Users
import com.sagauxassignment.domain.error.ErrorEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserListViewModel @Inject constructor(
    private val repository: AppRepository
): ViewModel() {

    private val _showLoading = MutableLiveData<Boolean>()
    val showLoading: LiveData<Boolean> get() = _showLoading

    private val _userList = MutableLiveData<Users?>()
    val userList: LiveData<Users?> get() = _userList

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage


    fun getUserList() {
        viewModelScope.launch {
            _showLoading.value = true
            when (val result = repository.getUserList()) {
                is ResultDataState.Success -> {
                    _showLoading.value = false
                    _userList.value = result.data
                }
                is ResultDataState.ErrorResult -> {
                    _showLoading.value = false
                    handleError(result.error)
                }
                else -> {
                    _showLoading.value = false
                    _errorMessage.value = "Unexpected error"
                }
            }
        }
    }

    private fun handleError(entity: ErrorEntity) {
        when(entity) {
            is ErrorEntity.Network -> {
                _errorMessage.value = "Please check your internet connection."
            }
            is ErrorEntity.NotFound -> {
                _errorMessage.value = "Data not found."
            }
            is ErrorEntity.ServiceUnavailable -> {
                _errorMessage.value = "Sorry! Service is temporary unavailable. Please try again after sometime."
            }
            is ErrorEntity.AccessDenied -> {
                _errorMessage.value = "Access denied due to invalid parameters"
            }
            else -> {
                _errorMessage.value = "Unexpected error"
            }
        }
    }
}