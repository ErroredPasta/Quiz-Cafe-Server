package com.project.quizcafe.domain.versioncontrol.dto

import com.project.quizcafe.domain.quiz.dto.response.QuizResponse
import com.project.quizcafe.domain.quizbook.entity.QuizLevel
import com.project.quizcafe.domain.user.entity.User

data class SavedQuizBook (
    val category: String,
    val title: String,
    val level: QuizLevel,
    val description: String,
    val createdBy: String,
    val quizzes: List<QuizResponse>,
)