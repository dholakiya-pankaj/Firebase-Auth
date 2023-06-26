package com.sagauxassignment.data.model

data class UserListResponse(
    val users: List<UserResponse>?
) {

    data class UserResponse(
        val id: String?,
        val name: String?,
        val email: String?,
        val profileUrl: String?
    )
}
