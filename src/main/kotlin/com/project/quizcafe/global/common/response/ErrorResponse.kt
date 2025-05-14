package com.project.quizcafe.common.response

data class ErrorResponse(
    val status: String,
    val code: Int,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)