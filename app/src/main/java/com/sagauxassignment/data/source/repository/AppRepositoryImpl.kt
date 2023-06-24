package com.sagauxassignment.data.source.repository

import android.net.Uri
import com.sagauxassignment.data.source.remote.AppDataSource
import com.sagauxassignment.domain.AppRepository
import com.sagauxassignment.domain.ResultDataState
import com.sagauxassignment.domain.User
import javax.inject.Inject

class AppRepositoryImpl @Inject constructor(
    private val appDataSource: AppDataSource
): AppRepository {

    override suspend fun signUpWithFirebase(user: User): ResultDataState<Boolean> {
        return appDataSource.signUpWithFirebase(user)
    }

    override suspend fun loginWithFirebase(
        email: String,
        password: String
    ): ResultDataState<Boolean> {
        return appDataSource.loginWithFirebase(email, password)
    }

    override suspend fun resetPasswordWithFirebase(email: String): ResultDataState<Boolean> {
        return appDataSource.resetPasswordWithFirebase(email)
    }

    override suspend fun uploadImageOnFirebase(imageUri: Uri): ResultDataState<Uri?> {
        return appDataSource.uploadImageOnFirebase(imageUri)
    }
}