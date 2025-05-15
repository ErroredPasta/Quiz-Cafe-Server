package com.project.quizcafe.domain.quizsolving.dto.request

import java.time.LocalDateTime

data class CreateQuizSolvingRequest(
    val quizId: Long,
    val questionType: String,  // 'MCQ', 'SHORT_ANSWER', 'OX'
    val content: String,
    val answer: String,
    val explanation: String?,
    val memo: String?,
    val userAnswer: String?,
    val isCorrect: Boolean,
    val completedAt: LocalDateTime = LocalDateTime.now(),
    val mcqOptions: List<CreateMcqOptionSolvingRequest>?= null
)
