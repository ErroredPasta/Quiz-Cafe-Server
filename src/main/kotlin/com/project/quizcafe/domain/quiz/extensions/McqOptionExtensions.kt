package com.project.quizcafe.domain.quiz.extensions

import com.project.quizcafe.domain.quiz.dto.request.CreateMcqOptionRequest
import com.project.quizcafe.domain.quiz.dto.request.UpdateMcqOptionRequest
import com.project.quizcafe.domain.quiz.dto.request.UpdateQuizRequest
import com.project.quizcafe.domain.quiz.dto.response.McqOptionResponse
import com.project.quizcafe.domain.quiz.entity.McqOption
import com.project.quizcafe.domain.quiz.entity.Quiz
import com.project.quizcafe.domain.quizsolving.dto.response.McqOptionSolvingResponse

fun CreateMcqOptionRequest.toEntity(quiz: Quiz): McqOption {
    return McqOption(
        quiz = quiz,
        optionNumber = this.optionNumber,
        optionContent = this.optionContent,
        isCorrect = this.isCorrect
    )
}

fun McqOption.toMcqOptionResponse(): McqOptionResponse {
    return McqOptionResponse(
        id = this.id,
        quizId = this.quiz.id,
        optionNumber = this.optionNumber,
        optionContent = this.optionContent,
        isCorrect = this.isCorrect
    )
}

fun UpdateMcqOptionRequest.applyTo(mcqOption: McqOption) {
    optionContent?.let { mcqOption.optionContent = it }
    isCorrect?.let { mcqOption.isCorrect = it }
}

fun List<McqOption>.toSolvingResponses(quizSolvingId: Long): List<McqOptionSolvingResponse> {
    return this.map { option ->
        McqOptionSolvingResponse(
            id = option.id,
            quizSolvingId = quizSolvingId,
            optionNumber = option.optionNumber,
            optionContent = option.optionContent,
            isCorrect = option.isCorrect
        )
    }
}