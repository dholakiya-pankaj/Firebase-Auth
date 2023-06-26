package com.sagauxassignment.domain

import androidx.annotation.Keep

@Keep
data class Users(
    val usersList: List<ApiUser>?
) {
    @Keep
    data class ApiUser(
        val id: String?,
        val name: String?,
        val email: String?,
        val profileUrl: String?
    )
}
