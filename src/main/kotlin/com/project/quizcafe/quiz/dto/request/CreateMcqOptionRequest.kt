package com.project.quizcafe.quiz.dto.request

data class CreateMcqOptionRequest(
    val quizId: Long, // 퀴즈 ID
    val optionNumber: Int, // 선택지 번호
    val optionContent: String, // 선택지 내용
    val isCorrect: Boolean // 정답 여부
)