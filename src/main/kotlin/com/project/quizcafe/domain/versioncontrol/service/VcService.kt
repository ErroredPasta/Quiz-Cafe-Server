package com.project.quizcafe.domain.versioncontrol.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.project.quizcafe.domain.quiz.dto.response.McqOptionResponse
import com.project.quizcafe.domain.quiz.dto.response.QuizResponse
import com.project.quizcafe.domain.quiz.entity.QuestionType
import com.project.quizcafe.domain.quiz.repository.QuizRepository
import com.project.quizcafe.domain.quiz.service.McqOptionService
import com.project.quizcafe.domain.versioncontrol.entity.Vc
import com.project.quizcafe.domain.versioncontrol.repository.VcRepository
import org.springframework.stereotype.Service
import org.springframework.context.annotation.Lazy

@Service
class VcService(
    private val vcRepository: VcRepository,
    private val quizRepository: QuizRepository,
    @Lazy private val mcqOptionService: McqOptionService
) {
    fun save(quizBookId: Long, version: Long) {

        val quizzes = quizRepository.findAllByQuizBookId(quizBookId)

        val quizzesValue = quizzes.map { quiz ->
            var mcqOptionList: List<McqOptionResponse>? = null
            if (quiz.questionType == QuestionType.MCQ) {
                mcqOptionList = mcqOptionService.getMcqOptionsByQuizId(quiz.id)
            }
            QuizResponse(
                id = quiz.id,
                quizBookId = quiz.quizBook.id,
                questionType = quiz.questionType,
                content = quiz.content,
                answer = quiz.answer,
                explanation = quiz.explanation,
                mcqOption = mcqOptionList
            )
        }
        val objectMapper = jacksonObjectMapper()
        val quizzesJson: String = objectMapper.writeValueAsString(quizzesValue)
        val vc = Vc(
            quizBookId = quizBookId,
            version = version,
            quizzesValue = quizzesJson
        )
        vcRepository.save(vc)
    }
}