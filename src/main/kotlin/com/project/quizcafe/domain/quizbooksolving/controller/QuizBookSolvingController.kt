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
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/quiz-book-solving")
@Tag(name = "07.QuizBookSolving", description = "퀴즈북 풀이 관련 API")
class QuizBookSolvingController(
    private val quizBookSolvingService: QuizBookSolvingService
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @PostMapping
    @Operation(summary = "문제집 풀이 생성", description = "사용자가 문제집 풀이를 생성합니다.")
    fun createQuizBookSolving(
        @AuthenticationPrincipal principal: UserDetailsImpl,
        @RequestBody request: CreateQuizBookSolvingRequest
    ): ResponseEntity<ApiResponse<Long?>> {
        val quizBookSolving = quizBookSolvingService.createQuizBookSolving(request, principal.getUser())
        return ApiResponseFactory.success(
            data = quizBookSolving.id,
            message = "문제집 풀이 생성 성공",
            status = HttpStatus.CREATED
        )
    }

    @PatchMapping("/{id}")
    @Operation(summary = "문제집 풀이 수정", description = "문제집 풀이를 수정합니다.")
    fun updateQuizBookSolving(
        @AuthenticationPrincipal principal: UserDetailsImpl,
        @PathVariable id: Long,
        @RequestBody request: UpdateQuizBookSolvingRequest
    ): ResponseEntity<ApiResponse<Unit?>> {
        quizBookSolvingService.updateQuizBookSolving(id, request, principal.getUser())
        return ApiResponseFactory.success(
            message = "문제집 풀이 수정 성공",
            status = HttpStatus.OK
        )
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "문제집 풀이 삭제", description = "문제집 풀이를 삭제합니다.")
    fun deleteQuizBookSolving(
        @AuthenticationPrincipal principal: UserDetailsImpl,
        @PathVariable id: Long
    ): ResponseEntity<ApiResponse<Unit?>> {
        quizBookSolvingService.deleteQuizBookSolving(id, principal.getUser())
        return ApiResponseFactory.success(
            message = "문제집 풀이 삭제 성공",
            status = HttpStatus.NO_CONTENT
        )
    }

    @GetMapping
    @Operation(summary = "특정 유저의 모든 문제집 풀이 조회", description = "특정 유저의 모든 문제집 풀이를 조회합니다.")
    fun getAllByUserId(@AuthenticationPrincipal principal: UserDetailsImpl): ResponseEntity<ApiResponse<List<QuizBookSolvingResponse>?>> {
        val responses = quizBookSolvingService.getAllByUserId(principal.getUser())

        return ApiResponseFactory.success(
            data = responses,
            message = "특정 유저의 모든 문제집 풀이 조회 성공",
            status = HttpStatus.OK
        )
    }

    @GetMapping("/{id}")
    @Operation(summary = "id에 해당하는 문제집 풀이 조회", description = "id에 해당하는 문제집 풀이를 조회합니다.")
    fun getAllByUserId(
        @PathVariable id: Long,
        @AuthenticationPrincipal principal: UserDetailsImpl
    ): ResponseEntity<ApiResponse<QuizBookSolvingResponse?>> {
        val responses = quizBookSolvingService.getQuizBookSolvingById(id)
        return ApiResponseFactory.success(
            data = responses,
            message = "문제집 풀이 조회 성공",
            status = HttpStatus.OK
        )
    }
}
