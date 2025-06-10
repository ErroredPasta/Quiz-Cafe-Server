package com.project.quizcafe.domain.auth.controller

import com.project.quizcafe.common.response.ApiResponse
import com.project.quizcafe.common.response.ApiResponseFactory
import com.project.quizcafe.domain.auth.dto.request.SendCodeRequest
import com.project.quizcafe.domain.auth.dto.request.SignInRequest
import com.project.quizcafe.domain.auth.dto.request.SignUpRequest
import com.project.quizcafe.domain.auth.dto.request.VerifyCodeRequest
import com.project.quizcafe.domain.auth.dto.response.TokenResponse
import com.project.quizcafe.domain.auth.service.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "사용자 인증 관련 API")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/sign-up")
    @Operation(summary = "회원가입", description = "사용자의 회원가입을 처리하는 API")
    @ApiResponses(
        value = [
            SwaggerApiResponse(responseCode = "201", description = "회원가입 성공"),
            SwaggerApiResponse(
                responseCode = "400",
                description = "잘못된 요청 - 이메일 형식 오류 또는 비밀번호 8자리 미만",
                content = [Content(schema = Schema(hidden = true))]
            ),
            SwaggerApiResponse(
                responseCode = "409",
                description = "이미 존재하는 이메일 또는 닉네임",
                content = [Content(schema = Schema(hidden = true))]
            ),
            SwaggerApiResponse(
                responseCode = "500",
                description = "서버 오류 - 회원가입 처리 중 오류가 발생했습니다.",
                content = [Content(schema = Schema(hidden = true))]
            )
        ]
    )
    fun signup(@Valid @RequestBody request: SignUpRequest): ResponseEntity<ApiResponse<Unit?>> {
        authService.signUp(request)
        return ApiResponseFactory.success(
            message = "회원가입 성공",
            status = HttpStatus.CREATED
        )
    }

    @PostMapping("/sign-in")
    @Operation(summary = "로그인", description = "사용자의 로그인 및 JWT 토큰 발급 API")
    @ApiResponses(
        value = [
            SwaggerApiResponse(
                responseCode = "200",
                description = "로그인 성공",
                content = [Content(schema = Schema(implementation = TokenResponse::class))]
            ),
            SwaggerApiResponse(
                responseCode = "401",
                description = "인증 실패 - 존재하지 않는 이메일 또는 비밀번호 불일치",
                content = [Content(schema = Schema(hidden = true))]
            )
        ]
    )
    fun signIn(@Valid @RequestBody request: SignInRequest): ResponseEntity<ApiResponse<TokenResponse?>> {
        val token = authService.signIn(request)
        return ApiResponseFactory.success(
            data = token,
            message = "로그인 성공"
        )
    }

    @PostMapping("/verification-code/send")
    @Operation(
        summary = "메일 인증 코드 전송",
        description = "사용자가 입력한 메일 인증 코드의 유효성을 검증하는 API (SIGN_UP: 회원가입, RESET_PASSWORD: 비밀번호 재설정)"
    )
    @ApiResponses(
        value = [
            SwaggerApiResponse(responseCode = "202", description = "메일 인증 코드 전송됨"),
            SwaggerApiResponse(
                responseCode = "400",
                description = "잘못된 요청",
                content = [Content(schema = Schema(hidden = true))]
            ),
            SwaggerApiResponse(
                responseCode = "404",
                description = "이메일 없음 - \"존재하지 않는 이메일입니다.\"",
                content = [Content(schema = Schema(hidden = true))]
            ),
            SwaggerApiResponse(
                responseCode = "409",
                description = "이미 존재하는 이메일 - \"이미 존재하는 이메일입니다.\"",
                content = [Content(schema = Schema(hidden = true))]
            ),
            SwaggerApiResponse(
                responseCode = "500",
                description = "서버 오류 - 인증 코드 저장 또는 이메일 전송 중 오류",
                content = [Content(schema = Schema(hidden = true))]
            )
        ]
    )
    fun sendCode(@Valid @RequestBody request: SendCodeRequest): ResponseEntity<ApiResponse<Unit?>> {
        authService.sendCode(request)
        return ApiResponseFactory.success(
            message = "메일 인증 코드 전송됨",
            status = HttpStatus.ACCEPTED
        )
    }

    @PostMapping("/verification-code/verify")
    @Operation(summary = "메일 인증 코드 검증", description = "사용자가 입력한 메일 인증 코드의 유효성을 검증하는 API")
    @ApiResponses(
        value = [
            SwaggerApiResponse(responseCode = "200", description = "인증 성공"),
            SwaggerApiResponse(
                responseCode = "400",
                description = "인증 코드 불일치 또는 만료",
                content = [Content(schema = Schema(hidden = true))]
            ),
            SwaggerApiResponse(
                responseCode = "404",
                description = "인증 코드 없음 - \"등록된 인증 코드가 없습니다.\"",
                content = [Content(schema = Schema(hidden = true))]
            )
        ]
    )
    fun verifyCode(@Valid @RequestBody request: VerifyCodeRequest): ResponseEntity<ApiResponse<Unit?>> {
        authService.verifyCode(request.toMail, request.code)
        return ApiResponseFactory.success(
            message = "인증 성공"
        )
    }

    @PostMapping("/password/reset")
    @Operation(summary = "비밀번호 재설정", description = "사용자의 비밀번호를 재설정하는 API")
    @ApiResponses(
        value = [
            SwaggerApiResponse(responseCode = "200", description = "비밀번호 재설정 성공"),
            SwaggerApiResponse(
                responseCode = "404",
                description = "사용자 없음 - \"사용자를 찾을 수 없습니다.\"",
                content = [Content(schema = Schema(hidden = true))]
            ),
            SwaggerApiResponse(
                responseCode = "500",
                description = "서버 오류 - 이메일 전송 또는 비밀번호 저장 중 오류",
                content = [Content(schema = Schema(hidden = true))]
            )
        ]
    )
    fun resetPassword(
        @RequestParam
        @Email(message = "올바른 이메일 형식이어야 합니다.")
        @NotBlank(message = "이메일을 입력하세요.")
        email: String
    ): ResponseEntity<ApiResponse<Unit?>> {
        authService.resetPassword(email)
        return ApiResponseFactory.success(
            message = "비밀번호 재설정 성공"
        )
    }
}
