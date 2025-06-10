package com.project.quizcafe.domain.user.controller

import com.project.quizcafe.domain.auth.security.UserDetailsImpl
import com.project.quizcafe.common.response.ApiResponse
import com.project.quizcafe.common.response.ApiResponseFactory
import com.project.quizcafe.domain.quizbook.dto.response.GetQuizBookResponse
import com.project.quizcafe.domain.quizbook.service.QuizBookService
import com.project.quizcafe.domain.user.dto.request.ChangePasswordRequest
import com.project.quizcafe.domain.user.dto.request.UpdateUserInfoRequest
import com.project.quizcafe.domain.user.dto.response.UserInfoResponse
import com.project.quizcafe.domain.user.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
@Tag(name = "User", description = "사용자 관련 API")
class UserController(
    private val userService: UserService,
    private val quizBookService: QuizBookService
) {

    @PatchMapping("password")
    @Operation(summary = "비밀번호 변경", description = "사용자가 자신의 비밀번호를 변경하는 API")
    fun changePassword(
        @AuthenticationPrincipal principal: UserDetailsImpl,
        @Valid @RequestBody request: ChangePasswordRequest
    ): ResponseEntity<ApiResponse<Unit?>> {
        userService.changePassword(principal.getUser().nickName, request.oldPassword, request.newPassword)
        return ApiResponseFactory.success(
            message = "비밀번호 변경 성공",
            status = HttpStatus.OK
        )
    }

    @GetMapping("quiz-book")
    @Operation(summary = "내 퀴즈북 조회", description = "사용자가 만든 퀴즈북 조회")
    fun getMyQuizBook(@AuthenticationPrincipal principal: UserDetailsImpl): ResponseEntity<ApiResponse<List<GetQuizBookResponse>?>> {
        val result = quizBookService.getMyQuizBooks(principal.getUser())
        return ApiResponseFactory.success(
            data = result,
            message = "문제집 조회 성공",
            status = HttpStatus.OK
        )
    }

    @DeleteMapping
    @Operation(summary = "회원 탈퇴", description = "사용자가 자신의 계정을 삭제하는 API")
    fun deleteUser(@AuthenticationPrincipal principal: UserDetailsImpl): ResponseEntity<ApiResponse<Unit?>> {
        userService.deleteUser(principal.getUser())
        return ApiResponseFactory.success(
            message = "회원 탈퇴 성공",
            status = HttpStatus.NO_CONTENT
        )
    }

    @PatchMapping
    @Operation(summary = "회원 정보 수정", description = "사용자가 자신의 정보를 수정하는 API")
    fun updateUserInfo(
        @AuthenticationPrincipal principal: UserDetailsImpl,
        @Valid @RequestBody request: UpdateUserInfoRequest
    ): ResponseEntity<ApiResponse<Unit?>> {
        userService.updateUserInfo(principal.getUser(), request)
        return ApiResponseFactory.success(
            message = "회원 정보 수정 성공",
            status = HttpStatus.OK
        )
    }

    @GetMapping
    @Operation(summary = "내 정보 조회", description = "로그인한 사용자의 정보를 조회하는 API")
    fun getUserInfo(@AuthenticationPrincipal principal: UserDetailsImpl): ResponseEntity<ApiResponse<UserInfoResponse?>> {
        val response = userService.getUserInfo(principal.getUser())
        return ApiResponseFactory.success(
            data = response,
            message = "유저 정보 조회 성공",
            status = HttpStatus.OK
        )
    }
}
