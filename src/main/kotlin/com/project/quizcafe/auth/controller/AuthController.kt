package com.project.quizcafe.auth.controller

import com.project.quizcafe.auth.dto.request.SignInRequest
import com.project.quizcafe.auth.dto.request.SendCodeRequest
import com.project.quizcafe.auth.dto.request.SignUpRequest
import com.project.quizcafe.auth.dto.request.VerifyCodeRequest
import com.project.quizcafe.auth.dto.request.ResetPasswordRequest
import com.project.quizcafe.auth.dto.response.TokenResponse
import com.project.quizcafe.auth.service.AuthService
import com.project.quizcafe.common.response.ApiResponse
import com.project.quizcafe.common.response.ApiResponseFactory
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PatchMapping
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

    @PostMapping("/sign-up")
    @Operation(summary = "회원가입", description = "사용자의 회원가입을 처리하는 API")
    fun signup(@RequestBody request: SignUpRequest): ResponseEntity<ApiResponse<Unit?>> {
        authService.signUp(request)
        return ApiResponseFactory.success("회원가입 성공")
    }

    @PostMapping("/sign-in")
    @Operation(summary = "로그인", description = "사용자의 로그인 및 JWT 토큰 발급 API")
    fun signIn(@RequestBody request: SignInRequest): ResponseEntity<ApiResponse<TokenResponse>> {
        val token = authService.signIn(request)
        return ApiResponseFactory.successWithData(token, "로그인 성공")
    }

    @PostMapping("/verification-code/send")
    @Operation(summary = "메일 인증 코드 전송", description = "사용자 이메일로 인증 코드를 전송하는 API")
    fun sendCode(@RequestBody request: SendCodeRequest): ResponseEntity<ApiResponse<Unit?>> {
        try {
            authService.sendCode(request.toMail)
            return ApiResponseFactory.success()
        } catch (e: Exception) {
            print(e.toString())
            return ApiResponseFactory.error("요청 실패", HttpStatus.INTERNAL_SERVER_ERROR.value())
        }
    }

    @PostMapping("/verification-code/verify")
    @Operation(summary = "메일 인증 코드 검증", description = "사용자가 입력한 메일 인증 코드의 유효성을 검증하는 API")
    fun verifyCode(@RequestBody request: VerifyCodeRequest): ResponseEntity<ApiResponse<Unit?>> {
        val isValid = authService.verifyCode(request.toMail, request.code)
        return if (isValid) {
            ApiResponseFactory.success("인증 성공")
        } else {
            ApiResponseFactory.error("인증 실패")
        }
    }

    @PostMapping("/password/reset")
    @Operation(summary = "비밀번호 재설정", description = "사용자의 비밀번호를 재설정하는 API")
    fun resetPassword(@RequestBody request: ResetPasswordRequest): ResponseEntity<ApiResponse<Unit?>> {
        val isValid = authService.resetPassword(request.email)
        return if (isValid) {
            ApiResponseFactory.success("비밀번호 재설정 성공")
        } else {
            ApiResponseFactory.error("비밀번호 재설정 실패")
        }
    }

}
