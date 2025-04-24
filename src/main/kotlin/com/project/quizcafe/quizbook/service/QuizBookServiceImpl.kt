package com.project.quizcafe.quizbook.service

import com.project.quizcafe.auth.security.UserDetailsImpl
import com.project.quizcafe.quizbook.dto.request.CreateQuizBookRequest
import com.project.quizcafe.quizbook.entity.QuizBook
import com.project.quizcafe.quizbook.repository.QuizBookRepository
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

    override fun getQuizBook(id: Long): Optional<QuizBook> {
        return quizBookRepository.findById(id)
    }

    @Transactional
    override fun updateQuizBook(id: Long, updatedQuizBook: QuizBook): QuizBook {
        if (!quizBookRepository.existsById(id)) {
            throw IllegalArgumentException("QuizBook with id $id not found")
        }

        updatedQuizBook.id = id // 수정할 때는 ID를 그대로 두어야 함
        return quizBookRepository.save(updatedQuizBook)
    }

    @Transactional
    override fun deleteQuizBook(id: Long) {
        if (!quizBookRepository.existsById(id)) {
            throw IllegalArgumentException("QuizBook with id $id not found")
        }
        quizBookRepository.deleteById(id)
    }
}
