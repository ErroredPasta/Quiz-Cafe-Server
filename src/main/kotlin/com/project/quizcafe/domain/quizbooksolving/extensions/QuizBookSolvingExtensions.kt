package com.project.quizcafe.domain.quizbooksolving.extensions

import com.project.quizcafe.domain.quizbook.entity.QuizBook
import com.project.quizcafe.domain.quizbooksolving.dto.request.CreateQuizBookSolvingRequest
import com.project.quizcafe.domain.quizbooksolving.dto.request.UpdateQuizBookSolvingRequest
import com.project.quizcafe.domain.quizbooksolving.dto.response.QuizBookSolvingResponse
import com.project.quizcafe.domain.quizbooksolving.entity.QuizBookSolving
import com.project.quizcafe.domain.quizsolving.dto.response.QuizSolvingResponse
import com.project.quizcafe.domain.user.entity.User
import com.project.quizcafe.domain.versioncontrol.dto.SavedQuizBook

fun CreateQuizBookSolvingRequest.toQuizBookSolving(user: User, quizBook: QuizBook): QuizBookSolving = QuizBookSolving(
    user = user,
    quizBook = quizBook,
    version = version,
    totalQuizzes = totalQuizzes,
    correctCount = correctCount,
    completedAt = completedAt,
    solvingTimeSeconds = solvingTime
)

fun QuizBookSolving.toQuizBookSolvingResponse(
    userId: Long,
    savedQuizBook: SavedQuizBook,
    quizSolvingResponses: List<QuizSolvingResponse>
): QuizBookSolvingResponse {
    return QuizBookSolvingResponse(
        id = this.id,
        userId = userId,
        quizBookId = this.quizBook.id,
        version = this.version,
        level = savedQuizBook.level,
        category = savedQuizBook.category,
        title = savedQuizBook.title,
        description = savedQuizBook.description,
        totalQuizzes = this.totalQuizzes,
        correctCount = this.correctCount,
        completedAt = this.completedAt,
        solvingTime = this.solvingTimeSeconds,
        quizzes = quizSolvingResponses,
    )
}

fun UpdateQuizBookSolvingRequest.applyTo(quizBookSolving: QuizBookSolving) {
    correctCount?.let { quizBookSolving.correctCount = it }
    completedAt?.let { quizBookSolving.completedAt = it }
    solvingTime?.let { quizBookSolving.solvingTimeSeconds = it }
}