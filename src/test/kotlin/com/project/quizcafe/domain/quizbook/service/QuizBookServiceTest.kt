package com.project.quizcafe.domain.quizbook.service

import com.project.quizcafe.common.exception.ForbiddenException
import com.project.quizcafe.common.exception.NotFoundException
import com.project.quizcafe.domain.quiz.repository.QuizRepository
import com.project.quizcafe.domain.util.createQuiz
import com.project.quizcafe.domain.util.createQuizBook
import com.project.quizcafe.domain.util.createQuizBookBookmark
import com.project.quizcafe.domain.util.createUser
import com.project.quizcafe.domain.quizbook.dto.request.CreateQuizBookRequest
import com.project.quizcafe.domain.quizbook.dto.request.UpdateQuizBookRequest
import com.project.quizcafe.domain.quizbook.entity.QuizBook
import com.project.quizcafe.domain.quizbook.entity.QuizLevel
import com.project.quizcafe.domain.quizbook.repository.QuizBookBookmarkRepository
import com.project.quizcafe.domain.quizbook.repository.QuizBookRepository
import com.project.quizcafe.domain.quizbook.validator.QuizBookValidator
import com.project.quizcafe.domain.quizbooksolving.repository.QuizBookSolvingRepository
import com.project.quizcafe.domain.versioncontrol.service.VcService
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.util.Optional
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class QuizBookServiceTest {
    @Mock
    private lateinit var quizBookRepository: QuizBookRepository

    @Mock
    private lateinit var quizRepository: QuizRepository

    @Mock
    private lateinit var vcService: VcService

    @Mock
    private lateinit var quizBookBookmarkRepository: QuizBookBookmarkRepository

    @Mock
    private lateinit var quizBookSolvingRepository: QuizBookSolvingRepository
    private lateinit var quizBookValidator: QuizBookValidator

    private lateinit var quizBookService: QuizBookService

    @BeforeEach
    fun setup() {
        quizBookValidator = QuizBookValidator()
        quizBookService = QuizBookService(
            quizBookRepository = quizBookRepository,
            quizRepository = quizRepository,
            vcService = vcService,
            quizBookBookmarkRepository = quizBookBookmarkRepository,
            quizBookSolvingRepository = quizBookSolvingRepository,
            quizBookValidator = quizBookValidator
        )
    }

    @Test
    fun `createQuizBook, 올바른 request가 넘어올 경우 퀴즈북 생성 후 저장`() {
        // given
        val createRequest = createCreateQuizBookRequest(
            category = "test create category",
            title = "test create title",
            description = "test create description",
            level = QuizLevel.HARD
        )
        val currentUser = createUser()
        val quizBook = createQuizBook(createdBy = currentUser)
        `when`(quizBookRepository.save(any())).thenReturn(quizBook)

        // when
        quizBookService.createQuizBook(createRequest, currentUser)

        // then
        val captor = ArgumentCaptor.forClass(QuizBook::class.java)
        verify(quizBookRepository, times(1)).save(captor.capture())

        val capturedQuizBook = captor.value
        assertAll(
            { assertEquals(capturedQuizBook.category, "test create category") },
            { assertEquals(capturedQuizBook.title, "test create title") },
            { assertEquals(capturedQuizBook.description, "test create description") },
            { assertEquals(capturedQuizBook.level, QuizLevel.HARD) },
        )
    }

    @Test
    fun `createQuizBook, 퀴즈북 생성 시 버전 정보 저장`() {
        // given
        val createRequest = createCreateQuizBookRequest(
            category = "test create category",
            title = "test create title",
            description = "test create description",
            level = QuizLevel.HARD
        )
        val currentUser = createUser()
        val quizBook = createQuizBook(createdBy = currentUser)
        `when`(quizBookRepository.save(any())).thenReturn(quizBook)

        // when
        quizBookService.createQuizBook(createRequest, currentUser)

        // then
        verify(vcService).save(anyLong(), eq(1L))
    }

    @Test
    fun `getQuizBooksByCategory, 카테고리별 퀴즈북 return`() {
        // given
        val currentUser = createUser(id = 3321)
        val quizBooks = (1L..5L).map { createQuizBook(id = it, category = "test category") }
        `when`(quizBookRepository.findAllByCategory("test category")).thenReturn(quizBooks)

        // when
        val result = quizBookService.getQuizBooksByCategory("test category", currentUser)

        // then
        assertAll(
            { assertEquals(result.size, 5) },
            { assertEquals(result.first().id, 1) },
            { assertEquals(result.last().id, 5) },
            { assertEquals(result.all { it.category == "test category" }, true) },
        )
    }

    @Test
    fun `getQuizBooksByCategory, 카테고리의 퀴즈북이 없을 경우 빈 리스트 return`() {
        // given
        val currentUser = createUser(id = 3321)
        `when`(quizBookRepository.findAllByCategory("another category")).thenReturn(emptyList())

        // when
        val result = quizBookService.getQuizBooksByCategory("another category", currentUser)

        // then
        assertEquals(result.isEmpty(), true)
    }

    @Test
    fun `getQuizBookById, 퀴즈북과 요약에 관한 정보 return`() {
        // given
        val quizBook = createQuizBook(id = 3231, version = 3, createdBy = null)
        val quizzes = (1L..10L).map { createQuiz(id = it, quizBook = quizBook) }
        val bookmarks = (1L..5L).map { createQuizBookBookmark(id = it, quizBook = quizBook) }
        val currentUser = createUser(id = 2213)
        val currentUserBookmark = createQuizBookBookmark(quizBook = quizBook, user = currentUser)

        `when`(quizBookRepository.findById(3231)).thenReturn(Optional.of(quizBook))
        `when`(quizRepository.findAllByQuizBookId(3231)).thenReturn(quizzes)
        `when`(quizBookBookmarkRepository.findAllByQuizBookId(3231)).thenReturn(bookmarks)
        `when`(quizBookSolvingRepository.findAvgCorrectCountByQuizBookId(3231)).thenReturn(3.3)
        `when`(quizBookBookmarkRepository.findByUserIdAndQuizBookId(2213, 3231)).thenReturn(currentUserBookmark)

        // when
        val result = quizBookService.getQuizBookById(3231, currentUser)

        // then
        assertAll(
            { assertEquals(result.id, 3231) },
            { assertEquals(result.averageCorrectCount, 3.3) },
            { assertEquals(result.version, 3) },
            { assertEquals(result.totalSaves, 5) },
            { assertEquals(result.createdBy, null) },
            { assertEquals(result.isSaved, true) },
        )
    }

    @Test
    fun `getQuizBookById, 퀴즈북에 퀴즈가 없을 경우 quizzes에 빈 리스트`() {
        // given
        val quizBook = createQuizBook(id = 3231, version = 1, createdBy = null)
        val bookmarks = (1L..5L).map { createQuizBookBookmark(id = it, quizBook = quizBook) }
        val currentUser = createUser(id = 2213)
        val currentUserBookmark = createQuizBookBookmark(quizBook = quizBook, user = currentUser)

        `when`(quizBookRepository.findById(3231)).thenReturn(Optional.of(quizBook))
        `when`(quizRepository.findAllByQuizBookId(3231)).thenReturn(emptyList())
        `when`(quizBookBookmarkRepository.findAllByQuizBookId(3231)).thenReturn(bookmarks)
        `when`(quizBookSolvingRepository.findAvgCorrectCountByQuizBookId(3231)).thenReturn(null)
        `when`(quizBookBookmarkRepository.findByUserIdAndQuizBookId(2213, 3231)).thenReturn(currentUserBookmark)

        // when
        val result = quizBookService.getQuizBookById(3231, currentUser)

        // then
        assertAll(
            { assertEquals(result.id, 3231) },
            { assertEquals(result.averageCorrectCount, 0.0) },
            { assertEquals(result.version, 1) },
            { assertEquals(result.totalSaves, 5) },
            { assertEquals(result.createdBy, null) },
            { assertEquals(result.isSaved, true) },
            { assertEquals(result.quizzes.isEmpty(), true) },
        )
    }

    @Test
    fun `getQuizBookById, 퀴즈북이 존재하지 않을 경우 NotFoundException`() {
        // given
        val currentUser = createUser(id = 2213)
        `when`(quizBookRepository.findById(3231)).thenReturn(Optional.empty())

        // when
        // then
        val exception = assertThrows(NotFoundException::class.java) {
            quizBookService.getQuizBookById(3231, currentUser)
        }
        assertEquals(exception.message, "해당 ID의 퀴즈북이 존재하지 않습니다: 3231")
    }

    @Test
    fun `getMyQuizBooks, 현재 사용자의 퀴즈북을 return`() {
        // given
        val currentUser = createUser(id = 3231)
        val quizBooks = (1L..5L).map { createQuizBook(id = it) }

        `when`(quizBookRepository.findByCreatedBy(currentUser)).thenReturn(quizBooks)

        (1L..5L).forEach { it ->
            if (it == 5L) {
                `when`(quizRepository.findAllByQuizBookId(it)).thenReturn(emptyList())
            } else {
                val quizBook = quizBooks[it.toInt()]
                val quizzes = (1L..10L).map { createQuiz(id = it, quizBook = quizBook) }
                `when`(quizRepository.findAllByQuizBookId(it)).thenReturn(quizzes)
            }
        }

        // when
        val result = quizBookService.getMyQuizBooks(currentUser)

        // then
        assertAll(
            { assertEquals(result.size, 5) },
            { assertEquals(result.first().id, 1) },
            { assertEquals(result.first().totalQuizzes, 10) },
            { assertEquals(result.last().id, 5) },
            { assertEquals(result.last().totalQuizzes, 0) },
            { assertEquals(result.size, 5) },
        )
    }

    @Test
    fun `getMyQuizBooks, 현재 사용자의 퀴즈북이 없으면 빈 리스트 return`() {
        // given
        val currentUser = createUser(id = 3231)
        `when`(quizBookRepository.findByCreatedBy(currentUser)).thenReturn(emptyList())

        // when
        val result = quizBookService.getMyQuizBooks(currentUser)

        // then
        assertEquals(result.isEmpty(), true)
    }

    @Test
    fun `updateQuizBook, 퀴즈북 업데이트 시 버전도 업데이트`() {
        // given
        val updateRequest = createUpdateQuizBookRequest(
            category = "test update category",
            title = "test update title",
            description = "test update description",
            level = QuizLevel.EASY
        )
        val currentUser = createUser(id = 3321)
        val quizBook = createQuizBook(id = 3231, version = 3, createdBy = currentUser)

        `when`(quizBookRepository.findById(3231)).thenReturn(Optional.of(quizBook))

        // when
        quizBookService.updateQuizBook(3231, updateRequest, currentUser)

        // then
        verify(vcService, times(1)).save(eq(3231L), eq(4L))
    }

    @Test
    fun `updateQuizBook, 존재하지 않는 퀴즈북 업데이트시 NotFoundException`() {
        // given
        val updateRequest = createUpdateQuizBookRequest(
            category = "test update category",
            title = "test update title",
            description = "test update description",
            level = QuizLevel.EASY
        )
        val currentUser = createUser(id = 3321)
        `when`(quizBookRepository.findById(3231)).thenReturn(Optional.empty())

        // when
        // then
        val exception = assertThrows(NotFoundException::class.java) {
            quizBookService.updateQuizBook(3231, updateRequest, currentUser)
        }
        assertEquals(exception.message, "해당 ID의 퀴즈북이 존재하지 않습니다: 3231")
    }

    @Test
    fun `updateQuizBook, 자신이 만들지 않은 퀴즈북 업데이트시 ForbiddenException`() {
        // given
        val updateRequest = createUpdateQuizBookRequest(
            category = "test update category",
            title = "test update title",
            description = "test update description",
            level = QuizLevel.EASY
        )
        val currentUser = createUser(id = 3321)
        val quizBookCreator = createUser(id = 1199)
        val quizBook = createQuizBook(id = 3231, version = 3, createdBy = quizBookCreator)
        `when`(quizBookRepository.findById(3231)).thenReturn(Optional.of(quizBook))

        // when
        // then
        val exception = assertThrows(ForbiddenException::class.java) {
            quizBookService.updateQuizBook(3231, updateRequest, currentUser)
        }
        assertEquals(exception.message, "문제집을 수정할 권한이 없습니다.")
    }

    @Test
    fun `deleteQuizBook, 자신이 만든 퀴즈북 삭제`() {
        // given
        val currentUser = createUser(id = 6634)
        val quizBook = createQuizBook(id = 5623, createdBy = currentUser)
        `when`(quizBookRepository.findById(5623)).thenReturn(Optional.of(quizBook))

        // when
        quizBookService.deleteQuizBook(5623, currentUser)

        // then
        val captor = ArgumentCaptor.forClass(QuizBook::class.java)
        verify(quizBookRepository).delete(captor.capture())

        assertEquals(captor.value.id, 5623)
    }

    @Test
    fun `deleteQuizBook, 존재하지 않는 퀴즈북 삭제시 NotFoundException`() {
        // given
        val currentUser = createUser(id = 6634)
        `when`(quizBookRepository.findById(5623)).thenReturn(Optional.empty())

        // when
        // then
        val exception = assertThrows(NotFoundException::class.java) {
            quizBookService.deleteQuizBook(5623, currentUser)
        }
        assertEquals(exception.message, "해당 ID의 퀴즈북이 존재하지 않습니다: 5623")
    }

    @Test
    fun `deleteQuizBook, 자신이 생성하지 않은 퀴즈북 삭제시 ForbiddenException`() {
        // given
        val currentUser = createUser(id = 6634)
        val quizBookCreator = createUser(id = 3344)
        val quizBook = createQuizBook(id = 5623, createdBy = quizBookCreator)
        `when`(quizBookRepository.findById(5623)).thenReturn(Optional.of(quizBook))

        // when
        // then
        val exception = assertThrows(ForbiddenException::class.java) {
            quizBookService.deleteQuizBook(5623, currentUser)
        }
        assertEquals(exception.message, "문제집을 수정할 권한이 없습니다.")
    }


    private fun createCreateQuizBookRequest(
        category: String = "default create category",
        title: String = "default create title",
        description: String = "default create description",
        level: QuizLevel = QuizLevel.EASY,
    ) = CreateQuizBookRequest(
        category = category,
        title = title,
        description = description,
        level = level
    )

    private fun createUpdateQuizBookRequest(
        category: String? = "default update category",
        title: String? = "default update title",
        description: String? = "default update description",
        level: QuizLevel? = QuizLevel.MEDIUM,
    ) = UpdateQuizBookRequest(
        category = category,
        title = title,
        description = description,
        level = level
    )
}