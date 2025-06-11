package com.project.quizcafe.domain.versioncontrol.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.project.quizcafe.common.exception.NotFoundException
import com.project.quizcafe.domain.quiz.dto.response.McqOptionResponse
import com.project.quizcafe.domain.quiz.dto.response.QuizResponse
import com.project.quizcafe.domain.quiz.entity.QuestionType
import com.project.quizcafe.domain.quiz.repository.QuizRepository
import com.project.quizcafe.domain.quiz.service.McqOptionService
import com.project.quizcafe.domain.quizbook.repository.QuizBookRepository
import com.project.quizcafe.domain.versioncontrol.dto.SavedQuizBook
import com.project.quizcafe.domain.versioncontrol.entity.Vc
import com.project.quizcafe.domain.versioncontrol.repository.VcRepository
import org.springframework.stereotype.Service
import org.springframework.context.annotation.Lazy

@Service
class VcService(
    private val vcRepository: VcRepository,
    private val quizRepository: QuizRepository,
    private val quizBookRepository: QuizBookRepository,
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

        val quizBook = quizBookRepository.findById(quizBookId)
            .orElseThrow { NotFoundException("퀴즈북을 찾을수 없습니다.") }

        val quizBookValue = quizBook.createdBy?.let {
            SavedQuizBook(
                category = quizBook.category,
                title = quizBook.title,
                level = quizBook.level,
                description = quizBook.description,
                createdBy = it.nickName,
                quizzes = quizzesValue
            )
        }

        val objectMapper = jacksonObjectMapper()
        //objectMapper.findAndRegisterModules()
        val quizBookJson: String = objectMapper.writeValueAsString(quizBookValue)

        val vc = Vc(
            quizBookId = quizBookId,
            version = version,
            quizzesValue = quizBookJson
        )
        vcRepository.save(vc)
    }
}