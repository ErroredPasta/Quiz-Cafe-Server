package com.project.quizcafe.domain.versioncontrol.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.project.quizcafe.common.exception.NotFoundException
import com.project.quizcafe.domain.quiz.service.QuizService
import com.project.quizcafe.domain.quizbook.repository.QuizBookRepository
import com.project.quizcafe.domain.versioncontrol.dto.SavedQuizBook
import com.project.quizcafe.domain.versioncontrol.entity.Vc
import com.project.quizcafe.domain.versioncontrol.repository.VcRepository
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service

@Service
class VcService(
    private val vcRepository: VcRepository,
    @Lazy private val quizService: QuizService,
    private val quizBookRepository: QuizBookRepository,
    private val objectMapper: ObjectMapper
) {
    fun save(quizBookId: Long, version: Long) {
        val quizzes = quizService.getQuizzesByQuizBookId(quizBookId)
        val quizBook = quizBookRepository.findById(quizBookId)
            .orElseThrow { NotFoundException("퀴즈북을 찾을수 없습니다.") }

        val quizBookValue = quizBook.createdBy?.let {
            SavedQuizBook(
                category = quizBook.category,
                title = quizBook.title,
                level = quizBook.level,
                description = quizBook.description,
                createdBy = it.nickName,
                quizzes = quizzes
            )
        }

        val quizBookJson: String = objectMapper.writeValueAsString(quizBookValue)

        val vc = Vc(
            quizBookId = quizBookId,
            version = version,
            quizzesValue = quizBookJson
        )
        vcRepository.save(vc)
    }
}