package com.project.quizcafe.quiz.controller

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/quiz")
@Tag(name = "Quiz", description = "퀴즈 관련 API")
class QuizController {
}