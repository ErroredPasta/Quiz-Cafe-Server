package com.project.quizcafe.quizbook.controller

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/quiz-book")
@Tag(name = "QuizBook", description = "문제집 관련 API")
class QuizBookController {

    fun createQuizBook(){

    }

}