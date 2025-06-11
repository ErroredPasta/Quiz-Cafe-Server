package com.project.quizcafe.common.exception

import com.project.quizcafe.common.response.ApiResponseFactory
import com.project.quizcafe.common.response.ErrorResponse
import jakarta.validation.ConstraintViolationException
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    // 커스텀 ApiException 처리
    @ExceptionHandler(ApiException::class)
    fun handleApiException(e: ApiException): ResponseEntity<ErrorResponse> {
        return ApiResponseFactory.error(
            message = e.message ?: "알 수 없는 오류입니다.",
            status = HttpStatus.valueOf(e.statusCode)
        )
    }

    // HTTP 메서드 오류 (405)
    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleMethodNotAllowed(e: HttpRequestMethodNotSupportedException): ResponseEntity<ErrorResponse> {
        return ApiResponseFactory.error(
            message = "허용되지 않은 HTTP 메서드입니다.",
            status = HttpStatus.METHOD_NOT_ALLOWED
        )
    }

    // 요청 파라미터 누락 (400)
    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingParams(e: MissingServletRequestParameterException): ResponseEntity<ErrorResponse> {
        return ApiResponseFactory.error(
            message = "필수 요청 파라미터가 누락되었습니다: ${e.parameterName}",
            status = HttpStatus.BAD_REQUEST
        )
    }

    // JSON 파싱 오류 (400)
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleInvalidJson(e: HttpMessageNotReadableException): ResponseEntity<ErrorResponse> {
        return ApiResponseFactory.error(
            message = "잘못된 요청 형식입니다.",
            status = HttpStatus.BAD_REQUEST
        )
    }

    // DTO 유효성 검사 실패 (400)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(e: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val errorMessage = e.bindingResult.fieldErrors
            .joinToString(", ") { "${it.field}: ${it.defaultMessage}" }
        return ApiResponseFactory.error(
            message = "유효성 검사 실패: $errorMessage",
            status = HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(e: ConstraintViolationException): ResponseEntity<ErrorResponse> {
        val errorMessage = e.constraintViolations.joinToString(", ") { violation ->
            "${violation.propertyPath}: ${violation.message}"
        }
        return ApiResponseFactory.error(
            message = "유효성 검사 실패: $errorMessage",
            status = HttpStatus.BAD_REQUEST
        )
    }

    // 권한 없음 (403)
    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(e: AccessDeniedException): ResponseEntity<ErrorResponse> {
        return ApiResponseFactory.error(
            message = "접근 권한이 없습니다.",
            status = HttpStatus.FORBIDDEN
        )
    }

    // 데이터 무결성 위반 (409)
    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrityViolation(e: DataIntegrityViolationException): ResponseEntity<ErrorResponse> {
        return ApiResponseFactory.error(
            message = "데이터 무결성 제약 조건을 위반했습니다.",
            status = HttpStatus.CONFLICT
        )
    }


    // 알 수 없는 예외 (500)
    @ExceptionHandler(Exception::class)
    fun handleGeneralException(e: Exception): ResponseEntity<ErrorResponse> {
        log.error("Unhandled exception occurred", e)
        return ApiResponseFactory.error(
            message = "서버 오류가 발생했습니다. ${e.message}",
            status = HttpStatus.INTERNAL_SERVER_ERROR
        )
    }
}
