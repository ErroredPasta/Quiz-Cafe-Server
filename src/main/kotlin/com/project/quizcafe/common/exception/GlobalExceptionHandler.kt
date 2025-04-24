package com.project.quizcafe.common.exception

import com.project.quizcafe.common.response.ApiResponse
import com.project.quizcafe.common.response.ApiResponseFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(e: IllegalArgumentException): ResponseEntity<ApiResponse<Unit?>> {
        return ApiResponseFactory.error(message = e.message ?: "잘못된 요청입니다.")
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneralException(e: Exception): ResponseEntity<ApiResponse<Unit?>> {
        print(e)
        return ApiResponseFactory.error(message = "서버 오류가 발생했습니다.")
    }
}