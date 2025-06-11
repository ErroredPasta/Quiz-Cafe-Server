package com.project.quizcafe.domain.quizsolving.extensions

import com.project.quizcafe.domain.quiz.dto.response.QuizResponse
import com.project.quizcafe.domain.quiz.entity.Quiz
import com.project.quizcafe.domain.quizbook.dto.request.UpdateQuizBookRequest
import com.project.quizcafe.domain.quizbook.entity.QuizBook
import com.project.quizcafe.domain.quizbooksolving.dto.request.UpdateQuizBookSolvingRequest
import com.project.quizcafe.domain.quizbooksolving.entity.QuizBookSolving
import com.project.quizcafe.domain.quizsolving.dto.request.CreateQuizSolvingRequest
import com.project.quizcafe.domain.quizsolving.dto.request.UpdateQuizSolvingRequest
import com.project.quizcafe.domain.quizsolving.dto.response.McqOptionSolvingResponse
import com.project.quizcafe.domain.quizsolving.dto.response.QuizSolvingResponse
import com.project.quizcafe.domain.quizsolving.entity.QuizSolving
import com.project.quizcafe.domain.user.entity.User


fun CreateQuizSolvingRequest.toQuizSolving(user: User, quiz: Quiz, quizBookSolving: QuizBookSolving): QuizSolving = QuizSolving(
    quizBookSolving = quizBookSolving,
    quiz = quiz,
    user = user,
    memo = memo,
    userAnswer = userAnswer,
    isCorrect = isCorrect,
    completedAt = completedAt
)

fun toQuizSolvingResponse(
    savedQuiz: QuizResponse,
    quizBookSolvingId: Long,
    quizId: Long,
    quizSolving: QuizSolving?,
    mcqOptionResponses: List<McqOptionSolvingResponse>
): QuizSolvingResponse {
    return QuizSolvingResponse(
        id = savedQuiz.id,
        quizBookSolvingId = quizBookSolvingId,
        quizId = quizId,
        questionType = savedQuiz.questionType,
        content = savedQuiz.content,
        answer = savedQuiz.answer,
        explanation = savedQuiz.explanation,
        memo = quizSolving?.memo,
        userAnswer = quizSolving?.userAnswer,
        isCorrect = quizSolving?.isCorrect,
        completedAt = quizSolving?.completedAt,
        mcqOptions = mcqOptionResponses
    )
}

fun UpdateQuizSolvingRequest.applyTo(quizSolving: QuizSolving) {
    userAnswer?.let { quizSolving.userAnswer = it }
    isCorrect?.let { quizSolving.isCorrect = it }
    memo?.let { quizSolving.memo = it }
}