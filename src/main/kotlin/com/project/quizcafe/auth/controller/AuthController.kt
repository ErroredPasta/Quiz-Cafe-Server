package com.project.quizcafe.auth.controller

import com.project.quizcafe.auth.dto.request.LoginRequest
import com.project.quizcafe.auth.dto.request.SendMailRequest
import com.project.quizcafe.auth.dto.request.SignupRequest
import com.project.quizcafe.auth.dto.request.VerifyCodeRequest
import com.project.quizcafe.auth.dto.request.resetPasswordRequest
import com.project.quizcafe.auth.dto.response.TokenResponse
import com.project.quizcafe.auth.service.AuthService
import com.project.quizcafe.common.response.ApiResponse
import com.project.quizcafe.common.response.ApiResponseFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/signup")
    fun signup(@RequestBody request: SignupRequest): ResponseEntity<ApiResponse<Unit?>> {
        authService.signup(request)
        return ApiResponseFactory.success("회원가입 성공")
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<ApiResponse<TokenResponse>> {
        val token = authService.login(request)
        return ApiResponseFactory.successWithData(token, "로그인 성공")
    }

    @PostMapping("/send-mail")
    fun sendMail(@RequestBody request: SendMailRequest): ResponseEntity<ApiResponse<Unit?>> {
        try {
            authService.sendMail(request.toMail)
            return ApiResponseFactory.success()
        } catch (e: Exception) {
            print(e.toString())
            return ApiResponseFactory.error("요청 실패", HttpStatus.INTERNAL_SERVER_ERROR.value())
        }
    }

    @PostMapping("/verify-code")
    fun verifyCode(@RequestBody request: VerifyCodeRequest): ResponseEntity<ApiResponse<Unit?>> {
        val isValid = authService.verifyCode(request.toMail, request.code)
        return if (isValid) {
            ApiResponseFactory.success("인증 성공")
        } else {
            ApiResponseFactory.error("인증 실패")
        }
    }

    @PostMapping("/reset-password")
    fun resetPassword(@RequestBody request: resetPasswordRequest): ResponseEntity<ApiResponse<Unit?>> {
        val isValid = authService.resetPassword(request.email)
        return if (isValid) {
            ApiResponseFactory.success("비밀번호 재설정 성공")
        } else {
            ApiResponseFactory.error("비밀번호 재설정 실패")
        }
    }

}