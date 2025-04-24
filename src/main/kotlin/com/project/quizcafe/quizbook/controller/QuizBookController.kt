package com.project.quizcafe.quizbook.controller

import com.project.quizcafe.auth.security.UserDetailsImpl
import com.project.quizcafe.common.response.ApiResponse
import com.project.quizcafe.common.response.ApiResponseFactory
import com.project.quizcafe.quizbook.dto.request.CreateQuizBookRequest
import com.project.quizcafe.quizbook.dto.request.UpdateQuizBookRequest
import com.project.quizcafe.quizbook.dto.response.GetQuizBookResponse
import com.project.quizcafe.quizbook.service.QuizBookService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/quiz-book")
@Tag(name = "QuizBook", description = "문제집 관련 API")
class QuizBookController(
    private val quizBookService: QuizBookService
) {

    @PostMapping
    @Operation(summary = "퀴즈북 만들기", description = "사용자가 퀴즈북 생성")
    fun createQuizBook(@RequestBody request: CreateQuizBookRequest): ResponseEntity<ApiResponse<Unit?>> {
        val createQuizBookRequest = quizBookService.createQuizBook(request)
        return ApiResponseFactory.success("문제집 생성 성공")
    }

    @GetMapping
    @Operation(summary = "카테고리로 퀴즈북 조회", description = "카테고리에 해당하는 퀴즈북 조회")
    fun getQuizBooksByCategoryOfMine(
        @RequestParam category: String
    ): ResponseEntity<ApiResponse<List<GetQuizBookResponse>>> {

        val result = quizBookService.getQuizBooksByCategory(category)

        return ApiResponseFactory.successWithData(result, "문제집 조회 성공")
    }

    @PatchMapping("/{quizBookId}")
    @Operation(summary = "퀴즈북 업데이트", description = "퀴즈북 id에 해당하는 퀴즈북 업데이트")
    fun updateQuizBook(
        @PathVariable quizBookId: Long,
        @RequestBody request: UpdateQuizBookRequest,
        @AuthenticationPrincipal principal: UserDetailsImpl
    ): ResponseEntity<ApiResponse<Unit?>> {
        quizBookService.updateQuizBook(quizBookId, request, principal.getUser())
        return ApiResponseFactory.success("문제집 수정 완료")
    }

    @DeleteMapping("/{quizBookId}")
    @Operation(summary = "퀴즈북 삭제", description = "퀴즈북 id에 해당하는 퀴즈북 삭제")
    fun deleteQuizBook(
        @PathVariable quizBookId: Long,
        @AuthenticationPrincipal principal: UserDetailsImpl
    ): ResponseEntity<ApiResponse<Unit?>> {
        quizBookService.deleteQuizBook(quizBookId, principal.getUser())
        return ApiResponseFactory.success("문제집 삭제 성공")
    }

}