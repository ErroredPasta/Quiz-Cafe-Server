package com.project.quizcafe.common.exception

import com.project.quizcafe.common.response.ApiResponseFactory
import com.project.quizcafe.common.response.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(ApiException::class)
    fun handleApiException(e: ApiException): ResponseEntity<ErrorResponse> {
        // ApiException을 상속받은 예외들에 대해 처리
        return ApiResponseFactory.error(
            message = e.message ?: "알 수 없는 오류입니다.",
            status = HttpStatus.valueOf(e.statusCode)
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneralException(e: Exception): ResponseEntity<ErrorResponse> {
        // 일반적인 예외 처리
        print(e)
        return ApiResponseFactory.error(
            message = "서버 오류가 발생했습니다."
        )
    }
}
