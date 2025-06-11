package com.project.quizcafe.domain.quizsolving.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.project.quizcafe.domain.auth.security.UserDetailsImpl
import com.project.quizcafe.domain.quiz.repository.QuizRepository
import com.project.quizcafe.domain.quiz.validator.QuizValidator
import com.project.quizcafe.domain.quizbooksolving.repository.QuizBookSolvingRepository
import com.project.quizcafe.domain.quizsolving.dto.request.UpdateQuizSolvingRequest
import com.project.quizcafe.domain.quizsolving.dto.response.QuizSolvingResponse
import com.project.quizcafe.domain.quizsolving.extensions.applyTo
import com.project.quizcafe.domain.quizsolving.extensions.toQuizSolvingResponse
import com.project.quizcafe.domain.quizsolving.repository.McqOptionSolvingRepository
import com.project.quizcafe.domain.quizsolving.repository.QuizSolvingRepository
import com.project.quizcafe.domain.quizsolving.validator.QuizSolvingValidator
import com.project.quizcafe.domain.user.entity.User
import com.project.quizcafe.domain.user.repository.UserRepository
import com.project.quizcafe.domain.versioncontrol.dto.SavedQuizBook
import com.project.quizcafe.domain.versioncontrol.repository.VcRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class QuizSolvingService(
    private val quizSolvingRepository: QuizSolvingRepository,
    private val quizRepository: QuizRepository,
    private val mcqOptionSolvingService: McqOptionSolvingService,
    private val quizSolvingValidator: QuizSolvingValidator,
    private val quizValidator: QuizValidator,
    private val vcRepository: VcRepository,
    private val quizBookSolvingRepository: QuizBookSolvingRepository,
    private val userRepository: UserRepository,
    private val mcqOptionSolvingRepository: McqOptionSolvingRepository,
) {
    fun getQuizSolving(id: Long, currentUser: User): QuizSolvingResponse {
        val quizSolving = quizSolvingValidator.validateQuizSolvingNotExist(id)
        val quiz = quizValidator.validateQuizNotExist(quizSolving.quiz.id)
        quizSolvingValidator.validateMyQuizSolving(quizSolving, currentUser)

        val quizBookValue = vcRepository.findByQuizBookIdAndVersion(quizSolving.quizBookSolving.quizBook.id, quizSolving.quizBookSolving.version)
            ?: throw RuntimeException("퀴즈북 버전을 찾을 수 없습니다.")
        val savedQuizBook: SavedQuizBook = jacksonObjectMapper().readValue(quizBookValue.quizzesValue)
        val savedQuiz = savedQuizBook.quizzes.find { it.id == quiz.id }
            ?: throw RuntimeException("저장된 퀴즈(savedQuiz)를 찾을 수 없습니다. quizId=${quiz.id}")

        return toQuizSolvingResponse(savedQuiz, quizSolving.quizBookSolving.id, quiz.id, quizSolving, mcqOptionSolvingService.getMcqOptionsByQuizSolvingId(id))
    }

    fun updateQuizSolving(id: Long, request: UpdateQuizSolvingRequest, currentUser: User) {
        val quizSolving = quizSolvingValidator.validateQuizSolvingNotExist(id)
        quizSolvingValidator.validateMyQuizSolving(quizSolving, currentUser)
        request.applyTo(quizSolving)
    }

    fun deleteQuizSolving(id: Long, currentUser: User) {
        val quizSolving = quizSolvingValidator.validateQuizSolvingNotExist(id)
        quizSolvingValidator.validateMyQuizSolving(quizSolving, currentUser)
        quizSolvingRepository.delete(quizSolving)
    }

    //    fun createQuizSolving(request: CreateSingleQuizSolvingRequest, user: User): QuizSolving {
//        val quizSolving = QuizSolving(
//            quizBookSolving = quizBookSolvingRepository.findById(request.quizBookSolvingId).get(),
//            quiz = quizRepository.findById(request.quizId).get(),
//            user = user,
//            version = request.version,
//            questionType = QuestionType.valueOf(request.questionType),
//            content = request.content,
//            answer = request.answer,
//            explanation = request.explanation,
//            memo = request.memo,
//            userAnswer = request.userAnswer,
//            isCorrect = request.isCorrect,
//            completedAt = request.completedAt
//        )
//
//        val saved = quizSolvingRepository.save(quizSolving)
//        if (quizSolving.questionType == QuestionType.MCQ) {
//            request.mcqOptions?.map { option ->
//                McqOptionSolving(
//                    quizSolving = quizSolving,
//                    optionNumber = option.optionNumber,
//                    optionContent = option.optionContent,
//                    isCorrect = option.isCorrect
//                )
//            }
//        }
//
//        return saved
//    }
}
