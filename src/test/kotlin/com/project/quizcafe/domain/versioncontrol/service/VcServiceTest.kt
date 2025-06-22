package com.project.quizcafe.domain.versioncontrol.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.project.quizcafe.common.exception.NotFoundException
import com.project.quizcafe.domain.quiz.dto.response.QuizResponse
import com.project.quizcafe.domain.quiz.service.QuizService
import com.project.quizcafe.domain.quizbook.entity.QuizBook
import com.project.quizcafe.domain.quizbook.repository.QuizBookRepository
import com.project.quizcafe.domain.util.createQuizBook
import com.project.quizcafe.domain.util.createQuizResponse
import com.project.quizcafe.domain.util.createUser
import com.project.quizcafe.domain.versioncontrol.dto.SavedQuizBook
import com.project.quizcafe.domain.versioncontrol.entity.Vc
import com.project.quizcafe.domain.versioncontrol.repository.VcRepository
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.Optional
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
class VcServiceTest {
    @RelaxedMockK
    private lateinit var vcRepository: VcRepository

    @RelaxedMockK
    private lateinit var quizService: QuizService

    @RelaxedMockK
    private lateinit var quizBookRepository: QuizBookRepository

    private lateinit var objectMapper: ObjectMapper

    private lateinit var vcService: VcService

    @BeforeEach
    fun setUp() {
        objectMapper = ObjectMapper()
        vcService = VcService(
            vcRepository = vcRepository,
            quizService = quizService,
            quizBookRepository = quizBookRepository,
            objectMapper = objectMapper
        )
    }

    @Test
    fun `save, 새로운 버전정보 저장`() {
        // given
        val quizBookCreator = createUser()
        val quizBook = createQuizBook(id = 1323, createdBy = quizBookCreator)
        val quizzes = (1L..5L).map { createQuizResponse(id = it, quizBookId = 1323) }
        every { quizService.getQuizzesByQuizBookId(1323) } returns quizzes
        every { quizBookRepository.findById(1323) } returns Optional.of(quizBook)

        val vcSlot = slot<Vc>()
        every { vcRepository.save(capture(vcSlot)) } answers { vcSlot.captured }

        // when
        vcService.save(1323, 3)

        // then
        val savedQuizBook = quizBook.toSavedQuizBook(quizBookCreator.nickName, quizzes)
        val expectedVc = Vc(
            quizBookId = 1323,
            version = 3,
            quizzesValue = objectMapper.writeValueAsString(savedQuizBook),
        )
        assertEquals(expectedVc, vcSlot.captured)
    }

    private fun QuizBook.toSavedQuizBook(
        creatorNickName: String,
        quizzes: List<QuizResponse>
    ) = SavedQuizBook(
        category = category,
        title = title,
        level = level,
        description = description,
        createdBy = creatorNickName,
        quizzes = quizzes
    )

    @Test
    fun `save, 퀴즈북 생성자가 없을 경우 quizzesValue에 null`() {
        // given
        val quizBook = createQuizBook(id = 1323, createdBy = null)
        val quizzes = (1L..5L).map { createQuizResponse(id = it, quizBookId = 1323) }
        every { quizService.getQuizzesByQuizBookId(1323) } returns quizzes
        every { quizBookRepository.findById(1323) } returns Optional.of(quizBook)

        val vcSlot = slot<Vc>()
        every { vcRepository.save(capture(vcSlot)) } answers { vcSlot.captured }

        // when
        vcService.save(1323, 3)

        // then
        assertEquals("null", vcSlot.captured.quizzesValue)
    }

    @Test
    fun `save, 해당 id의 퀴즈북이 없을 경우 NotFoundException`() {
        // given
        every { quizService.getQuizzesByQuizBookId(1323) } returns emptyList()
        every { quizBookRepository.findById(1323) } returns Optional.empty()

        // when
        // then
        val exception = assertThrows(NotFoundException::class.java) {
            vcService.save(1323, 3)
        }
        assertEquals("퀴즈북을 찾을수 없습니다.", exception.message)
    }
}