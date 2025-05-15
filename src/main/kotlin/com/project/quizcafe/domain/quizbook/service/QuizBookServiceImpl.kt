package com.project.quizcafe.domain.quizbook.service

import com.project.quizcafe.domain.auth.security.UserDetailsImpl
import com.project.quizcafe.domain.quiz.repository.QuizRepository
import com.project.quizcafe.domain.quizbook.dto.request.CreateQuizBookRequest
import com.project.quizcafe.domain.quizbook.dto.request.UpdateQuizBookRequest
import com.project.quizcafe.domain.quizbook.dto.response.GetQuizBookAndQuizSummaryResponse
import com.project.quizcafe.domain.quizbook.dto.response.GetQuizBookResponse
import com.project.quizcafe.domain.quizbook.dto.response.QuizSummary
import com.project.quizcafe.domain.quizbook.entity.QuizBook
import com.project.quizcafe.domain.quizbook.repository.QuizBookRepository
import com.project.quizcafe.domain.user.entity.User
import com.project.quizcafe.domain.user.repository.UserRepository
import com.project.quizcafe.domain.versioncontrol.repository.VcRepository
import com.project.quizcafe.domain.versioncontrol.service.VcService
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class QuizBookServiceImpl(
    private val quizBookRepository: QuizBookRepository,
    private val quizRepository: QuizRepository,
    private val vcService: VcService
) : QuizBookService {

    @Transactional
    override fun createQuizBook(request: CreateQuizBookRequest): QuizBook {
        val userDetails = SecurityContextHolder.getContext().authentication.principal as UserDetailsImpl
        val user = userDetails.getUser()

        val newQuizBook = QuizBook(
            category = request.category,
            title = request.title,
            description = request.description,
            createdBy = user,
            level = request.level
        )
        return quizBookRepository.save(newQuizBook)
    }

    override fun getQuizBooksByCategory(category: String, user: User): List<GetQuizBookResponse> {
        val quizBooks = quizBookRepository.findByCategory(category)
        return quizBooks.map { quizBook ->
            val quizzes = quizRepository.findAllByQuizBookId(quizBook.id)

            val nickname = quizBook.createdBy?.nickName
            GetQuizBookResponse(
                id = quizBook.id,
                version = quizBook.version,
                category = quizBook.category,
                title = quizBook.title,
                description = quizBook.description,
                level = quizBook.level,
                createdBy = nickname,
                totalQuizzes = quizzes.size
            )
        }
    }

    override fun getQuizBookById(id: Long, user: User): GetQuizBookAndQuizSummaryResponse {
        val quizBook = quizBookRepository.findById(id)
            .orElseThrow { IllegalArgumentException("해당 ID의 퀴즈북이 존재하지 않습니다: $id") }
        val quizzes = quizRepository.findAllByQuizBookId(quizBook.id)
        val quizSummaries = quizzes.map { quiz ->
            QuizSummary(
                quizId = quiz.id,
                quizContent = quiz.content,
                quizType = quiz.questionType
            )
        }
        val nickname = quizBook.createdBy?.nickName

        return GetQuizBookAndQuizSummaryResponse(
            id = quizBook.id,
            version = quizBook.version,
            category = quizBook.category,
            title = quizBook.title,
            description = quizBook.description,
            level = quizBook.level,
            createdBy = nickname,
            quizzes = quizSummaries
        )
    }


    override fun getMyQuizBooks(user: User): List<GetQuizBookResponse> {
        val quizBooks = quizBookRepository.findByCreatedBy(user)
        return quizBooks.map { quizBook ->
            val quizzes = quizRepository.findAllByQuizBookId(quizBook.id)
            val nickname = quizBook.createdBy?.nickName
            GetQuizBookResponse(
                id = quizBook.id,
                version = quizBook.version,
                category = quizBook.category,
                title = quizBook.title,
                description = quizBook.description,
                level = quizBook.level,
                createdBy = nickname,
                totalQuizzes = quizzes.size
            )
        }
    }

    @Transactional
    override fun updateQuizBook(id: Long, request: UpdateQuizBookRequest, user: User) {
        val quizBook = quizBookRepository.findById(id)
            .orElseThrow { IllegalArgumentException("문제집을 찾을 수 없습니다.") }

        quizBook.createdBy?.let {
            if (it.id != user.id) {
                throw IllegalArgumentException("문제집을 수정할 권한이 없습니다.")
            }
        }

        vcService.save(quizBook.id, quizBook.version)

        request.category?.let { quizBook.category = it }
        request.title?.let { quizBook.title = it }
        request.description?.let { quizBook.description = it }
        request.level?.let { quizBook.level = it }
        quizBook.version++

    }

    override fun getQuizBook(id: Long): Optional<QuizBook> {
        return quizBookRepository.findById(id)
    }

    @Transactional
    override fun deleteQuizBook(id: Long, user: User) {
        val quizBook = quizBookRepository.findById(id)
            .orElseThrow { IllegalArgumentException("퀴즈북이 존재하지 않습니다.") }

        quizBook.createdBy?.let {
            if (it.id != user.id) {
                throw IllegalArgumentException("이 퀴즈북을 삭제할 권한이 없습니다.")
            }
        }

        quizBookRepository.delete(quizBook)
    }
}
