package com.project.quizcafe.quiz.service

import com.project.quizcafe.auth.security.UserDetailsImpl
import com.project.quizcafe.quiz.dto.request.CreateQuizRequest
import com.project.quizcafe.quiz.dto.request.UpdateQuizRequest
import com.project.quizcafe.quiz.dto.response.QuizResponse
import com.project.quizcafe.quiz.entity.Quiz
import com.project.quizcafe.quiz.repository.QuizRepository
import com.project.quizcafe.quizbook.repository.QuizBookRepository
import jakarta.transaction.Transactional
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.*

@Service
@Transactional
class QuizServiceImpl(
    private val quizRepository: QuizRepository,
    private val quizBookRepository: QuizBookRepository
) : QuizService{
    override fun createQuiz(request: CreateQuizRequest): Long {
        val quiz = Quiz(
            quizBook = quizBookRepository.findById(request.quizBookId).get(),
            questionType = request.questionType,
            content = request.content,
            answer = request.answer,
            explanation = request.explanation
        )
        val saved = quizRepository.save(quiz)
        return saved.id
    }

    override fun getQuizzesByQuizBookId(quizBookId: Long): List<QuizResponse> {
        val quizzes = quizRepository.findAllByQuizBookId(quizBookId)
        return quizzes.map { quiz ->
            QuizResponse(
                id = quiz.id,
                quizBookId = quiz.quizBook.id,
                questionType = quiz.questionType,
                content = quiz.content,
                answer = quiz.answer,
                explanation = quiz.explanation
            )
        }
    }

    override fun updateQuiz(quizId: Long, request: UpdateQuizRequest) {
        val quiz = quizRepository.findById(quizId)
            .orElseThrow { IllegalArgumentException("퀴즈를 찾을 수 없습니다. id=$quizId") }

        val currentUser = (SecurityContextHolder.getContext().authentication.principal as UserDetailsImpl).getUser()

        val quizBookCreator = quiz.quizBook.createdBy

        if (quizBookCreator?.id != currentUser.id) {
            throw IllegalArgumentException("수정 권한이 없습니다.")
        }

        request.content?.let { quiz.content = it }
        request.answer?.let { quiz.answer = it }
        request.explanation?.let { quiz.explanation = it }
    }

    override fun deleteQuiz(quizId: Long) {
        val quiz = quizRepository.findById(quizId)
            .orElseThrow { IllegalArgumentException("퀴즈를 찾을 수 없습니다. id=$quizId") }

        val currentUser = (SecurityContextHolder.getContext().authentication.principal as UserDetailsImpl).getUser()

        val quizBookCreator = quiz.quizBook.createdBy

        if (quizBookCreator?.id != currentUser.id) {
            throw IllegalArgumentException("삭제 권한이 없습니다.")
        }

        quizRepository.delete(quiz)
    }

}