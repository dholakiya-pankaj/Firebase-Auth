package com.sagauxassignment.data.source.remote

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.sagauxassignment.data.mapper.toDomain
import com.sagauxassignment.data.service.ApiService
import com.sagauxassignment.domain.ResultDataState
import com.sagauxassignment.domain.User
import com.sagauxassignment.domain.Users
import com.sagauxassignment.domain.error.ErrorEntity
import com.sagauxassignment.domain.error.ErrorHandler
import com.sagauxassignment.util.AppConstants.CHILD_PROFILE_IMAGE_URL
import com.sagauxassignment.util.AppConstants.IMAGE_EXTENSION
import com.sagauxassignment.util.AppConstants.REFERENCE_PROFILE_IMAGES
import com.sagauxassignment.util.AppConstants.REFERENCE_USERS
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AppDataSourceImpl @Inject constructor(
    private val apiService: ApiService,
    private val firebaseAuth: FirebaseAuth,
    private val firebaseDatabase: FirebaseDatabase,
    private val firebaseStorage: FirebaseStorage,
    private val errorHandler: ErrorHandler
) : AppDataSource {

    override suspend fun signUpWithFirebase(user: User): ResultDataState<Boolean> {
        return try {
            firebaseAuth.createUserWithEmailAndPassword(
                user.email, user.password
            ).await()
            addLoginDetailsToFirebase(user)
        } catch (e: Exception) {
            ResultDataState.ErrorMessage(e.message.toString())
        }
    }

    private suspend fun addLoginDetailsToFirebase(user: User): ResultDataState<Boolean> {
        return try {
            firebaseDatabase.getReference(REFERENCE_USERS)
                .child(firebaseAuth.currentUser?.uid.toString())
                .setValue(user).await()
            ResultDataState.Success(true)
        } catch (e: Exception) {
            ResultDataState.ErrorMessage(e.message.toString())
        }
    }

    override suspend fun loginWithFirebase(
        email: String,
        password: String
    ): ResultDataState<Boolean> {
        return try {
            firebaseAuth.signInWithEmailAndPassword(
                email, password
            ).await()
            ResultDataState.Success(true)
        } catch (e: Exception) {
            ResultDataState.ErrorMessage(e.message.toString())
        }
    }

    override suspend fun resetPasswordWithFirebase(email: String): ResultDataState<Boolean> {
        return try {
            firebaseAuth.sendPasswordResetEmail(
                email
            ).await()
            ResultDataState.Success(true)
        } catch (e: Exception) {
            ResultDataState.ErrorMessage(e.message.toString())
        }
    }

    override suspend fun uploadImageOnFirebase(imageUri: Uri): ResultDataState<Uri?> {
        return try {
            val storageRef = firebaseStorage.getReference(REFERENCE_PROFILE_IMAGES)
                .child(firebaseAuth.currentUser?.uid.toString())
                .child(IMAGE_EXTENSION)
                .putFile(imageUri)
            storageRef.await()
            val downloadedUri = storageRef.snapshot.storage.downloadUrl.await()
            updateProfileImageUrl(downloadedUri)
            ResultDataState.Success(downloadedUri)
        } catch (e: Exception) {
            ResultDataState.ErrorMessage(e.message.toString())
        }
    }

    private fun updateProfileImageUrl(downloadedUri: Uri) {
        firebaseDatabase.getReference(REFERENCE_USERS)
            .child(firebaseAuth.currentUser?.uid.toString() + "/" + CHILD_PROFILE_IMAGE_URL)
            .setValue(downloadedUri.toString())
    }

    override suspend fun getUserList(): ResultDataState<Users> {
        runCatching {
            apiService.getUsersList()
        }.onSuccess {
            return ResultDataState.Success(it.toDomain())
        }.onFailure {
            return ResultDataState.ErrorResult(errorHandler.getError(it))
        }

        return ResultDataState.ErrorResult(ErrorEntity.Unknown)
    }
}