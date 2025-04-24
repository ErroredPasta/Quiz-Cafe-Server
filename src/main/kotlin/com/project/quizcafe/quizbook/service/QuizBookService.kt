package com.project.quizcafe.quizbook.service

import com.project.quizcafe.quizbook.dto.request.CreateQuizBookRequest
import com.project.quizcafe.quizbook.entity.QuizBook
import java.util.*

interface QuizBookService {
    fun createQuizBook(request: CreateQuizBookRequest): QuizBook
    fun getQuizBook(id: Long): Optional<QuizBook>
    //fun getAllQuizBooks(): List<QuizBook>
    fun updateQuizBook(id: Long, updatedQuizBook: QuizBook): QuizBook
    fun deleteQuizBook(id: Long)
}