package com.project.quizcafe.domain.quiz.service

import com.project.quizcafe.domain.auth.security.UserDetailsImpl
import com.project.quizcafe.domain.quiz.dto.request.CreateQuizRequest
import com.project.quizcafe.domain.quiz.dto.request.UpdateQuizRequest
import com.project.quizcafe.domain.quiz.dto.response.QuizResponse
import com.project.quizcafe.domain.quiz.extensions.applyTo
import com.project.quizcafe.domain.quiz.extensions.toQuiz
import com.project.quizcafe.domain.quiz.extensions.toQuizResponse
import com.project.quizcafe.domain.quiz.repository.QuizRepository
import com.project.quizcafe.domain.quiz.validator.QuizValidator
import com.project.quizcafe.domain.quizbook.repository.QuizBookRepository
import com.project.quizcafe.domain.quizbook.validator.QuizBookValidator
import com.project.quizcafe.domain.user.entity.User
import com.project.quizcafe.domain.versioncontrol.service.VcService
import jakarta.transaction.Transactional
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.*

@Service
@Transactional
class QuizService(
    private val quizRepository: QuizRepository,
    private val mcqOptionService: McqOptionService,
    private val vcService: VcService,
    private val quizBookValidator: QuizBookValidator,
    private val quizValidator: QuizValidator
) {

    fun createQuiz(request: CreateQuizRequest): Long {
        val quizBook = quizBookValidator.validateQuizBookNotExist(request.quizBookId)
        val quiz = request.toQuiz(quizBook)
        val saved = quizRepository.save(quiz)
        quizBook.version++
        vcService.save(quizBook.id, quizBook.version)
        return saved.id
    }

    fun getQuizzesByQuizBookId(quizBookId: Long): List<QuizResponse> {
        val quizzes = quizRepository.findAllByQuizBookId(quizBookId)
        return quizzes.map {it.toQuizResponse(mcqOptionService)}
    }

    fun getQuizzesByQuizId(quizId: Long) : QuizResponse {
        val quiz = quizValidator.validateQuizNotExist(quizId)
        return quiz.toQuizResponse(mcqOptionService)
    }

    fun updateQuiz(quizId: Long, request: UpdateQuizRequest, currentUser: User) {
        val quiz = quizValidator.validateQuizNotExist(quizId)
        quizValidator.validateMyQuiz(quiz, currentUser)
        val quizBook = quizBookValidator.validateQuizBookNotExist(quiz.quizBook.id)

        vcService.save(quizBook.id, quizBook.version+1)

        request.applyTo(quiz)
        quiz.quizBook.version++
    }

    fun deleteQuiz(quizId: Long, currentUser: User) {
        val quiz = quizValidator.validateQuizNotExist(quizId)
        quizValidator.validateMyQuiz(quiz, currentUser)
        quizRepository.delete(quiz)
    }

}