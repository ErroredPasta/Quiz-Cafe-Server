package com.project.quizcafe.domain.quiz.service

import com.project.quizcafe.domain.quiz.dto.request.CreateQuizRequest
import com.project.quizcafe.domain.quiz.dto.request.UpdateQuizRequest
import com.project.quizcafe.domain.quiz.dto.response.QuizResponse

interface QuizService {
    fun createQuiz(request: CreateQuizRequest): Long
    fun getQuizzesByQuizBookId(quizBookId: Long): List<QuizResponse>
    fun getQuizzesByQuizId(quizId: Long) : QuizResponse
    fun updateQuiz(quizId: Long, request: UpdateQuizRequest)
    fun deleteQuiz(quizId: Long)

}