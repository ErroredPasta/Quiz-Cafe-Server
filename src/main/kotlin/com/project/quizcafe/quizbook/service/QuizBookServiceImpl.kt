package com.project.quizcafe.quizbook.service

import com.project.quizcafe.auth.security.UserDetailsImpl
import com.project.quizcafe.quizbook.dto.request.CreateQuizBookRequest
import com.project.quizcafe.quizbook.dto.request.UpdateQuizBookRequest
import com.project.quizcafe.quizbook.dto.response.GetQuizBookResponse
import com.project.quizcafe.quizbook.entity.QuizBook
import com.project.quizcafe.quizbook.repository.QuizBookRepository
import com.project.quizcafe.user.entity.User
import com.project.quizcafe.user.repository.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class QuizBookServiceImpl(
    private val quizBookRepository: QuizBookRepository,
    private val userRepository: UserRepository
) : QuizBookService {

    @Transactional
    override fun createQuizBook(request: CreateQuizBookRequest): QuizBook {
        val userDetails = SecurityContextHolder.getContext().authentication.principal as UserDetailsImpl
        val user = userDetails.getUser()

        val newQuizBook = QuizBook(
            category = request.category,
            title = request.title,
            description = request.description,
            createdBy = user
        )
        return quizBookRepository.save(newQuizBook)
    }

    override fun getQuizBooksByCategory(category: String): List<GetQuizBookResponse> {
        val quizBooks = quizBookRepository.findByCategory(category)
        return quizBooks.map { GetQuizBookResponse.from(it) }
    }

    override fun getMyQuizBooks(user: User): List<GetQuizBookResponse> {
        val quizBooks = quizBookRepository.findByCreatedBy(user)
        return quizBooks.map { GetQuizBookResponse.from(it) }
    }

    @Transactional
    override fun updateQuizBook(id: Long, request: UpdateQuizBookRequest, user: User){
        val quizBook = quizBookRepository.findById(id)
            .orElseThrow { IllegalArgumentException("문제집을 찾을 수 없습니다.") }

        quizBook.createdBy?.let {
            if (it.id != user.id) {
                throw IllegalArgumentException("문제집을 수정할 권한이 없습니다.")
            }
        }

        request.category?.let { quizBook.category = it }
        request.title?.let { quizBook.title = it }
        request.description?.let { quizBook.description = it }

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
