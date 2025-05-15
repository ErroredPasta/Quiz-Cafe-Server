package com.project.quizcafe.domain.quizsolving.service

import com.project.quizcafe.domain.auth.security.UserDetailsImpl
import com.project.quizcafe.domain.quiz.entity.QuestionType
import com.project.quizcafe.domain.quiz.repository.QuizRepository
import com.project.quizcafe.domain.quizbooksolving.repository.QuizBookSolvingRepository
import com.project.quizcafe.domain.quizsolving.dto.request.CreateQuizSolvingRequest
import com.project.quizcafe.domain.quizsolving.dto.request.CreateSingleQuizSolvingRequest
import com.project.quizcafe.domain.quizsolving.dto.request.UpdateQuizSolvingRequest
import com.project.quizcafe.domain.quizsolving.dto.response.QuizSolvingResponse
import com.project.quizcafe.domain.quizsolving.entity.McqOptionSolving
import com.project.quizcafe.domain.quizsolving.entity.QuizSolving
import com.project.quizcafe.domain.quizsolving.repository.McqOptionSolvingRepository
import com.project.quizcafe.domain.quizsolving.repository.QuizSolvingRepository
import com.project.quizcafe.domain.user.entity.User
import com.project.quizcafe.domain.user.repository.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class QuizSolvingService(
    private val quizSolvingRepository: QuizSolvingRepository,
    private val quizBookSolvingRepository: QuizBookSolvingRepository,
    private val quizRepository: QuizRepository,
    private val mcqOptionSolvingService: McqOptionSolvingService,
    private val userRepository: UserRepository,
    private val mcqOptionSolvingRepository: McqOptionSolvingRepository,
) {

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

    fun getQuizSolving(id: Long): QuizSolvingResponse {
        val quizSolving = quizSolvingRepository.findById(id)
            .orElseThrow { RuntimeException("QuizSolving not found with id $id") }
        val quiz = quizRepository.findById(quizSolving.quiz.id)
            .orElseThrow { RuntimeException("QuizSolving not found with id $id") }

        val currentUser = (SecurityContextHolder.getContext().authentication.principal as UserDetailsImpl).getUser()

        if (quizSolving.user.id != currentUser.id) {
            throw IllegalArgumentException("조회 권한이 없습니다.")
        }

        return QuizSolvingResponse(
            id = quizSolving.id,
            quizBookSolvingId = quizSolving.quizBookSolving.id,
            quizId = quizSolving.quiz.id,
            questionType = quiz.questionType,
            content = quiz.content,
            answer = quiz.answer,
            explanation = quiz.explanation,
            memo = quizSolving.memo,
            userAnswer = quizSolving.userAnswer,
            isCorrect = quizSolving.isCorrect,
            completedAt = quizSolving.completedAt,
            mcqOptions = mcqOptionSolvingService.getMcqOptionsByQuizSolvingId(id)
        )
    }

    fun updateQuizSolving(id: Long, request: UpdateQuizSolvingRequest) {
        val quizSolving = quizSolvingRepository.findById(id)
            .orElseThrow { IllegalArgumentException("퀴즈 풀이를 찾을 수 없습니다. id=$id") }

        val currentUser = (SecurityContextHolder.getContext().authentication.principal as UserDetailsImpl).getUser()
        if (quizSolving.user.id != currentUser.id) {
            throw IllegalArgumentException("수정 권한이 없습니다.")
        }

        request.userAnswer?.let { quizSolving.userAnswer = it }
        request.isCorrect?.let { quizSolving.isCorrect = it }
        request.memo?.let { quizSolving.memo = it }

        // 퀴즈 풀이 수정 완료 후 저장
        quizSolvingRepository.save(quizSolving)
    }

    fun deleteQuizSolving(id: Long) {
        val quizSolving = quizSolvingRepository.findById(id)
            .orElseThrow { IllegalArgumentException("퀴즈 풀이를 찾을 수 없습니다. id=$id") }

        val currentUser = (SecurityContextHolder.getContext().authentication.principal as UserDetailsImpl).getUser()

        if (quizSolving.user.id != currentUser.id) {
            throw IllegalArgumentException("삭제 권한이 없습니다.")
        }

        quizSolvingRepository.delete(quizSolving)
    }

}
