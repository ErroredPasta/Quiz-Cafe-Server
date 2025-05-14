package com.project.quizcafe.common.response

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

object ApiResponseFactory {

    fun <T> success(
        data: T? = null,
        message: String = "요청 성공",
        status: HttpStatus = HttpStatus.OK
    ): ResponseEntity<ApiResponse<T?>> {
        return ResponseEntity
            .status(status)
            .body(
                ApiResponse(
                    status = "success",
                    code = status.value(),
                    message = message,
                    data = data
                )
            )
    }

    fun error(
        message: String = "요청 실패",
        status: HttpStatus = HttpStatus.BAD_REQUEST
    ): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(status)
            .body(
                ErrorResponse(
                    status = "error",
                    code = status.value(),
                    message = message
                )
            )
    }
}
