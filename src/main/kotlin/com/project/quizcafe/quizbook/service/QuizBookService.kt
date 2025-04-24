package com.project.quizcafe.quizbook.service

import com.project.quizcafe.quizbook.dto.request.CreateQuizBookRequest
import com.project.quizcafe.quizbook.dto.request.UpdateQuizBookRequest
import com.project.quizcafe.quizbook.dto.response.GetQuizBookResponse
import com.project.quizcafe.quizbook.entity.QuizBook
import com.project.quizcafe.user.entity.User
import java.util.*

interface QuizBookService {
    fun createQuizBook(request: CreateQuizBookRequest): QuizBook
    fun getQuizBook(id: Long): Optional<QuizBook>

    fun getQuizBooksByCategory(category: String): List<GetQuizBookResponse>
    fun getMyQuizBooks(user: User): List<GetQuizBookResponse>

    fun updateQuizBook(id: Long, request: UpdateQuizBookRequest, user: User)


    fun deleteQuizBook(id: Long, user: User)
}