package com.sagauxassignment.domain

import androidx.annotation.Keep

@Keep
data class User(
    val username: String,
    val email: String,
    val password: String,
    val imageUrl: String? = null
)
