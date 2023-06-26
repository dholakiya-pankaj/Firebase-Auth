package com.sagauxassignment.ui.viewmodel

import android.net.Uri
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
class ProfileViewModel @Inject constructor(
    private val repository: AppRepository
): ViewModel() {

    private val _showLoading = MutableLiveData<Boolean>()
    val showLoading: LiveData<Boolean> get() = _showLoading

    private val _isProfileImageUploaded = MutableLiveData<Uri?>()
    val isProfileImageUploaded: LiveData<Uri?> get() = _isProfileImageUploaded

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun uploadProfileImage(imageUri: Uri) {
        viewModelScope.launch {
            _showLoading.value = true
            when(val result = repository.uploadImageOnFirebase(imageUri)) {
                is ResultDataState.Success -> {
                    _showLoading.value = false
                    _isProfileImageUploaded.value = result.data
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