package com.project.quizcafe.domain.quizbooksolving.controller

import com.project.quizcafe.common.response.ApiResponse
import com.project.quizcafe.common.response.ApiResponseFactory
import com.project.quizcafe.domain.auth.security.UserDetailsImpl
import com.project.quizcafe.domain.quizbooksolving.dto.request.CreateQuizBookSolvingRequest
import com.project.quizcafe.domain.quizbooksolving.dto.request.UpdateQuizBookSolvingRequest
import com.project.quizcafe.domain.quizbooksolving.dto.response.QuizBookSolvingResponse
import com.project.quizcafe.domain.quizbooksolving.service.QuizBookSolvingService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/quiz-book-solving")
@Tag(name = "QuizBookSolving", description = "퀴즈북 풀이 관련 API")
class QuizBookSolvingController(
    private val quizBookSolvingService: QuizBookSolvingService
) {

    @PostMapping
    @Operation(summary = "문제집 풀이 생성", description = "사용자가 문제집 풀이를 생성합니다.")
    fun createQuizBookSolving(
        @AuthenticationPrincipal principal: UserDetailsImpl,
        @RequestBody request: CreateQuizBookSolvingRequest
    ): ResponseEntity<ApiResponse<Long?>> {
        val quizBookSolving = quizBookSolvingService.createQuizBookSolving(request)
        return ApiResponseFactory.success(
            data = quizBookSolving.id,
            message = "문제집 풀이 생성 성공",
            status = HttpStatus.CREATED
        )
    }

    @PatchMapping("/{id}")
    @Operation(summary = "문제집 풀이 수정", description = "문제집 풀이를 수정합니다.")
    fun updateQuizBookSolving(
        @PathVariable id: Long,
        @RequestBody request: UpdateQuizBookSolvingRequest
    ): ResponseEntity<ApiResponse<Unit?>> {
        quizBookSolvingService.updateQuizBookSolving(id, request)
        return ApiResponseFactory.success(
            message = "문제집 풀이 수정 성공"
        )
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "문제집 풀이 삭제", description = "문제집 풀이를 삭제합니다.")
    fun deleteQuizBookSolving(@PathVariable id: Long): ResponseEntity<ApiResponse<Unit?>> {
        quizBookSolvingService.deleteQuizBookSolving(id)
        return ApiResponseFactory.success(
            message = "문제집 풀이 삭제 성공"
        )
    }

    @GetMapping
    @Operation(summary = "특정 유저의 모든 문제집 풀이 조회", description = "특정 유저의 모든 문제집 풀이를 조회합니다.")
    fun getAllByUserId(@AuthenticationPrincipal principal: UserDetailsImpl): ResponseEntity<ApiResponse<List<QuizBookSolvingResponse>?>> {
        val responses = quizBookSolvingService.getAllByUserId(principal.getUser().id)
        return ApiResponseFactory.success(
            data = responses,
            message = "특정 유저의 모든 문제집 풀이 조회 성공"
        )
    }
}
