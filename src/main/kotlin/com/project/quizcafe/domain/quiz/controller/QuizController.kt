package com.project.quizcafe.domain.quiz.controller

import com.project.quizcafe.common.response.ApiResponse
import com.project.quizcafe.common.response.ApiResponseFactory
import com.project.quizcafe.domain.quiz.dto.request.CreateQuizRequest
import com.project.quizcafe.domain.quiz.dto.request.UpdateQuizRequest
import com.project.quizcafe.domain.quiz.dto.response.QuizResponse
import com.project.quizcafe.domain.quiz.service.QuizServiceImpl
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/quiz")
@Tag(name = "Quiz", description = "퀴즈 관련 API")
class QuizController(
    private val quizService: QuizServiceImpl
) {

    @PostMapping
    @Operation(summary = "퀴즈 생성", description = "사용자가 퀴즈 생성")
    fun createQuiz(@RequestBody request: CreateQuizRequest): ResponseEntity<ApiResponse<Long?>> {
        val quizId = quizService.createQuiz(request)
        return ApiResponseFactory.success(
            data = quizId,
            message = "문제집 생성 성공",
            status = HttpStatus.CREATED
        )
    }


    @GetMapping
    @Operation(summary = "퀴즈북 ID로 퀴즈 목록 조회", description = "특정 퀴즈북에 속한 모든 퀴즈를 조회합니다.")
    fun getQuizzesByQuizBookId(@RequestParam quizBookId: Long): ResponseEntity<ApiResponse<List<QuizResponse>?>> {
        val quizzes = quizService.getQuizzesByQuizBookId(quizBookId)
        return ApiResponseFactory.success(
            data = quizzes,
            message = "퀴즈 조회 성공"
        )
    }

    @GetMapping("/{quizId}")
    @Operation(summary = "퀴즈 ID로 퀴즈 조회", description = "퀴즈 ID로 퀴즈를 조회합니다.")
    fun getQuizzesByQuizId(@RequestParam quizId: Long): ResponseEntity<ApiResponse<QuizResponse?>> {
        val quiz = quizService.getQuizzesByQuizId(quizId)
        return ApiResponseFactory.success(
            data = quiz,
            message = "퀴즈 조회 성공"
        )
    }

    @PatchMapping("/{quizId}")
    @Operation(summary = "퀴즈 수정", description = "퀴즈 ID로 수정")
    fun updateQuiz(
        @PathVariable quizId: Long,
        @RequestBody request: UpdateQuizRequest
    ): ResponseEntity<ApiResponse<Unit?>> {
        quizService.updateQuiz(quizId, request)
        return ApiResponseFactory.success(
            message = "퀴즈 수정 성공"
        )
    }

    @DeleteMapping("/{quizId}")
    @Operation(summary = "퀴즈 삭제", description = "퀴즈 삭제")
    fun deleteQuiz(@PathVariable quizId: Long): ResponseEntity<ApiResponse<Unit?>> {
        quizService.deleteQuiz(quizId)
        return ApiResponseFactory.success(
            message = "퀴즈 삭제 성공"
        )
    }
}