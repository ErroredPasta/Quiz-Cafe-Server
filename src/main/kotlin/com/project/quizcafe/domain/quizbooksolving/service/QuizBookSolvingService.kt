package com.project.quizcafe.domain.quizbooksolving.service

import com.project.quizcafe.domain.auth.security.UserDetailsImpl
import com.project.quizcafe.domain.quiz.entity.QuestionType
import com.project.quizcafe.domain.quiz.repository.QuizRepository
import com.project.quizcafe.domain.quizbooksolving.dto.request.CreateQuizBookSolvingRequest
import com.project.quizcafe.domain.quizbooksolving.entity.QuizBookSolving
import com.project.quizcafe.domain.quizbooksolving.repository.QuizBookSolvingRepository
import com.project.quizcafe.domain.quizbook.repository.QuizBookRepository
import com.project.quizcafe.domain.quizbooksolving.dto.request.UpdateQuizBookSolvingRequest
import com.project.quizcafe.domain.quizbooksolving.dto.response.QuizBookSolvingResponse
import com.project.quizcafe.domain.quizsolving.dto.response.McqOptionSolvingResponse
import com.project.quizcafe.domain.quizsolving.dto.response.QuizSolvingResponse
import com.project.quizcafe.domain.quizsolving.entity.McqOptionSolving
import com.project.quizcafe.domain.quizsolving.entity.QuizSolving
import com.project.quizcafe.domain.quizsolving.repository.McqOptionSolvingRepository
import com.project.quizcafe.domain.quizsolving.repository.QuizSolvingRepository
import com.project.quizcafe.domain.quizsolving.service.McqOptionSolvingService
import com.project.quizcafe.domain.user.repository.UserRepository
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
    private val mcqOptionSolvingRepository: McqOptionSolvingRepository,
    private val userRepository: UserRepository,
) {

    @Transactional
    fun createQuizBookSolving(request: CreateQuizBookSolvingRequest): QuizBookSolving {
        val user = (SecurityContextHolder.getContext().authentication.principal as UserDetailsImpl).getUser()

        val quizBook = quizBookRepository.findById(request.quizBookId)
            .orElseThrow { RuntimeException("퀴즈북을 찾을 수 없습니다. id=${request.quizBookId}") }

        val quizBookSolving = QuizBookSolving(
            user = user,
            quizBook = quizBook,
            version = request.version,
            level = request.level,
            category = request.category,
            title = request.title,
            description = request.description,
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
                version = quizRequest.version,
                questionType = QuestionType.valueOf(quizRequest.questionType),
                content = quizRequest.content,
                answer = quizRequest.answer,
                explanation = quizRequest.explanation,
                memo = quizRequest.memo,
                userAnswer = quizRequest.userAnswer,
                isCorrect = quizRequest.isCorrect,
                completedAt = quizRequest.completedAt
            )

            quizSolvingRepository.save(quizSolving)

            if (quizSolving.questionType == QuestionType.MCQ) {
                val mcqOptions = quizRequest.mcqOptions?.map { option ->
                    McqOptionSolving(
                        quizSolving = quizSolving,
                        optionNumber = option.optionNumber,
                        optionContent = option.optionContent,
                        isCorrect = option.isCorrect
                    )
                }
                mcqOptions?.let { mcqOptionSolvingRepository.saveAll(it) }
            }
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
        val user = userRepository.findById(userId)
            .orElseThrow { RuntimeException("유저를 찾을 수 없습니다. id=$userId") }

        return quizBookSolvingRepository.findByUserId(userId).map { quizBookSolving ->
            val quizSolvings = quizSolvingRepository.findByQuizBookSolvingId(quizBookSolving.id)

            val quizSolvingResponses = quizSolvings.map { quizSolving ->
                val mcqOptionEntities = mcqOptionSolvingRepository.findByQuizSolvingId(quizSolving.id)
                val mcqOptionResponses = mcqOptionEntities.map { option ->
                    McqOptionSolvingResponse(
                        id = option.id,
                        quizSolvingId = quizSolving.id,
                        optionNumber = option.optionNumber,
                        optionContent = option.optionContent,
                        isCorrect = option.isCorrect
                    )
                }

                QuizSolvingResponse(
                    id = quizSolving.id,
                    quizBookSolvingId = quizBookSolving.id,
                    quizId = quizSolving.quiz.id,
                    version = quizSolving.version,
                    questionType = quizSolving.questionType,
                    content = quizSolving.content,
                    answer = quizSolving.answer,
                    explanation = quizSolving.explanation,
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
                level = quizBookSolving.level,
                category = quizBookSolving.category,
                title = quizBookSolving.title,
                description = quizBookSolving.description,
                totalQuizzes = quizBookSolving.totalQuizzes,
                correctCount = quizBookSolving.correctCount,
                completedAt = quizBookSolving.completedAt,
                quizzes = quizSolvingResponses
            )
        }
    }

}