package com.project.quizcafe.domain.quiz.controller

import com.project.quizcafe.common.response.ApiResponse
import com.project.quizcafe.common.response.ApiResponseFactory
import com.project.quizcafe.domain.auth.security.UserDetailsImpl
import com.project.quizcafe.domain.quiz.dto.request.CreateMcqOptionRequest
import com.project.quizcafe.domain.quiz.dto.request.UpdateMcqOptionRequest
import com.project.quizcafe.domain.quiz.dto.response.McqOptionResponse
import com.project.quizcafe.domain.quiz.service.McqOptionService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/quiz/mcq-option")
@Tag(name = "05.McqOption", description = "객관식 보기 관련 API")
class McqOptionController(
    private val mcqOptionService: McqOptionService
) {

    @PostMapping
    @Operation(summary = "객관식 보기 생성", description = "새로운 객관식 보기를 생성합니다.")
    fun createMcqOption(
        @AuthenticationPrincipal principal: UserDetailsImpl,
        @RequestBody request: CreateMcqOptionRequest
    ): ResponseEntity<ApiResponse<Long?>> {
        val response = mcqOptionService.createMcqOption(request, principal.getUser())
        return ApiResponseFactory.success(
            data = response.id,
            message = "객관식 보기 생성 성공",
            status = HttpStatus.CREATED
        )
    }

    @PatchMapping("/{id}")
    @Operation(summary = "객관식 보기 수정", description = "특정 객관식 보기의 내용을 수정합니다.")
    fun updateMcqOption(
        @AuthenticationPrincipal principal: UserDetailsImpl,
        @PathVariable id: Long,
        @RequestBody request: UpdateMcqOptionRequest
    ): ResponseEntity<ApiResponse<Unit?>> {
        mcqOptionService.updateMcqOption(id, request, principal.getUser())
        return ApiResponseFactory.success(
            message = "객관식 보기 수정 성공",
            status = HttpStatus.OK
        )
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "객관식 보기 삭제", description = "특정 객관식 보기를 삭제합니다.")
    fun deleteMcqOption(
        @AuthenticationPrincipal principal: UserDetailsImpl,
        @PathVariable id: Long
    ): ResponseEntity<ApiResponse<Unit?>> {
        mcqOptionService.deleteMcqOption(id,principal.getUser())
        return ApiResponseFactory.success(
            message = "객관식 보기 삭제 성공",
            status = HttpStatus.NO_CONTENT
        )
    }

    @GetMapping
    @Operation(summary = "퀴즈 ID로 객관식 보기 조회", description = "특정 퀴즈에 속한 모든 객관식 보기를 조회합니다.")
    fun getMcqOptionsByQuizId(@RequestParam quizId: Long): ResponseEntity<ApiResponse<List<McqOptionResponse>?>> {
        val response = mcqOptionService.getMcqOptionsByQuizId(quizId)
        return ApiResponseFactory.success(
            data = response,
            message = "객관식 보기 조회 성공",
            status = HttpStatus.OK
        )
    }

    @GetMapping("/{id}")
    @Operation(summary = "객관식 보기 ID로 객관식 보기 조회", description = "객관식 보기 ID로 객관식 보기를 조회합니다.")
    fun getMcqOptionsById(@PathVariable id: Long): ResponseEntity<ApiResponse<McqOptionResponse?>> {
        val response = mcqOptionService.getMcqOptionsById(id)
        return ApiResponseFactory.success(
            data = response,
            message = "객관식 보기 조회 성공",
            status = HttpStatus.OK
        )
    }
}
