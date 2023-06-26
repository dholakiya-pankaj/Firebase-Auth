package com.sagauxassignment.data.mapper

import com.sagauxassignment.data.model.UserListResponse
import com.sagauxassignment.domain.Users

fun UserListResponse.toDomain() = Users(
    users?.map {
        it.toDomain()
    }
)

fun UserListResponse.UserResponse.toDomain() = Users.ApiUser(
    id, name, email, profileUrl
)