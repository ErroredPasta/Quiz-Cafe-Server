package com.project.quizcafe.common.response

data class ApiResponse<T>(
    val status: String,
    val code: Int,
    val message: String,
    val data: T?
)