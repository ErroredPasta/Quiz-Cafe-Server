package com.project.quizcafe.domain.quizbooksolving.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.project.quizcafe.domain.auth.security.UserDetailsImpl
import com.project.quizcafe.domain.quiz.repository.McqOptionRepository
import com.project.quizcafe.domain.quiz.repository.QuizRepository
import com.project.quizcafe.domain.quizbooksolving.dto.request.CreateQuizBookSolvingRequest
import com.project.quizcafe.domain.quizbooksolving.entity.QuizBookSolving
import com.project.quizcafe.domain.quizbooksolving.repository.QuizBookSolvingRepository
import com.project.quizcafe.domain.quizbook.repository.QuizBookRepository
import com.project.quizcafe.domain.quizbooksolving.dto.request.UpdateQuizBookSolvingRequest
import com.project.quizcafe.domain.quizbooksolving.dto.response.QuizBookSolvingResponse
import com.project.quizcafe.domain.quizsolving.dto.response.McqOptionSolvingResponse
import com.project.quizcafe.domain.quizsolving.dto.response.QuizSolvingResponse
import com.project.quizcafe.domain.quizsolving.entity.QuizSolving
import com.project.quizcafe.domain.quizsolving.repository.McqOptionSolvingRepository
import com.project.quizcafe.domain.quizsolving.repository.QuizSolvingRepository
import com.project.quizcafe.domain.user.entity.User
import com.project.quizcafe.domain.user.repository.UserRepository
import com.project.quizcafe.domain.versioncontrol.dto.SavedQuizBook
import com.project.quizcafe.domain.versioncontrol.entity.Vc
import com.project.quizcafe.domain.versioncontrol.repository.VcRepository
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class QuizBookSolvingService(
    private val quizBookSolvingRepository: QuizBookSolvingRepository,
    private val quizBookRepository: QuizBookRepository,
    private val quizSolvingRepository: QuizSolvingRepository,
    private val quizRepository: QuizRepository,
    private val userRepository: UserRepository,
    private val vcRepository: VcRepository,
    private val mcqOptionRepository: McqOptionRepository
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun createQuizBookSolving(request: CreateQuizBookSolvingRequest, user: User): QuizBookSolving {

        val quizBook = quizBookRepository.findById(request.quizBookId)
            .orElseThrow { RuntimeException("퀴즈북을 찾을 수 없습니다. id=${request.quizBookId}") }

        val quizBookSolving = QuizBookSolving(
            user = user,
            quizBook = quizBook,
            version = request.version,
            totalQuizzes = request.totalQuizzes,
            correctCount = request.correctCount,
            completedAt = request.completedAt
        )

        val savedQuizBookSolving = quizBookSolvingRepository.save(quizBookSolving)

        for (quizRequest in request.quizzes) {
            val quiz = quizRepository.findById(quizRequest.quizId)
                .orElseThrow { RuntimeException("퀴즈를 찾을 수 없습니다. id=${quizRequest.quizId}") }

            val quizSolving = QuizSolving(
                quizBookSolving = savedQuizBookSolving,
                quiz = quiz,
                user = user,
                memo = quizRequest.memo,
                userAnswer = quizRequest.userAnswer,
                isCorrect = quizRequest.isCorrect,
                completedAt = quizRequest.completedAt
            )

            quizSolvingRepository.save(quizSolving)
        }

        return savedQuizBookSolving
    }

    fun updateQuizBookSolving(id: Long, request: UpdateQuizBookSolvingRequest) {
        val solving = quizBookSolvingRepository.findById(id)
            .orElseThrow { RuntimeException("문제집 풀이를 찾을 수 없습니다. id=$id") }

        val currentUser = (SecurityContextHolder.getContext().authentication.principal as UserDetailsImpl).getUser()

        if (solving.user.id != currentUser.id) {
            throw IllegalArgumentException("조회 권한이 없습니다.")
        }

        request.correctCount?.let { solving.correctCount = it }
        request.completedAt?.let { solving.completedAt = it }

        quizBookSolvingRepository.save(solving)
    }

    fun deleteQuizBookSolving(id: Long) {
        val solving = quizBookSolvingRepository.findById(id)
            .orElseThrow { RuntimeException("문제집 풀이를 찾을 수 없습니다. id=$id") }

        val currentUser = (SecurityContextHolder.getContext().authentication.principal as UserDetailsImpl).getUser()

        if (solving.user.id != currentUser.id) {
            throw IllegalArgumentException("조회 권한이 없습니다.")
        }

        quizBookSolvingRepository.delete(solving)
    }

    fun getAllByUserId(userId: Long): List<QuizBookSolvingResponse> {
        try {
            val user = userRepository.findById(userId)
                .orElseThrow { RuntimeException("유저를 찾을 수 없습니다. id=$userId") }

            val quizBookSolvings = quizBookSolvingRepository.findByUserId(userId) // 이 부분 확인 필요

            return quizBookSolvings.map { quizBookSolving ->

                val quizBookValue = vcRepository.findByQuizBookIdAndVersion(quizBookSolving.quizBook.id, quizBookSolving.version)

                val objectMapper = jacksonObjectMapper()
                val savedQuizBook: SavedQuizBook = objectMapper.readValue(quizBookValue?.quizzesValue ?: "")

                val quizSolvingResponses = savedQuizBook.quizzes.map { savedQuiz ->
                    val quiz = quizRepository.findById(savedQuiz.id)
                        .orElseThrow { RuntimeException("퀴즈를 찾을 수 없습니다.") }
                    val quizSolving = quizSolvingRepository.findById(quiz.id)
                        .orElseThrow { RuntimeException("퀴즈 풀이를 찾을 수 없습니다.") }

                    val mcqOptions = mcqOptionRepository.findByQuizId(quiz.id)

                    val mcqOptionResponses = mcqOptions.map { option ->
                        McqOptionSolvingResponse(
                            id = option.id,
                            quizSolvingId = savedQuiz.id,
                            optionNumber = option.optionNumber,
                            optionContent = option.optionContent,
                            isCorrect = option.isCorrect
                        )
                    }

                    QuizSolvingResponse(
                        id = savedQuiz.id,
                        quizBookSolvingId = quizBookSolving.id,
                        quizId = quiz.id,
                        questionType = savedQuiz.questionType,
                        content = savedQuiz.content,
                        answer = savedQuiz.answer,
                        explanation = savedQuiz.explanation,
                        memo = quizSolving.memo,
                        userAnswer = quizSolving.userAnswer,
                        isCorrect = quizSolving.isCorrect,
                        completedAt = quizSolving.completedAt,
                        mcqOptions = mcqOptionResponses
                    )
                }

                QuizBookSolvingResponse(
                    id = quizBookSolving.id,
                    userId = user.id,
                    quizBookId = quizBookSolving.quizBook.id,
                    version = quizBookSolving.version,
                    level = savedQuizBook.level,
                    category = savedQuizBook.category,
                    title = savedQuizBook.title,
                    description = savedQuizBook.description,
                    totalQuizzes = quizBookSolving.totalQuizzes,
                    correctCount = quizBookSolving.correctCount,
                    completedAt = quizBookSolving.completedAt,
                    quizzes = quizSolvingResponses
                )
            }
        } catch (e: Exception) {
            log.error("getAllByUserId 처리 중 예외 발생", e)
            throw e
        }
    }


    fun getQuizBookSolvingById(id: Long): QuizBookSolvingResponse {
        val quizBookSolving = quizBookSolvingRepository.findById(id)
            .orElseThrow { RuntimeException("퀴즈북 풀이 결과를 찾을 수 없습니다. id=$id") }

        val quizBookValue = vcRepository.findByQuizBookIdAndVersion(
            quizBookSolving.quizBook.id,
            quizBookSolving.version
        ) ?: throw RuntimeException("퀴즈북 버전을 찾을 수 없습니다.")

        val savedQuizBook: SavedQuizBook = jacksonObjectMapper()
            .readValue(quizBookValue.quizzesValue)

        val quizSolvingResponses = savedQuizBook.quizzes.map { savedQuiz ->
            val quiz = quizRepository.findById(savedQuiz.id)
                .orElseThrow { RuntimeException("퀴즈를 찾을 수 없습니다. id=${savedQuiz.id}") }

            val quizSolving = quizSolvingRepository.findByQuizBookSolvingIdAndQuizId(quizBookSolving.id, quiz.id)
                ?: throw RuntimeException("퀴즈 풀이를 찾을 수 없습니다. quizId=${quiz.id}")

            val mcqOptionResponses = mcqOptionRepository.findByQuizId(quiz.id).map { option ->
                McqOptionSolvingResponse(
                    id = option.id,
                    quizSolvingId = savedQuiz.id,
                    optionNumber = option.optionNumber,
                    optionContent = option.optionContent,
                    isCorrect = option.isCorrect
                )
            }

            QuizSolvingResponse(
                id = quizSolving.id,
                quizBookSolvingId = quizBookSolving.id,
                quizId = quiz.id,
                questionType = savedQuiz.questionType,
                content = savedQuiz.content,
                answer = savedQuiz.answer,
                explanation = savedQuiz.explanation,
                memo = quizSolving.memo,
                userAnswer = quizSolving.userAnswer,
                isCorrect = quizSolving.isCorrect,
                completedAt = quizSolving.completedAt,
                mcqOptions = mcqOptionResponses
            )
        }

        return QuizBookSolvingResponse(
            id = quizBookSolving.id,
            userId = quizBookSolving.user.id,
            quizBookId = quizBookSolving.quizBook.id,
            version = quizBookSolving.version,
            level = savedQuizBook.level,
            category = savedQuizBook.category,
            title = savedQuizBook.title,
            description = savedQuizBook.description,
            totalQuizzes = quizBookSolving.totalQuizzes,
            correctCount = quizBookSolving.correctCount,
            completedAt = quizBookSolving.completedAt,
            quizzes = quizSolvingResponses
        )
    }

}