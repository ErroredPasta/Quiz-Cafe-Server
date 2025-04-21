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
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "사용자 인증 관련 API")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "사용자의 회원가입을 처리하는 API")
    fun signup(@RequestBody request: SignupRequest): ResponseEntity<ApiResponse<Unit?>> {
        authService.signup(request)
        return ApiResponseFactory.success("회원가입 성공")
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "사용자의 로그인 및 JWT 토큰 발급 API")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<ApiResponse<TokenResponse>> {
        val token = authService.login(request)
        return ApiResponseFactory.successWithData(token, "로그인 성공")
    }

    @PostMapping("/send-mail")
    @Operation(summary = "메일 인증 코드 전송", description = "사용자 이메일로 인증 코드를 전송하는 API")
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
    @Operation(summary = "메일 인증 코드 검증", description = "사용자가 입력한 메일 인증 코드의 유효성을 검증하는 API")
    fun verifyCode(@RequestBody request: VerifyCodeRequest): ResponseEntity<ApiResponse<Unit?>> {
        val isValid = authService.verifyCode(request.toMail, request.code)
        return if (isValid) {
            ApiResponseFactory.success("인증 성공")
        } else {
            ApiResponseFactory.error("인증 실패")
        }
    }

    @PostMapping("/reset-password")
    @Operation(summary = "비밀번호 재설정", description = "사용자의 비밀번호를 재설정하는 API")
    fun resetPassword(@RequestBody request: resetPasswordRequest): ResponseEntity<ApiResponse<Unit?>> {
        val isValid = authService.resetPassword(request.email)
        return if (isValid) {
            ApiResponseFactory.success("비밀번호 재설정 성공")
        } else {
            ApiResponseFactory.error("비밀번호 재설정 실패")
        }
    }

}
