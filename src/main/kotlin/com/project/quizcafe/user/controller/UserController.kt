package com.project.quizcafe.user.controller

import com.project.quizcafe.auth.security.UserDetailsImpl
import com.project.quizcafe.user.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import com.project.quizcafe.common.response.ApiResponse
import com.project.quizcafe.common.response.ApiResponseFactory
import com.project.quizcafe.quizbook.dto.response.GetQuizBookResponse
import com.project.quizcafe.quizbook.service.QuizBookService
import com.project.quizcafe.user.dto.request.ChangePasswordRequest
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping

@RestController
@RequestMapping("/user")
@Tag(name = "User", description = "사용자 관련 API")
class UserController(
    private val userService: UserService,
    private val quizBookService: QuizBookService
) {
    @PatchMapping("password")
    @Operation(summary = "비밀번호 변경", description = "사용자가 자신의 비밀번호를 변경하는 API")
    fun changePassword(@RequestBody request: ChangePasswordRequest): ResponseEntity<ApiResponse<Unit?>> {
        val username = SecurityContextHolder.getContext().authentication.name
        val isPasswordChanged = userService.changePassword(username, request.oldPassword, request.newPassword)

        return if (isPasswordChanged) {
            ApiResponseFactory.success("비밀번호 변경 성공")
        } else {
            ApiResponseFactory.error("비밀번호 변경 실패")
        }
    }

    @GetMapping("quiz-book")
    @Operation(summary = "내 퀴즈북 조회", description = "사용자가 만든 퀴즈북 조회")
    fun getMyQuizBook(@AuthenticationPrincipal principal: UserDetailsImpl): ResponseEntity<ApiResponse<List<GetQuizBookResponse>>> {
        val result = quizBookService.getMyQuizBooks(principal.getUser())
        return ApiResponseFactory.successWithData(result,"문제집 조회 성공")
    }
}
