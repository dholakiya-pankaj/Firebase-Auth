package com.sagauxassignment.data.source.remote

import android.net.Uri
import com.sagauxassignment.domain.ResultDataState
import com.sagauxassignment.domain.User
import com.sagauxassignment.domain.Users

interface AppDataSource {

    suspend fun signUpWithFirebase(user: User): ResultDataState<Boolean>
    suspend fun loginWithFirebase(email: String, password: String): ResultDataState<Boolean>
    suspend fun resetPasswordWithFirebase(email: String): ResultDataState<Boolean>
    suspend fun uploadImageOnFirebase(imageUri: Uri): ResultDataState<Uri?>
    suspend fun getUserList(): ResultDataState<Users>
    suspend fun logout(): ResultDataState<Boolean>
}