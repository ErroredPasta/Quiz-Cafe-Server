package com.project.quizcafe.quiz.service

import com.project.quizcafe.quiz.dto.request.CreateQuizRequest
import com.project.quizcafe.quiz.dto.request.UpdateQuizRequest
import com.project.quizcafe.quiz.dto.response.QuizResponse

interface QuizService {
    fun createQuiz(request: CreateQuizRequest): Long
    fun getQuizzesByQuizBookId(quizBookId: Long): List<QuizResponse>
    fun updateQuiz(quizId: Long, request: UpdateQuizRequest)
    fun deleteQuiz(quizId: Long)

}