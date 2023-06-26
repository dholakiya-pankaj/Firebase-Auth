package com.sagauxassignment.domain

data class Users(
    val usersList: List<ApiUser>?
) {
    data class ApiUser(
        val id: String?,
        val name: String?,
        val email: String?,
        val profileUrl: String?
    )
}
