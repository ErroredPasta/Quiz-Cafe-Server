package com.project.quizcafe.domain.quizsolving.controller

import com.project.quizcafe.common.response.ApiResponse
import com.project.quizcafe.common.response.ApiResponseFactory
import com.project.quizcafe.domain.auth.security.UserDetailsImpl
import com.project.quizcafe.domain.quizsolving.dto.request.CreateQuizSolvingRequest
import com.project.quizcafe.domain.quizsolving.dto.request.CreateSingleQuizSolvingRequest
import com.project.quizcafe.domain.quizsolving.dto.request.UpdateQuizSolvingRequest
import com.project.quizcafe.domain.quizsolving.dto.response.QuizSolvingResponse
import com.project.quizcafe.domain.quizsolving.service.QuizSolvingService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/quiz-solving")
@Tag(name = "QuizSolving", description = "퀴즈 풀이 관련 API")
class QuizSolvingController(
    private val quizSolvingService: QuizSolvingService
) {

//    @PostMapping
//    @Operation(summary = "퀴즈 풀이 생성", description = "사용자가 퀴즈 풀이를 생성")
//    fun createQuizSolving(
//        @AuthenticationPrincipal principal: UserDetailsImpl,
//        @RequestBody request: CreateSingleQuizSolvingRequest
//    ): ResponseEntity<ApiResponse<Long?>> {
//        val quizSolving = quizSolvingService.createQuizSolving(request, principal.getUser())
//        return ApiResponseFactory.success(
//            data = quizSolving.id,
//            message = "퀴즈 풀이 생성 성공",
//            status = HttpStatus.CREATED
//        )
//    }

    @GetMapping("/{id}")
    @Operation(summary = "퀴즈 풀이 조회", description = "특정 퀴즈 풀이를 조회합니다.")
    fun getQuizSolving(@PathVariable id: Long): ResponseEntity<ApiResponse<QuizSolvingResponse?>> {
        val quizSolving = quizSolvingService.getQuizSolving(id)
        return ApiResponseFactory.success(
            data = quizSolving,
            message = "퀴즈 풀이 조회 성공"
        )
    }

    @PatchMapping("/{id}")
    @Operation(summary = "퀴즈 풀이 수정", description = "퀴즈 풀이를 수정합니다.")
    fun updateQuizSolving(
        @PathVariable id: Long,
        @RequestBody request: UpdateQuizSolvingRequest
    ): ResponseEntity<ApiResponse<Unit?>> {
        quizSolvingService.updateQuizSolving(id, request)
        return ApiResponseFactory.success(
            message = "퀴즈 풀이 수정 성공"
        )
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "퀴즈 풀이 삭제", description = "퀴즈 풀이를 삭제합니다.")
    fun deleteQuizSolving(@PathVariable id: Long): ResponseEntity<ApiResponse<Unit?>> {
        quizSolvingService.deleteQuizSolving(id)
        return ApiResponseFactory.success(
            message = "퀴즈 풀이 삭제 성공"
        )
    }

}
