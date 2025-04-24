package com.project.quizcafe.quizbook.controller

import com.project.quizcafe.common.response.ApiResponse
import com.project.quizcafe.common.response.ApiResponseFactory
import com.project.quizcafe.quizbook.dto.request.CreateQuizBookRequest
import com.project.quizcafe.quizbook.service.QuizBookService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/quiz-book")
@Tag(name = "QuizBook", description = "문제집 관련 API")
class QuizBookController(
    private val quizBookService: QuizBookService
) {

    @PostMapping
    fun createQuizBook(@RequestBody request: CreateQuizBookRequest): ResponseEntity<ApiResponse<Unit?>>{
        val createQuizBookRequest = quizBookService.createQuizBook(request)
        return ApiResponseFactory.success("문제집 생성 성공")
    }



}