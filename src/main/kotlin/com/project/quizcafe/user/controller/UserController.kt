package com.project.quizcafe.user.controller

import com.project.quizcafe.auth.dto.request.SendMailRequest
import com.project.quizcafe.auth.dto.request.VerifyCodeRequest
import com.project.quizcafe.user.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import com.project.quizcafe.common.response.ApiResponse
import com.project.quizcafe.common.response.ApiResponseFactory
import com.project.quizcafe.user.dto.request.ChangePasswordRequest
import org.springframework.security.core.context.SecurityContextHolder

@RestController
@RequestMapping("/user")
@Tag(name = "User", description = "사용자 관련 API")
class UserController(
    private val userService: UserService
) {
    @PostMapping("change-password")
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
}
