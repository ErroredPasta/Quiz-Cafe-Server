package com.project.quizcafe.domain.quizbooksolving.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.project.quizcafe.domain.quiz.extensions.toSolvingResponses
import com.project.quizcafe.domain.quiz.repository.McqOptionRepository
import com.project.quizcafe.domain.quiz.repository.QuizRepository
import com.project.quizcafe.domain.quiz.repository.getByQuizBookId
import com.project.quizcafe.domain.quiz.validator.QuizValidator
import com.project.quizcafe.domain.quizbook.repository.QuizBookRepository
import com.project.quizcafe.domain.quizbook.repository.getQuizBookById
import com.project.quizcafe.domain.quizbooksolving.dto.request.CreateQuizBookSolvingRequest
import com.project.quizcafe.domain.quizbooksolving.entity.QuizBookSolving
import com.project.quizcafe.domain.quizbooksolving.repository.QuizBookSolvingRepository
import com.project.quizcafe.domain.quizbooksolving.dto.request.UpdateQuizBookSolvingRequest
import com.project.quizcafe.domain.quizbooksolving.dto.response.QuizBookSolvingResponse
import com.project.quizcafe.domain.quizbooksolving.extensions.applyTo
import com.project.quizcafe.domain.quizbooksolving.extensions.toQuizBookSolving
import com.project.quizcafe.domain.quizbooksolving.extensions.toQuizBookSolvingResponse
import com.project.quizcafe.domain.quizbooksolving.validator.QuizBookSolvingValidator
import com.project.quizcafe.domain.quizsolving.dto.response.QuizSolvingResponse
import com.project.quizcafe.domain.quizsolving.extensions.toQuizSolving
import com.project.quizcafe.domain.quizsolving.extensions.toQuizSolvingResponse
import com.project.quizcafe.domain.quizsolving.repository.QuizSolvingRepository
import com.project.quizcafe.domain.quizsolving.validator.QuizSolvingValidator
import com.project.quizcafe.domain.user.entity.User
import com.project.quizcafe.domain.versioncontrol.dto.SavedQuizBook
import com.project.quizcafe.domain.versioncontrol.repository.VcRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class QuizBookSolvingService(
    private val quizBookSolvingRepository: QuizBookSolvingRepository,
    private val quizSolvingRepository: QuizSolvingRepository,
    private val quizRepository: QuizRepository,
    private val vcRepository: VcRepository,
    private val mcqOptionRepository: McqOptionRepository,
    private val quizValidator: QuizValidator,
    private val quizBookSolvingValidator: QuizBookSolvingValidator,
    private val quizSolvingValidator: QuizSolvingValidator,
    private val quizBookRepository: QuizBookRepository
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun createQuizBookSolving(request: CreateQuizBookSolvingRequest, user: User): QuizBookSolving {
        val quizBook = quizBookRepository.getQuizBookById(request.quizBookId)

        val quizBookSolving = request.toQuizBookSolving(user, quizBook)
        val savedQuizBookSolving = quizBookSolvingRepository.save(quizBookSolving)

        request.quizzes.forEach {
            val quiz = quizRepository.getByQuizBookId(it.quizId)
            val quizSolving = it.toQuizSolving(user, quiz, savedQuizBookSolving)
            quizSolvingRepository.save(quizSolving)
        }
        return savedQuizBookSolving
    }

    fun updateQuizBookSolving(id: Long, request: UpdateQuizBookSolvingRequest, currentUser: User) {
        val quizBookSolving = quizBookSolvingValidator.validateQuizBookSolvingNotExist(id)
        quizBookSolvingValidator.validateMyQuizBookSolving(quizBookSolving, currentUser)
        request.applyTo(quizBookSolving)
    }

    fun deleteQuizBookSolving(id: Long, currentUser: User) {
        val quizBookSolving = quizBookSolvingValidator.validateQuizBookSolvingNotExist(id)
        quizBookSolvingValidator.validateMyQuizBookSolving(quizBookSolving, currentUser)
        quizBookSolvingRepository.delete(quizBookSolving)
    }

    fun getAllByUserId(user: User): List<QuizBookSolvingResponse> {
        val quizBookSolvingList = quizBookSolvingRepository.findByUserId(user.id)
        val objectMapper = jacksonObjectMapper()

        return quizBookSolvingList.map { quizBookSolving ->
            val quizBookValue = vcRepository.findByQuizBookIdAndVersion(quizBookSolving.quizBook.id, quizBookSolving.version)
                ?: throw RuntimeException("퀴즈북 버전을 찾을 수 없습니다.")
            val savedQuizBook: SavedQuizBook = objectMapper.readValue(quizBookValue.quizzesValue)

            val quizSolvingResponses = buildQuizSolvingResponses(quizBookSolving, savedQuizBook)

            quizBookSolving.toQuizBookSolvingResponse(
                userId = user.id,
                savedQuizBook = savedQuizBook,
                quizSolvingResponses = quizSolvingResponses
            )
        }
    }

    fun getQuizBookSolvingById(id: Long): QuizBookSolvingResponse {
        val quizBookSolving = quizBookSolvingValidator.validateQuizBookSolvingNotExist(id)

        val quizBookValue = vcRepository.findByQuizBookIdAndVersion(quizBookSolving.quizBook.id, quizBookSolving.version)
            ?: throw RuntimeException("퀴즈북 버전을 찾을 수 없습니다.")
        val savedQuizBook: SavedQuizBook = jacksonObjectMapper().readValue(quizBookValue.quizzesValue)

        val quizSolvingResponses = buildQuizSolvingResponses(quizBookSolving, savedQuizBook)

        return quizBookSolving.toQuizBookSolvingResponse(
            userId = quizBookSolving.user.id,
            savedQuizBook = savedQuizBook,
            quizSolvingResponses = quizSolvingResponses
        )

//        val quizSolvingResponses = savedQuizBook.quizzes.map { savedQuiz ->
//            val quiz = quizRepository.findById(savedQuiz.id)
//                .orElseThrow { RuntimeException("퀴즈를 찾을 수 없습니다. id=${savedQuiz.id}") }
//
//            val quizSolving = quizSolvingRepository.findByQuizBookSolvingIdAndQuizId(quizBookSolving.id, quiz.id)
//                ?: throw RuntimeException("퀴즈 풀이를 찾을 수 없습니다. quizId=${quiz.id}")
//
//            val mcqOptionResponses = mcqOptionRepository.findByQuizId(quiz.id).map { option ->
//                McqOptionSolvingResponse(
//                    id = option.id,
//                    quizSolvingId = savedQuiz.id,
//                    optionNumber = option.optionNumber,
//                    optionContent = option.optionContent,
//                    isCorrect = option.isCorrect
//                )
//            }
//
//            QuizSolvingResponse(
//                id = quizSolving.id,
//                quizBookSolvingId = quizBookSolving.id,
//                quizId = quiz.id,
//                questionType = savedQuiz.questionType,
//                content = savedQuiz.content,
//                answer = savedQuiz.answer,
//                explanation = savedQuiz.explanation,
//                memo = quizSolving.memo,
//                userAnswer = quizSolving.userAnswer,
//                isCorrect = quizSolving.isCorrect,
//                completedAt = quizSolving.completedAt,
//                mcqOptions = mcqOptionResponses
//            )
//        }
//
//        return QuizBookSolvingResponse(
//            id = quizBookSolving.id,
//            userId = quizBookSolving.user.id,
//            quizBookId = quizBookSolving.quizBook.id,
//            version = quizBookSolving.version,
//            level = savedQuizBook.level,
//            category = savedQuizBook.category,
//            title = savedQuizBook.title,
//            description = savedQuizBook.description,
//            totalQuizzes = quizBookSolving.totalQuizzes,
//            correctCount = quizBookSolving.correctCount,
//            completedAt = quizBookSolving.completedAt,
//            quizzes = quizSolvingResponses
//        )
    }

    private fun buildQuizSolvingResponses(
        quizBookSolving: QuizBookSolving,
        savedQuizBook: SavedQuizBook
    ): List<QuizSolvingResponse> {
        return savedQuizBook.quizzes.map { savedQuiz ->
            val quiz = quizRepository.getByQuizBookId(savedQuiz.id)
            val quizSolving = quizSolvingRepository.findByQuizBookSolvingIdAndQuizId(quizBookSolving.id, quiz.id)
            val mcqOptions = mcqOptionRepository.findByQuizId(quiz.id)
            val mcqOptionResponses = mcqOptions.toSolvingResponses(savedQuiz.id)

            toQuizSolvingResponse(savedQuiz, quizBookSolving.id, quiz.id, quizSolving, mcqOptionResponses)
        }
    }

}