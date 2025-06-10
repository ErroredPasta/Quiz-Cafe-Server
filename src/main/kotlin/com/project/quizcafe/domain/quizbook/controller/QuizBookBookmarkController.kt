package com.project.quizcafe.domain.quizbook.controller

import com.project.quizcafe.domain.auth.security.UserDetailsImpl
import com.project.quizcafe.common.response.ApiResponse
import com.project.quizcafe.common.response.ApiResponseFactory
import com.project.quizcafe.domain.quizbook.service.QuizBookBookmarkService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/quiz-book-bookmark")
@Tag(name = "quiz-book-bookmark", description = "퀴즈북 북마크 관련 API")
class QuizBookBookmarkController(
    private val quizBookBookmarkService: QuizBookBookmarkService
) {

    @PostMapping
    @Operation(summary = "퀴즈북 북마크 추가", description = "사용자가 퀴즈북을 북마크로 추가")
    fun addBookmark(@AuthenticationPrincipal principal: UserDetailsImpl, @RequestParam quizBookId: Long): ResponseEntity<ApiResponse<Long?>> {
        quizBookBookmarkService.addBookmark(principal.getUser(), quizBookId)
        return ApiResponseFactory.success(
            data = null,
            message = "퀴즈북 북마크 추가 성공",
            status = HttpStatus.CREATED
        )
    }

    @DeleteMapping
    @Operation(summary = "퀴즈북 북마크 삭제", description = "사용자가 퀴즈북 북마크를 삭제")
    fun removeBookmark(@AuthenticationPrincipal principal: UserDetailsImpl, @RequestParam quizBookId: Long): ResponseEntity<ApiResponse<Nothing?>> {
        quizBookBookmarkService.removeBookmark(principal.getUser().id, quizBookId)
        return ApiResponseFactory.success(
            data = null,
            message = "퀴즈북 북마크 삭제 성공",
            status = HttpStatus.NO_CONTENT
        )
    }

    @GetMapping
    @Operation(summary = "사용자 퀴즈북 북마크 목록 조회", description = "사용자가 북마크한 퀴즈북 목록 조회")
    fun getUserBookmarks(@AuthenticationPrincipal principal: UserDetailsImpl): ResponseEntity<ApiResponse<List<Long>?>> {
        val bookmarkIds = quizBookBookmarkService.getBookmarksByUserId(principal.getUser())
        return ApiResponseFactory.success(
            data = bookmarkIds,
            message = "사용자 북마크 목록 조회 성공",
            status = HttpStatus.OK
        )
    }
}
