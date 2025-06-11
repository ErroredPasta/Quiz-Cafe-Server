package com.project.quizcafe.domain.quizbook.extensions

import com.project.quizcafe.domain.quiz.entity.Quiz
import com.project.quizcafe.domain.quizbook.entity.QuizBook
import com.project.quizcafe.domain.quizbook.dto.request.CreateQuizBookRequest
import com.project.quizcafe.domain.quizbook.dto.request.UpdateQuizBookRequest
import com.project.quizcafe.domain.quizbook.dto.response.GetQuizBookAndQuizSummaryResponse
import com.project.quizcafe.domain.quizbook.dto.response.GetQuizBookResponse
import com.project.quizcafe.domain.quizbook.dto.response.QuizSummary
import com.project.quizcafe.domain.user.entity.User

fun CreateQuizBookRequest.toQuizBook(user: User): QuizBook = QuizBook(
    category = category,
    title = title,
    description = description,
    createdBy = user,
    level = level
)

fun QuizBook.toGetQuizBookResponse(quizzes: List<Quiz>): GetQuizBookResponse {
    return GetQuizBookResponse(
        id = id,
        version = version,
        category = category,
        title = title,
        description = description,
        level = level,
        createdBy = createdBy?.nickName,
        totalQuizzes = quizzes.size,
        createdAt = createdAt
    )
}

fun QuizBook.toGetQuizBookAndQuizSummaryResponse(
    quizzes: List<Quiz>,
    bookmarkCount: Int,
    averageCorrectCount: Double,
    isSaved: Boolean
): GetQuizBookAndQuizSummaryResponse {
    val quizSummaries = quizzes.map { quiz ->
        QuizSummary(
            quizId = quiz.id,
            quizContent = quiz.content,
            quizType = quiz.questionType
        )
    }

    return GetQuizBookAndQuizSummaryResponse(
        id = id,
        version = version,
        category = category,
        title = title,
        description = description,
        level = level,
        createdBy = createdBy?.nickName,
        quizzes = quizSummaries,
        createdAt = createdAt,
        totalSaves = bookmarkCount,
        averageCorrectCount = averageCorrectCount,
        ownerId = createdBy?.id,
        views = 0, // TODO: 추후 구현
        isSaved = isSaved
    )
}

fun UpdateQuizBookRequest.applyTo(quizBook: QuizBook) {
    category?.let { quizBook.category = it }
    title?.let { quizBook.title = it }
    description?.let { quizBook.description = it }
    level?.let { quizBook.level = it }
}