package com.sagauxassignment.domain

import android.net.Uri

interface AppRepository {

    suspend fun signUpWithFirebase(user: User): ResultDataState<Boolean>
    suspend fun loginWithFirebase(email: String, password: String): ResultDataState<Boolean>
    suspend fun resetPasswordWithFirebase(email: String): ResultDataState<Boolean>
    suspend fun uploadImageOnFirebase(imageUri: Uri): ResultDataState<Uri?>
    suspend fun getUserList(): ResultDataState<Users>
    suspend fun logout(): ResultDataState<Boolean>
}