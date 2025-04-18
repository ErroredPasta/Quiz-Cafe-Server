package com.project.quizcafe.common.response

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

object ApiResponseFactory {
    fun success(message: String = "요청 성공", code: Int = HttpStatus.OK.value()): ResponseEntity<ApiResponse<Unit?>> {
        return ResponseEntity.ok(
            ApiResponse(
                status = "success",
                code = code,
                message = message,
                data = null
            )
        )
    }

    fun <T> successWithData(data: T, message: String = "요청 성공", code: Int = HttpStatus.OK.value()): ResponseEntity<ApiResponse<T>> {
        return ResponseEntity.ok(
            ApiResponse(
                status = "success",
                code = code,
                message = message,
                data = data
            )
        )
    }

    fun error(message: String = "요청 실패", code: Int = HttpStatus.BAD_REQUEST.value()): ResponseEntity<ApiResponse<Unit?>> {
        return ResponseEntity
            .status(code)
            .body(
                ApiResponse(
                    status = "error",
                    code = code,
                    message = message,
                    data = null
                )
            )
    }
}