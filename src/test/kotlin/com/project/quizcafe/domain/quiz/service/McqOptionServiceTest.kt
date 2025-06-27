package com.project.quizcafe.domain.quiz.service

import com.project.quizcafe.common.exception.ForbiddenException
import com.project.quizcafe.common.exception.NotFoundException
import com.project.quizcafe.domain.quiz.dto.request.CreateMcqOptionRequest
import com.project.quizcafe.domain.quiz.dto.request.UpdateMcqOptionRequest
import com.project.quizcafe.domain.quiz.entity.McqOption
import com.project.quizcafe.domain.quiz.repository.McqOptionRepository
import com.project.quizcafe.domain.quiz.repository.QuizRepository
import com.project.quizcafe.domain.quizbook.validator.QuizBookValidator
import com.project.quizcafe.domain.util.createMcqOption
import com.project.quizcafe.domain.util.createQuiz
import com.project.quizcafe.domain.util.createQuizBook
import com.project.quizcafe.domain.util.createUser
import com.project.quizcafe.domain.versioncontrol.service.VcService
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import java.util.Optional
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
class McqOptionServiceTest {
    @RelaxedMockK
    private lateinit var mcqOptionRepository: McqOptionRepository

    @RelaxedMockK
    private lateinit var vcService: VcService

    @RelaxedMockK
    private lateinit var quizRepository: QuizRepository

    private lateinit var quizBookValidator: QuizBookValidator

    private lateinit var mcqOptionService: McqOptionService

    @BeforeEach
    fun setUp() {
        quizBookValidator = QuizBookValidator()
        mcqOptionService = McqOptionService(
            mcqOptionRepository = mcqOptionRepository,
            vcService = vcService,
            quizRepository = quizRepository,
            quizBookValidator = quizBookValidator
        )
    }

    @Test
    fun `createMcqOption, mcqOption 생성 후 저장`() {
        // given
        val createRequest = createCreateMcqOptionRequest(
            quizId = 4903,
            optionNumber = 3,
            optionContent = "test optionContent",
            isCorrect = true
        )
        val user = createUser()

        val quizBook = createQuizBook(createdBy = user)
        val quiz = createQuiz(id = 4903, quizBook = quizBook)
        every { quizRepository.findById(4903) } returns Optional.of(quiz)

        val mcqOptionSlot = slot<McqOption>()
        every { mcqOptionRepository.save(capture(mcqOptionSlot)) } answers { mcqOptionSlot.captured }


        // when
        mcqOptionService.createMcqOption(createRequest, user)

        // then
        val capturedMcqOption = mcqOptionSlot.captured
        assertAll(
            { assertEquals(4903, capturedMcqOption.quiz.id) },
            { assertEquals(3, capturedMcqOption.optionNumber) },
            { assertEquals("test optionContent", capturedMcqOption.optionContent) },
            { assertEquals(true, capturedMcqOption.isCorrect) },
        )
    }

    @Test
    fun `createMcqOption, mcqOption 생성시 퀴즈북 버전정보 업데이트`() {
        // given
        val createRequest = createCreateMcqOptionRequest(
            quizId = 4903,
            optionNumber = 3,
            optionContent = "test optionContent",
            isCorrect = true
        )
        val user = createUser()

        val quizBook = createQuizBook(id = 5432, createdBy = user, version = 1)
        val quiz = createQuiz(id = 4903, quizBook = quizBook)
        every { quizRepository.findById(4903) } returns Optional.of(quiz)

        val mcqOptionSlot = slot<McqOption>()
        every { mcqOptionRepository.save(capture(mcqOptionSlot)) } answers { mcqOptionSlot.captured }


        // when
        mcqOptionService.createMcqOption(createRequest, user)

        // then
        verify(exactly = 1) { vcService.save(5432, 2) }
    }

    @Test
    fun `createMcqOption, 존재하지 않는 퀴즈북의 mcqOption 생성시 NotFoundException`() {
        // given
        val createRequest = createCreateMcqOptionRequest(
            quizId = 4903,
            optionNumber = 3,
            optionContent = "test optionContent",
            isCorrect = true
        )
        val user = createUser()
        every { quizRepository.findById(4903) } returns Optional.empty()


        // when
        // then
        val exception = assertThrows(NotFoundException::class.java) {
            mcqOptionService.createMcqOption(createRequest, user)
        }
        assertEquals("해당 ID의 퀴즈가 존재하지 않습니다: 4903", exception.message)
    }

    @Test
    fun `createMcqOption, 자신이 생성하지 않은 퀴즈북의 mcqOption 생성시 ForbiddenException`() {
        // given
        val createRequest = createCreateMcqOptionRequest(
            quizId = 4903,
            optionNumber = 3,
            optionContent = "test optionContent",
            isCorrect = true
        )
        val user = createUser(id = 2231)
        val quizBookCreator = createUser(id = 6877)
        val quizBook = createQuizBook(createdBy = quizBookCreator)
        val quiz = createQuiz(id = 4903, quizBook = quizBook)
        every { quizRepository.findById(4903) } returns Optional.of(quiz)

        val mcqOptionSlot = slot<McqOption>()
        every { mcqOptionRepository.save(capture(mcqOptionSlot)) } answers { mcqOptionSlot.captured }


        // when
        // then
        val exception = assertThrows(ForbiddenException::class.java) {
            mcqOptionService.createMcqOption(createRequest, user)
        }
        assertEquals("문제집을 수정할 권한이 없습니다.", exception.message)
    }

    @Test
    fun `updateMcqOption, mcqOption 수정시 퀴즈북 버전 정보도 업데이트`() {
        // given
        val updateRequest = createUpdateMcqOptionRequest()
        val currentUser = createUser()
        val quizBook = createQuizBook(id = 6431, createdBy = currentUser, version = 4)
        val quiz = createQuiz(quizBook = quizBook)
        val mcqOption = createMcqOption(id = 2991, quiz = quiz)
        every { mcqOptionRepository.findById(2991) } returns Optional.of(mcqOption)

        // when
        mcqOptionService.updateMcqOption(2991, updateRequest, currentUser)

        // then
        verify(exactly = 1) { vcService.save(6431, 5) }
    }

    @Test
    fun `updateMcqOption, 존재하지 않는 mcqOption 수정시 NotFoundException`() {
        // given
        val updateRequest = createUpdateMcqOptionRequest()
        val currentUser = createUser()
        every { mcqOptionRepository.findById(2991) } returns Optional.empty()

        // when
        // then
        val exception = assertThrows(NotFoundException::class.java) {
            mcqOptionService.updateMcqOption(2991, updateRequest, currentUser)
        }
        assertEquals("해당 ID의 객관식 보기가 존재하지 않습니다: 2991", exception.message)
    }

    @Test
    fun `updateMcqOption, 자신이 생성하지 않은 퀴즈북의 mcqOption 수정시 ForbiddenException`() {
        // given
        val updateRequest = createUpdateMcqOptionRequest()
        val currentUser = createUser(id = 1100)
        val quizBookCreator = createUser(id = 9900)
        val quizBook = createQuizBook(id = 6431, createdBy = quizBookCreator, version = 4)
        val quiz = createQuiz(quizBook = quizBook)
        val mcqOption = createMcqOption(id = 2991, quiz = quiz)
        every { mcqOptionRepository.findById(2991) } returns Optional.of(mcqOption)

        // when
        // then
        val exception = assertThrows(ForbiddenException::class.java) {
            mcqOptionService.updateMcqOption(2991, updateRequest, currentUser)
        }
        assertEquals("문제집을 수정할 권한이 없습니다.", exception.message)
    }

    @Test
    fun `deleteMcqOption, 해당 id의 mcqOption 삭제`() {
        // given
        val currentUser = createUser()
        val quizBook = createQuizBook(createdBy = currentUser)
        val quiz = createQuiz(quizBook = quizBook)
        val mcqOption = createMcqOption(id = 2991, quiz = quiz)
        every { mcqOptionRepository.findById(2991) } returns Optional.of(mcqOption)

        val mcqOptionSlot = slot<McqOption>()
        every { mcqOptionRepository.delete(capture(mcqOptionSlot)) } just Runs

        // when
        mcqOptionService.deleteMcqOption(2991, currentUser)

        // then
        val capturedMcqOption = mcqOptionSlot.captured
        assertEquals(2991, capturedMcqOption.id)
    }

    @Test
    fun `deleteMcqOption, 존재하지 않는 mcqOption 삭제시 NotFoundException`() {
        // given
        val currentUser = createUser()
        every { mcqOptionRepository.findById(2991) } returns Optional.empty()

        // when
        // then
        val exception = assertThrows(NotFoundException::class.java) {
            mcqOptionService.deleteMcqOption(2991, currentUser)
        }
        assertEquals("해당 ID의 객관식 보기가 존재하지 않습니다: 2991", exception.message)
    }

    @Test
    fun `deleteMcqOption, 자신이 생성하지 않은 퀴즈북의 mcqOption 삭제시 ForbiddenException`() {
        // given
        val currentUser = createUser(id = 1100)
        val quizBookCreator = createUser(id = 9900)
        val quizBook = createQuizBook(id = 6431, createdBy = quizBookCreator, version = 4)
        val quiz = createQuiz(quizBook = quizBook)
        val mcqOption = createMcqOption(id = 2991, quiz = quiz)
        every { mcqOptionRepository.findById(2991) } returns Optional.of(mcqOption)

        // when
        // then
        val exception = assertThrows(ForbiddenException::class.java) {
            mcqOptionService.deleteMcqOption(2991, currentUser)
        }
        assertEquals("문제집을 수정할 권한이 없습니다.", exception.message)
    }

    @Test
    fun `getMcqOptionsByQuizId, 해당 id 퀴즈의 mcqOption들을 return`() {
        // given
        val quizBook = createQuizBook()
        val quiz = createQuiz(id = 2939, quizBook = quizBook)
        val mcqOptions = (1L..5L).map {
            createMcqOption(id = it, quiz = quiz)
        }
        every { mcqOptionRepository.findByQuizId(2939) } returns mcqOptions

        // when
        val result = mcqOptionService.getMcqOptionsByQuizId(2939)

        // then
        assertAll(
            { assertEquals(5, result.size) },
            { assertEquals(true, result.all { it.quizId == 2939L }) },
        )
    }

    @Test
    fun `getMcqOptionsByQuizId, 해당 id 퀴즈의 mcqOption이 없을 경우 빈 리스트 return`() {
        // given
        every { mcqOptionRepository.findByQuizId(2939) } returns emptyList()

        // when
        val result = mcqOptionService.getMcqOptionsByQuizId(2939)

        // then
        assertEquals(true, result.isEmpty())
    }

    @Test
    fun `getMcqOptionsById, 해당 id의 mcqOption return`() {
        // given
        val quizBook = createQuizBook()
        val quiz = createQuiz(id = 2939, quizBook = quizBook)
        val mcqOption = createMcqOption(id = 2889, quiz = quiz)
        every { mcqOptionRepository.findById(2889) } returns Optional.of(mcqOption)

        // when
        val result = mcqOptionService.getMcqOptionsById(2889)

        // then
        assertEquals(2889, result.id)
    }

    @Test
    fun `getMcqOptionsById, 해당 id의 mcqOption이 존재하지 않을 경우 NotFoundException`() {
        // given
        every { mcqOptionRepository.findById(2889) } returns Optional.empty()

        // when
        // then
        val exception = assertThrows(NotFoundException::class.java) {
            mcqOptionService.getMcqOptionsById(2889)
        }
        assertEquals("해당 ID의 객관식 보기가 존재하지 않습니다: 2889", exception.message)
    }

    private fun createCreateMcqOptionRequest(
        quizId: Long = 1L,
        optionNumber: Int = 1,
        optionContent: String = "default optionContent",
        isCorrect: Boolean = false,
    ) = CreateMcqOptionRequest(
        quizId = quizId,
        optionNumber = optionNumber,
        optionContent = optionContent,
        isCorrect = isCorrect,
    )

    private fun createUpdateMcqOptionRequest(
        optionContent: String? = null,
        isCorrect: Boolean? = null,
    ) = UpdateMcqOptionRequest(
        optionContent = optionContent,
        isCorrect = isCorrect,
    )
}