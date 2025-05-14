package com.project.quizcafe.domain.quizbook.controller

import com.project.quizcafe.domain.auth.security.UserDetailsImpl
import com.project.quizcafe.common.response.ApiResponse
import com.project.quizcafe.common.response.ApiResponseFactory
import com.project.quizcafe.domain.quizbook.dto.request.CreateQuizBookRequest
import com.project.quizcafe.domain.quizbook.dto.request.UpdateQuizBookRequest
import com.project.quizcafe.domain.quizbook.dto.response.GetAllCategoriesResponse
import com.project.quizcafe.domain.quizbook.dto.response.GetQuizBookResponse
import com.project.quizcafe.domain.quizbook.entity.QuizCategory
import com.project.quizcafe.domain.quizbook.service.QuizBookService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/quiz-book")
@Tag(name = "QuizBook", description = "문제집 관련 API")
class QuizBookController(
    private val quizBookService: QuizBookService
) {

    @PostMapping
    @Operation(summary = "퀴즈북 만들기", description = "사용자가 퀴즈북 생성")
    fun createQuizBook(@RequestBody request: CreateQuizBookRequest): ResponseEntity<ApiResponse<Long?>> {
        val createdQuizBook = quizBookService.createQuizBook(request)
        return ApiResponseFactory.success(
            data = createdQuizBook.id,
            message = "문제집 생성 성공",
            status = HttpStatus.CREATED
        )
    }

    @GetMapping
    @Operation(summary = "카테고리로 퀴즈북 조회", description = "카테고리에 해당하는 퀴즈북 조회")
    fun getQuizBooksByCategoryOfMine(
        @RequestParam category: String
    ): ResponseEntity<ApiResponse<List<GetQuizBookResponse>?>> {

        val result = quizBookService.getQuizBooksByCategory(category)

        return ApiResponseFactory.success(
            data = result,
            message = "문제집 조회 성공"
        )
    }

    @PatchMapping("/{quizBookId}")
    @Operation(summary = "퀴즈북 업데이트", description = "퀴즈북 id에 해당하는 퀴즈북 업데이트")
    fun updateQuizBook(
        @PathVariable quizBookId: Long,
        @RequestBody request: UpdateQuizBookRequest,
        @AuthenticationPrincipal principal: UserDetailsImpl
    ): ResponseEntity<ApiResponse<Unit?>> {
        quizBookService.updateQuizBook(quizBookId, request, principal.getUser())
        return ApiResponseFactory.success(
            message = "문제집 수정 완료"
        )
    }

    @DeleteMapping("/{quizBookId}")
    @Operation(summary = "퀴즈북 삭제", description = "퀴즈북 id에 해당하는 퀴즈북 삭제")
    fun deleteQuizBook(
        @PathVariable quizBookId: Long,
        @AuthenticationPrincipal principal: UserDetailsImpl
    ): ResponseEntity<ApiResponse<Unit?>> {
        quizBookService.deleteQuizBook(quizBookId, principal.getUser())
        return ApiResponseFactory.success(
            message = "문제집 삭제 성공"
        )
    }

    @GetMapping("/category")
    @Operation(summary = "모든 카테고리 조회", description = "모든 카테고리 조회")
    fun getAllCategories(): ResponseEntity<ApiResponse<List<GetAllCategoriesResponse>?>> {
        // Category enum을 DTO로 변환
        val categories = QuizCategory.entries.map { category ->
            GetAllCategoriesResponse(
                category = category.name,
                name = category.categoryName,
                group = category.group
            )
        }

        return ApiResponseFactory.success(
            data = categories,
            message = "카테고리 조회 성공",
            status = HttpStatus.OK
        )
    }

}