package com.project.quizcafe.domain.quiz.service

import com.project.quizcafe.domain.quiz.dto.request.CreateMcqOptionRequest
import com.project.quizcafe.domain.quiz.dto.request.UpdateMcqOptionRequest
import com.project.quizcafe.domain.quiz.dto.response.McqOptionResponse
import com.project.quizcafe.domain.quiz.extensions.applyTo
import com.project.quizcafe.domain.quiz.extensions.toEntity
import com.project.quizcafe.domain.quiz.extensions.toMcqOptionResponse
import com.project.quizcafe.domain.quiz.repository.McqOptionRepository
import com.project.quizcafe.domain.quiz.repository.QuizRepository
import com.project.quizcafe.domain.quiz.repository.getByQuizBookId
import com.project.quizcafe.domain.quiz.validator.McqOptionValidator
import com.project.quizcafe.domain.quizbook.validator.QuizBookValidator
import com.project.quizcafe.domain.user.entity.User
import com.project.quizcafe.domain.versioncontrol.service.VcService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class McqOptionService(
    private val mcqOptionRepository: McqOptionRepository,
    private val vcService: VcService,
    private val quizRepository: QuizRepository,
    private val quizBookValidator: QuizBookValidator,
    private val mcqOptionValidator: McqOptionValidator
) {

    fun createMcqOption(request: CreateMcqOptionRequest, currentUser: User): McqOptionResponse {
        val quiz = quizRepository.getByQuizBookId(request.quizId)
        quizBookValidator.validateMyQuizBook(quiz.quizBook, currentUser)
        val mcqOption = request.toEntity(quiz)
        val saved = mcqOptionRepository.save(mcqOption)
        quiz.quizBook.version++
        vcService.save(quiz.quizBook.id, quiz.quizBook.version)
        return saved.toMcqOptionResponse()
    }

    @Transactional
    fun updateMcqOption(id: Long, request: UpdateMcqOptionRequest, currentUser: User) {
        val mcqOption = mcqOptionValidator.validateMcqOptionNotExist(id)
        quizBookValidator.validateMyQuizBook(mcqOption.quiz.quizBook, currentUser)

        vcService.save(mcqOption.quiz.quizBook.id, mcqOption.quiz.quizBook.version+1)

        request.applyTo(mcqOption)

        mcqOption.quiz.quizBook.version++
    }


    fun deleteMcqOption(id: Long, currentUser: User) {
        val mcqOption = mcqOptionValidator.validateMcqOptionNotExist(id)
        quizBookValidator.validateMyQuizBook(mcqOption.quiz.quizBook, currentUser)
        mcqOptionRepository.delete(mcqOption)
    }

    fun getMcqOptionsByQuizId(quizId: Long): List<McqOptionResponse> {
        val mcqOptions = mcqOptionRepository.findByQuizId(quizId)
        return mcqOptions.map { it.toMcqOptionResponse() }
    }

    fun getMcqOptionsById(id: Long): McqOptionResponse {
        val mcqOption = mcqOptionValidator.validateMcqOptionNotExist(id)
        return mcqOption.toMcqOptionResponse()
    }
}
