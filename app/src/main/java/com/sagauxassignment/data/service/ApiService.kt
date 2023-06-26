package com.sagauxassignment.data.service

import com.sagauxassignment.data.model.UserListResponse
import retrofit2.http.GET

interface ApiService {

    @GET
    suspend fun getUsersList(): UserListResponse
}