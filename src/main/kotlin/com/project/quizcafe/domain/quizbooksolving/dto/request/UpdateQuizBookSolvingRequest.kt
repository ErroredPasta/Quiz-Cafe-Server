package com.project.quizcafe.domain.quizbooksolving.dto.request

import com.project.quizcafe.domain.quizbook.entity.QuizLevel
import com.project.quizcafe.domain.quizsolving.dto.request.CreateQuizSolvingRequest
import java.time.LocalDateTime

data class UpdateQuizBookSolvingRequest (
    val correctCount: Int? = null,
    val solvingTime: Long?,
    val completedAt: LocalDateTime? = LocalDateTime.now()
)