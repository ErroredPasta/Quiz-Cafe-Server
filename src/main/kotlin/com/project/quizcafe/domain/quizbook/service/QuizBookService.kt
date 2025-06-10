package com.project.quizcafe.domain.quizbook.service

import com.project.quizcafe.domain.quiz.repository.QuizRepository
import com.project.quizcafe.domain.quizbook.dto.request.CreateQuizBookRequest
import com.project.quizcafe.domain.quizbook.dto.request.UpdateQuizBookRequest
import com.project.quizcafe.domain.quizbook.dto.response.GetQuizBookAndQuizSummaryResponse
import com.project.quizcafe.domain.quizbook.dto.response.GetQuizBookResponse
import com.project.quizcafe.domain.quizbook.entity.QuizBook
import com.project.quizcafe.domain.quizbook.extentions.applyTo
import com.project.quizcafe.domain.quizbook.extentions.toGetQuizBookAndQuizSummaryResponse
import com.project.quizcafe.domain.quizbook.extentions.toGetQuizBookResponse
import com.project.quizcafe.domain.quizbook.repository.QuizBookBookmarkRepository
import com.project.quizcafe.domain.quizbook.repository.QuizBookRepository
import com.project.quizcafe.domain.quizbook.extentions.toQuizBook
import com.project.quizcafe.domain.quizbook.validator.QuizBookValidator
import com.project.quizcafe.domain.quizbooksolving.repository.QuizBookSolvingRepository
import com.project.quizcafe.domain.user.entity.User
import com.project.quizcafe.domain.versioncontrol.service.VcService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class QuizBookService(
    private val quizBookRepository: QuizBookRepository,
    private val quizRepository: QuizRepository,
    private val vcService: VcService,
    private val quizBookBookmarkRepository: QuizBookBookmarkRepository,
    private val quizBookSolvingRepository: QuizBookSolvingRepository,
    private val quizBookValidator: QuizBookValidator
    ){

    @Transactional
    fun createQuizBook(request: CreateQuizBookRequest, user: User): QuizBook {
        val newQuizBook = request.toQuizBook(user)
        val savedQuizBook: QuizBook = quizBookRepository.save(newQuizBook)

        vcService.save(savedQuizBook.id, savedQuizBook.version)

        return savedQuizBook
    }

    fun getQuizBooksByCategory(category: String, user: User): List<GetQuizBookResponse> {
        val quizBooks = quizBookRepository.findAllByCategory(category)

        return quizBooks.map { quizBook ->
            val quizzes = quizRepository.findAllByQuizBookId(quizBook.id)
            quizBook.toGetQuizBookResponse(quizzes)
        }
    }

    fun getQuizBookById(id: Long, user: User): GetQuizBookAndQuizSummaryResponse {
        val quizBook = quizBookValidator.validateQuizBookNotExist(id)
        val quizzes = quizRepository.findAllByQuizBookId(quizBook.id)

        val bookmarkCount = quizBookBookmarkRepository.findAllByQuizBookId(quizBook.id).size
        val averageCorrectCount = quizBookSolvingRepository.findAvgCorrectCountByQuizBookId(quizBook.id) ?: 0.0
        val isSaved = quizBookBookmarkRepository.findByUserIdAndQuizBookId(user.id, quizBook.id) != null

        return quizBook.toGetQuizBookAndQuizSummaryResponse(
            quizzes = quizzes,
            bookmarkCount = bookmarkCount,
            averageCorrectCount = averageCorrectCount,
            isSaved = isSaved
        )
    }


    fun getMyQuizBooks(user: User): List<GetQuizBookResponse> {
        val quizBooks = quizBookRepository.findByCreatedBy(user)
        return quizBooks.map { quizBook ->
            val quizzes = quizRepository.findAllByQuizBookId(quizBook.id)
            quizBook.toGetQuizBookResponse(quizzes)
        }
    }

    @Transactional
    fun updateQuizBook(id: Long, request: UpdateQuizBookRequest, user: User) {
        val quizBook = quizBookValidator.validateQuizBookNotExist(id)
        quizBookValidator.validateMyQuizBook(quizBook, user)

        request.applyTo(quizBook)
        quizBook.version++

        vcService.save(quizBook.id, quizBook.version)

    }

    @Transactional
    fun deleteQuizBook(id: Long, user: User) {
        val quizBook = quizBookValidator.validateQuizBookNotExist(id)
        quizBookValidator.validateMyQuizBook(quizBook, user)

        quizBookRepository.delete(quizBook)
    }
}
