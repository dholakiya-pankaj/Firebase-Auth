package com.sagauxassignment.domain.error

interface ErrorHandler {
    fun getError(throwable: Throwable?): ErrorEntity
}