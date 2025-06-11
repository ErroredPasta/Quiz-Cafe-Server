package com.project.quizcafe.domain.quiz.extensions

import com.project.quizcafe.domain.quiz.dto.request.CreateQuizRequest
import com.project.quizcafe.domain.quiz.dto.request.UpdateQuizRequest
import com.project.quizcafe.domain.quiz.dto.response.QuizResponse
import com.project.quizcafe.domain.quiz.entity.QuestionType
import com.project.quizcafe.domain.quiz.entity.Quiz
import com.project.quizcafe.domain.quiz.service.McqOptionService
import com.project.quizcafe.domain.quizbook.dto.request.UpdateQuizBookRequest
import com.project.quizcafe.domain.quizbook.entity.QuizBook

fun CreateQuizRequest.toQuiz(quizBook: QuizBook): Quiz {
    return Quiz(
        quizBook = quizBook,
        questionType = questionType,
        content = content,
        answer = answer,
        explanation = explanation
    )
}

fun Quiz.toQuizResponse(mcqOptionService: McqOptionService): QuizResponse {
    val mcqOptionList = if (this.questionType == QuestionType.MCQ) {
        mcqOptionService.getMcqOptionsByQuizId(this.id)
    } else {
        null
    }

    return QuizResponse(
        id = this.id,
        quizBookId = this.quizBook.id,
        questionType = this.questionType,
        content = this.content,
        answer = this.answer,
        explanation = this.explanation,
        mcqOption = mcqOptionList
    )
}

fun UpdateQuizRequest.applyTo(quiz: Quiz) {
    content?.let { quiz.content = it }
    answer?.let { quiz.answer = it }
    explanation?.let { quiz.explanation = it }
}
