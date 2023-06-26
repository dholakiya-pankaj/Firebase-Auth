package com.sagauxassignment.data.model

import androidx.annotation.Keep

@Keep
data class UserListResponse(
    val users: List<UserResponse>?
) {
    @Keep
    data class UserResponse(
        val id: String?,
        val name: String?,
        val email: String?,
        val profileUrl: String?
    )
}
