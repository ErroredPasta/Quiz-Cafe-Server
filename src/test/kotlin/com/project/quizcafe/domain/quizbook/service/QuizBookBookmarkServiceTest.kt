package com.project.quizcafe.domain.quizbook.service

import com.project.quizcafe.common.exception.ConflictException
import com.project.quizcafe.common.exception.NotFoundException
import com.project.quizcafe.domain.quizbook.entity.QuizBookBookmark
import com.project.quizcafe.domain.quizbook.repository.QuizBookBookmarkRepository
import com.project.quizcafe.domain.quizbook.repository.QuizBookRepository
import com.project.quizcafe.domain.util.createQuizBook
import com.project.quizcafe.domain.util.createQuizBookBookmark
import com.project.quizcafe.domain.util.createUser
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.slot
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import java.util.Optional
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
class QuizBookBookmarkServiceTest {
    @RelaxedMockK
    private lateinit var quizBookBookmarkRepository: QuizBookBookmarkRepository

    @RelaxedMockK
    private lateinit var quizBookRepository: QuizBookRepository

    @InjectMockKs
    private lateinit var quizBookBookmarkService: QuizBookBookmarkService

    @Test
    fun `addBookmark, 유저의 북마크 생성 후 저장`() {
        // given
        val currentUser = createUser(id = 2211)
        val quizBook = createQuizBook(id = 3829)
        every { quizBookBookmarkRepository.existsByUserIdAndQuizBookId(2211, 3829) } returns false
        every { quizBookRepository.findById(3829) } returns Optional.of(quizBook)

        val bookmarkSlot = slot<QuizBookBookmark>()
        every { quizBookBookmarkRepository.save(capture(bookmarkSlot)) } answers { bookmarkSlot.captured }

        // when
        quizBookBookmarkService.addBookmark(currentUser, 3829)

        // then
        val capturedBookmark = bookmarkSlot.captured
        assertAll(
            { assertEquals(3829, capturedBookmark.quizBook.id) },
            { assertEquals(2211, capturedBookmark.user.id) },
        )
    }

    @Test
    fun `addBookmark, 이미 북마크가 설정되어 있는 경우 ConflictException`() {
        // given
        val currentUser = createUser(id = 2211)
        every { quizBookBookmarkRepository.existsByUserIdAndQuizBookId(2211, 3829) } returns true

        // when
        // then
        val exception = assertThrows(ConflictException::class.java) {
            quizBookBookmarkService.addBookmark(currentUser, 3829)
        }
        assertEquals("이미 북마크한 퀴즈북입니다.", exception.message)
    }

    @Test
    fun `addBookmark, 존재하지 않는 퀴즈북에 북마크 추가하는 경우 NotFoundException`() {
        // given
        val currentUser = createUser(id = 2211)
        every { quizBookBookmarkRepository.existsByUserIdAndQuizBookId(2211, 3829) } returns false
        every { quizBookRepository.findById(3829) } returns Optional.empty()


        // when
        // then
        val exception = assertThrows(NotFoundException::class.java) {
            quizBookBookmarkService.addBookmark(currentUser, 3829)
        }
        assertEquals("해당 ID의 퀴즈북이 존재하지 않습니다: 3829", exception.message)
    }

    @Test
    fun `removeBookmark, 북마크 삭제`() {
        // given
        val currentUser = createUser(id = 2211)
        val quizBook = createQuizBook(id = 3829)
        val bookmark = createQuizBookBookmark(user = currentUser, quizBook = quizBook)
        every { quizBookBookmarkRepository.findByUserIdAndQuizBookId(2211, 3829) } returns bookmark

        val bookmarkSlot = slot<QuizBookBookmark>()
        every { quizBookBookmarkRepository.delete(capture(bookmarkSlot)) } just Runs


        // when
        quizBookBookmarkService.removeBookmark(2211, 3829)

        // then
        val capturedBookmark = bookmarkSlot.captured
        assertAll(
            { assertEquals(3829, capturedBookmark.quizBook.id) },
            { assertEquals(2211, capturedBookmark.user.id) },
        )
    }

    @Test
    fun `removeBookmark, 존재하지 않는 북마크 삭제시 NotFoundException`() {
        // given
        every { quizBookBookmarkRepository.findByUserIdAndQuizBookId(2211, 3829) } returns null

        // when
        // then
        val exception = assertThrows(NotFoundException::class.java) {
            quizBookBookmarkService.removeBookmark(2211, 3829)
        }
        assertEquals("북마크가 존재하지 않습니다.", exception.message)
    }

    @Test
    fun `getBookmarksByUserId, 유저가 북마크한 퀴즈북의 id들을 return`() {
        // given
        val currentUser = createUser(id = 2211)
        val bookmarks = (1L..5L).map {
            val quizBook = createQuizBook(id = it)
            createQuizBookBookmark(id = it, quizBook = quizBook)
        }
        every { quizBookBookmarkRepository.findAllByUserId(2211) } returns bookmarks

        // when
        val result = quizBookBookmarkService.getBookmarksByUserId(currentUser)

        // then
        assertAll(
            { assertEquals(5, result.size) },
            { assertEquals(true, result.containsAll((1L..5L).toList())) },
        )
    }

    @Test
    fun `getBookmarksByUserId, 유저의 북마크가 없을 경우 빈 리스트 return`() {
        // given
        val currentUser = createUser(id = 2211)
        every { quizBookBookmarkRepository.findAllByUserId(2211) } returns emptyList()

        // when
        val result = quizBookBookmarkService.getBookmarksByUserId(currentUser)

        // then
        assertEquals(true, result.isEmpty())
    }
}
