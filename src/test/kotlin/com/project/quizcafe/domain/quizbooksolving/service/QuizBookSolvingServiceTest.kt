package com.project.quizcafe.domain.quizbooksolving.service

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.ObjectMapper
import com.project.quizcafe.common.exception.ForbiddenException
import com.project.quizcafe.common.exception.NotFoundException
import com.project.quizcafe.domain.quiz.entity.QuestionType
import com.project.quizcafe.domain.quiz.repository.McqOptionRepository
import com.project.quizcafe.domain.quiz.repository.QuizRepository
import com.project.quizcafe.domain.quiz.validator.QuizValidator
import com.project.quizcafe.domain.quizbook.repository.QuizBookRepository
import com.project.quizcafe.domain.quizbooksolving.dto.request.CreateQuizBookSolvingRequest
import com.project.quizcafe.domain.quizbooksolving.dto.request.UpdateQuizBookSolvingRequest
import com.project.quizcafe.domain.quizbooksolving.entity.QuizBookSolving
import com.project.quizcafe.domain.quizbooksolving.repository.QuizBookSolvingRepository
import com.project.quizcafe.domain.quizbooksolving.validator.QuizBookSolvingValidator
import com.project.quizcafe.domain.quizsolving.dto.request.CreateQuizSolvingRequest
import com.project.quizcafe.domain.quizsolving.entity.QuizSolving
import com.project.quizcafe.domain.quizsolving.repository.QuizSolvingRepository
import com.project.quizcafe.domain.quizsolving.validator.QuizSolvingValidator
import com.project.quizcafe.domain.util.createQuiz
import com.project.quizcafe.domain.util.createQuizBook
import com.project.quizcafe.domain.util.createQuizBookSolving
import com.project.quizcafe.domain.util.createQuizResponse
import com.project.quizcafe.domain.util.createQuizSolving
import com.project.quizcafe.domain.util.createUser
import com.project.quizcafe.domain.util.toSavedQuizBook
import com.project.quizcafe.domain.versioncontrol.entity.Vc
import com.project.quizcafe.domain.versioncontrol.repository.VcRepository
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.slot
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDateTime
import java.util.Optional
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
class QuizBookSolvingServiceTest {
    @RelaxedMockK
    private lateinit var quizBookSolvingRepository: QuizBookSolvingRepository

    @RelaxedMockK
    private lateinit var quizSolvingRepository: QuizSolvingRepository

    @RelaxedMockK
    private lateinit var quizRepository: QuizRepository

    @RelaxedMockK
    private lateinit var vcRepository: VcRepository

    @RelaxedMockK
    private lateinit var mcqOptionRepository: McqOptionRepository

    @RelaxedMockK
    private lateinit var quizValidator: QuizValidator

    private lateinit var quizBookSolvingValidator: QuizBookSolvingValidator

    private lateinit var quizSolvingValidator: QuizSolvingValidator

    @RelaxedMockK
    private lateinit var quizBookRepository: QuizBookRepository

    private lateinit var quizBookSolvingService: QuizBookSolvingService

    @BeforeEach
    fun setUp() {
        quizSolvingValidator = QuizSolvingValidator()
        quizBookSolvingValidator = QuizBookSolvingValidator()
        quizBookSolvingService = QuizBookSolvingService(
            quizBookSolvingRepository = quizBookSolvingRepository,
            quizSolvingRepository = quizSolvingRepository,
            quizRepository = quizRepository,
            vcRepository = vcRepository,
            mcqOptionRepository = mcqOptionRepository,
            quizValidator = quizValidator,
            quizBookSolvingValidator = quizBookSolvingValidator,
            quizSolvingValidator = quizSolvingValidator,
            quizBookRepository = quizBookRepository
        )
    }

    @Test
    fun `createQuizBookSolving, quizBookSolving 생성 후 저장`() {
        // given
        val currentUser = createUser()
        val createRequest = createCreateQuizBookSolvingRequest(
            quizBookId = 3323,
            version = 2L,
            totalQuizzes = 5,
            correctCount = 3,
            completedAt = LocalDateTime.of(2025, 1, 1, 1, 0),
            solvingTime = 1234L,
        )
        val quizBook = createQuizBook(id = 3323)

        every { quizBookRepository.findById(3323) } returns Optional.of(quizBook)

        val quizBookSolvingSlot = slot<QuizBookSolving>()
        every { quizBookSolvingRepository.save(capture(quizBookSolvingSlot)) } answers { quizBookSolvingSlot.captured }

        // when
        quizBookSolvingService.createQuizBookSolving(createRequest, currentUser)

        // then
        val capturedQuizBookSolving = quizBookSolvingSlot.captured
        assertAll(
            { assertEquals(currentUser.id, capturedQuizBookSolving.user.id) },
            { assertEquals(3323, capturedQuizBookSolving.quizBook.id) },
            { assertEquals(2L, capturedQuizBookSolving.version) },
            { assertEquals(5, capturedQuizBookSolving.totalQuizzes) },
            { assertEquals(3, capturedQuizBookSolving.correctCount) },
            { assertEquals(LocalDateTime.of(2025, 1, 1, 1, 0), capturedQuizBookSolving.completedAt) },
            { assertEquals(1234L, capturedQuizBookSolving.solvingTimeSeconds) },
        )
    }

    @Test
    fun `createQuizBookSolving, 퀴즈북의 퀴즈에 관한 quizSolving도 저장`() {
        // given
        val currentUser = createUser()
        val createQuizSolvingRequests = (1L..5L).map { createCreateQuizSolvingRequest(quizId = it) }
        val createRequest = createCreateQuizBookSolvingRequest(
            quizBookId = 3323,
            version = 2L,
            totalQuizzes = 5,
            correctCount = 3,
            completedAt = LocalDateTime.of(2025, 1, 1, 1, 0),
            solvingTime = 1234L,
            quizzes = createQuizSolvingRequests
        )
        val quizBook = createQuizBook(id = 3323)
        every { quizBookRepository.findById(3323) } returns Optional.of(quizBook)

        val quizBookSolvingSlot = slot<QuizBookSolving>()
        every { quizBookSolvingRepository.save(capture(quizBookSolvingSlot)) } answers { quizBookSolvingSlot.captured }

        val quizIdSlot = slot<Long>()
        every { quizRepository.findById(capture(quizIdSlot)) } answers {
            Optional.of(createQuiz(id = quizIdSlot.captured, quizBook = quizBook))
        }

        val capturedQuizSolvings = mutableListOf<QuizSolving>()
        every { quizSolvingRepository.save(capture(capturedQuizSolvings)) } answers { capturedQuizSolvings.last() }

        // when
        quizBookSolvingService.createQuizBookSolving(createRequest, currentUser)

        // then
        val capturedQuizSolvingQuizIds = capturedQuizSolvings.map { it.quiz.id }.sorted()
        assertAll(
            { assertEquals(5, capturedQuizSolvings.size) },
            { assertEquals((1L..5L).toList(), capturedQuizSolvingQuizIds) }
        )
    }

    @Test
    fun `createQuizBookSolving, 존재하지 않는 퀴즈북의 quizBookSolving 생성시 NotFoundException`() {
        // given
        val currentUser = createUser()
        val createRequest = createCreateQuizBookSolvingRequest(
            quizBookId = 3323,
            version = 2L,
            totalQuizzes = 5,
            correctCount = 3,
            completedAt = LocalDateTime.of(2025, 1, 1, 1, 0),
            solvingTime = 1234L,
        )
        every { quizBookRepository.findById(3323) } returns Optional.empty()

        // when
        // then
        val exception = assertThrows(NotFoundException::class.java) {
            quizBookSolvingService.createQuizBookSolving(createRequest, currentUser)
        }
        assertEquals("해당 ID의 퀴즈북이 존재하지 않습니다: 3323", exception.message)
    }

    @Test
    fun `createQuizBookSolving, quizBookSolving의 quizzes를 찾을 수 없을 경우 NotFoundException`() {
        // given
        val currentUser = createUser()
        val createQuizSolvingRequests = listOf(createCreateQuizSolvingRequest(quizId = 5453L))
        val createRequest = createCreateQuizBookSolvingRequest(
            quizBookId = 3323,
            version = 2L,
            totalQuizzes = 5,
            correctCount = 3,
            completedAt = LocalDateTime.of(2025, 1, 1, 1, 0),
            solvingTime = 1234L,
            quizzes = createQuizSolvingRequests
        )
        val quizBook = createQuizBook(id = 3323)

        every { quizBookRepository.findById(3323) } returns Optional.of(quizBook)
        every { quizRepository.findById(5453) } returns Optional.empty()

        val quizBookSolvingSlot = slot<QuizBookSolving>()
        every { quizBookSolvingRepository.save(capture(quizBookSolvingSlot)) } answers {
            quizBookSolvingSlot.captured
        }

        // when
        // then
        val exception = assertThrows(NotFoundException::class.java) {
            quizBookSolvingService.createQuizBookSolving(createRequest, currentUser)
        }
        assertEquals("해당 ID의 퀴즈가 존재하지 않습니다: 5453", exception.message)
    }

    @Test
    fun `updateQuizBookSolving, 존재하지 않는 quizBookSolving 수정시 NotFoundException`() {
        // given
        val updateRequest = createUpdateQuizBookSolvingRequest(solvingTime = 3214L)
        val currentUser = createUser(id = 1122)
        every { quizBookSolvingRepository.findById(3323) } returns Optional.empty()

        // when
        // then
        val exception = assertThrows(NotFoundException::class.java) {
            quizBookSolvingService.updateQuizBookSolving(3323, updateRequest, currentUser)
        }
        assertEquals("해당 ID의 문제 풀이가 존재하지 않습니다: 3323", exception.message)
    }

    @Test
    fun `updateQuizBookSolving, 자신이 생성하지 않은 quizBookSolving 수정시 ForbiddenException`() {
        // given
        val updateRequest = createUpdateQuizBookSolvingRequest(solvingTime = 3214L)
        val currentUser = createUser(id = 1122)
        val quizBook = createQuizBook()
        val quizBookSolver = createUser(id = 2211)
        val quizBookSolving = createQuizBookSolving(
            id = 3323,
            user = quizBookSolver,
            quizBook = quizBook
        )
        every { quizBookSolvingRepository.findById(3323) } returns Optional.of(quizBookSolving)

        // when
        // then
        val exception = assertThrows(ForbiddenException::class.java) {
            quizBookSolvingService.updateQuizBookSolving(3323, updateRequest, currentUser)
        }
        assertEquals("조회 권한이 없습니다.", exception.message)
    }

    @Test
    fun `deleteQuizBookSolving, 해당 id의 quizBookSolving 삭제`() {
        // given
        val currentUser = createUser(id = 1122)
        val quizBook = createQuizBook()
        val quizBookSolving = createQuizBookSolving(id = 3323, user = currentUser, quizBook = quizBook)
        every { quizBookSolvingRepository.findById(3323) } returns Optional.of(quizBookSolving)

        val quizBookSolvingSlot = slot<QuizBookSolving>()
        every { quizBookSolvingRepository.delete(capture(quizBookSolvingSlot)) } just Runs

        // when
        quizBookSolvingService.deleteQuizBookSolving(3323, currentUser)

        // then
        val capturedQuizBookSolving = quizBookSolvingSlot.captured
        assertEquals(3323, capturedQuizBookSolving.id)
    }

    @Test
    fun `deleteQuizBookSolving, 존재하지 않는 quizBookSolving 삭제시 NotFoundException`() {
        // given
        val currentUser = createUser(id = 1122)
        every { quizBookSolvingRepository.findById(3323) } returns Optional.empty()

        // when
        // then
        val exception = assertThrows(NotFoundException::class.java) {
            quizBookSolvingService.deleteQuizBookSolving(3323, currentUser)
        }
        assertEquals("해당 ID의 문제 풀이가 존재하지 않습니다: 3323", exception.message)
    }

    @Test
    fun `deleteQuizBookSolving, 자신이 생성하지 않은 quizBookSolving 삭제시 ForbiddenException`() {
        // given
        val currentUser = createUser(id = 1122)
        val quizBook = createQuizBook()
        val quizBookSolver = createUser(id = 2211)
        val quizBookSolving = createQuizBookSolving(id = 3323, user = quizBookSolver, quizBook = quizBook)
        every { quizBookSolvingRepository.findById(3323) } returns Optional.of(quizBookSolving)

        // when
        // then
        val exception = assertThrows(ForbiddenException::class.java) {
            quizBookSolvingService.deleteQuizBookSolving(3323, currentUser)
        }
        assertEquals("조회 권한이 없습니다.", exception.message)
    }

    @Test
    fun `getAllByUserId, 유저가 생성한 quizBookSolving을 모두 가져옴`() {
        // given
        val currentUser = createUser(id = 2929, nickName = "test nickName")
        val quizBooks = (1L..5L).map { createQuizBook(id = it, version = 1) }
        val quizBookSolvings = (1L..5L).map {
            createQuizBookSolving(
                id = it,
                quizBook = quizBooks[it.toInt() - 1],
                user = currentUser
            )
        }
        every { quizBookSolvingRepository.findByUserId(2929) } returns quizBookSolvings

        val objectMapper = ObjectMapper()
        for (id in 1L..5L) {
            val quizBookId = id
            val quizId = id
            val quiz = createQuiz(
                id = quizId,
                quizBook = quizBooks[id.toInt() - 1],
                questionType = QuestionType.SHORT_ANSWER
            )

            every { quizRepository.findById(quizId) } returns Optional.of(quiz)

            val quizSolvingId = id
            val quizSolving = createQuizSolving(
                id = quizSolvingId,
                quiz = quiz,
                quizBookSolving = quizBookSolvings[quizSolvingId.toInt() - 1],
                user = currentUser
            )

            every {
                quizSolvingRepository.findByQuizBookSolvingIdAndQuizId(id, quizId)
            } returns quizSolving

            every { mcqOptionRepository.findByQuizId(any()) } returns emptyList()

            val quizzes = listOf(createQuizResponse(id = quizId))
            val savedQuizBook = createQuizBook(id = quizBookId, version = 1)
                .toSavedQuizBook("test nickName", quizzes)
            val vc = Vc(
                id = id,
                quizBookId = quizBookId,
                version = 1,
                quizzesValue = objectMapper.writeValueAsString(savedQuizBook)
            )

            every { vcRepository.findByQuizBookIdAndVersion(quizBookId, 1) } returns vc
        }

        // when
        val result = quizBookSolvingService.getAllByUserId(currentUser)

        // then
        assertAll(
            { assertEquals(5, result.size) },
            { assertEquals(true, result.all { it.userId == 2929L }) },
            { assertEquals(true, result.all { it.quizzes.size == 1 }) },
        )
    }

    @Test
    fun `getAllByUserId, 해당 id의 quizBookSolving이 없을 경우 빈리스트 return`() {
        // given
        val currentUser = createUser(id = 2929, nickName = "test nickName")
        every { quizBookSolvingRepository.findByUserId(2929) } returns emptyList()

        // when
        val result = quizBookSolvingService.getAllByUserId(currentUser)

        // then
        assertEquals(true, result.isEmpty())
    }

    @Test
    fun `getAllByUserId, 퀴즈에 대한 버전정보가 없을 경우 RuntimeException`() {
        // given
        val currentUser = createUser(id = 2929, nickName = "test nickName")
        val quizBooks = (1L..5L).map { createQuizBook(id = it, version = 1) }
        val quizBookSolvings = (1L..5L).map {
            createQuizBookSolving(
                id = it,
                quizBook = quizBooks[it.toInt() - 1],
                user = currentUser
            )
        }
        every { quizBookSolvingRepository.findByUserId(2929) } returns quizBookSolvings
        every { vcRepository.findByQuizBookIdAndVersion(any(), any()) } returns null

        // when
        // then
        val exception = assertThrows(RuntimeException::class.java) {
            quizBookSolvingService.getAllByUserId(currentUser)
        }
        assertEquals("퀴즈북 버전을 찾을 수 없습니다.", exception.message)
    }

    @Test
    fun `getAllByUserId, 버전정보에 올바르지 않은 quizzesValue가 있을 경우 JsonParseException`() {
        // given
        val currentUser = createUser(id = 2929, nickName = "test nickName")
        val quizBooks = (1L..5L).map { createQuizBook(id = it, version = 1) }
        val quizBookSolvings = (1L..5L).map {
            createQuizBookSolving(
                id = it,
                quizBook = quizBooks[it.toInt() - 1],
                user = currentUser
            )
        }
        every { quizBookSolvingRepository.findByUserId(2929) } returns quizBookSolvings

        for (id in 1L..5L) {
            val quizBookId = id
            val quizId = id
            val quiz = createQuiz(
                id = quizId,
                quizBook = quizBooks[id.toInt() - 1],
                questionType = QuestionType.SHORT_ANSWER
            )

            every { quizRepository.findById(quizId) } returns Optional.of(quiz)

            val quizSolvingId = id
            val quizSolving = createQuizSolving(
                id = quizSolvingId,
                quiz = quiz,
                quizBookSolving = quizBookSolvings[quizSolvingId.toInt() - 1],
                user = currentUser
            )

            every {
                quizSolvingRepository.findByQuizBookSolvingIdAndQuizId(id, quizId)
            } returns quizSolving

            every { mcqOptionRepository.findByQuizId(any()) } returns emptyList()

            val vc = Vc(
                id = id,
                quizBookId = quizBookId,
                version = 1,
                quizzesValue = "invalid quizzesValue"
            )

            every { vcRepository.findByQuizBookIdAndVersion(quizBookId, 1) } returns vc
        }

        // when
        // then
        assertThrows(JsonParseException::class.java) {
            quizBookSolvingService.getAllByUserId(currentUser)
        }
    }

    @Test
    fun `getAllByUserId, 버전정보에 저장된 퀴즈가 없을 경우 NotFoundException`() {
        // given
        val currentUser = createUser(id = 2929, nickName = "test nickName")
        val quizBooks = (1L..5L).map { createQuizBook(id = it, version = 1) }
        val quizBookSolvings = (1L..5L).map {
            createQuizBookSolving(
                id = it,
                quizBook = quizBooks[it.toInt() - 1],
                user = currentUser
            )
        }
        every { quizBookSolvingRepository.findByUserId(2929) } returns quizBookSolvings

        val objectMapper = ObjectMapper()
        for (id in 1L..5L) {
            val quizBookId = id
            val quizId = id

            every { quizRepository.findById(any()) } returns Optional.empty()

            val quizzes = listOf(createQuizResponse(id = quizId))
            val savedQuizBook = createQuizBook(id = quizBookId, version = 1)
                .toSavedQuizBook("test nickName", quizzes)
            val vc = Vc(
                id = id,
                quizBookId = quizBookId,
                version = 1,
                quizzesValue = objectMapper.writeValueAsString(savedQuizBook)
            )

            every { vcRepository.findByQuizBookIdAndVersion(quizBookId, 1) } returns vc
        }

        // when
        // then
        val exception = assertThrows(NotFoundException::class.java) {
            quizBookSolvingService.getAllByUserId(currentUser)
        }
        assertEquals(true, exception.message?.contains("해당 ID의 퀴즈가 존재하지 않습니다: "))
    }

    @Test
    fun `getQuizBookSolvingById, 해당 id의 quizBookSolving을 return`() {
        // given
        val quizBook = createQuizBook(id = 2993, version = 1)
        val quizBookSolver = createUser(id = 5544, nickName = "test nickName")
        val quizBookSolving = createQuizBookSolving(
            id = 3909,
            quizBook = quizBook,
            user = quizBookSolver
        )
        every { quizBookSolvingRepository.findById(3909) } returns Optional.of(quizBookSolving)

        val quizResponses = listOf(createQuizResponse(id = 2222))
        val savedQuizBook = quizBook.toSavedQuizBook("test nickName", quizResponses)
        val objectMapper = ObjectMapper()
        val vc = Vc(
            quizBookId = 2993,
            version = 1,
            quizzesValue = objectMapper.writeValueAsString(savedQuizBook)
        )
        every { vcRepository.findByQuizBookIdAndVersion(2993, 1) } returns vc

        val quiz = createQuiz(id = 2222, quizBook = quizBook)
        every { quizRepository.findById(2222) } returns Optional.of(quiz)

        val quizSolving = createQuizSolving(
            id = 2030,
            quiz = quiz,
            quizBookSolving = quizBookSolving,
            user = quizBookSolver
        )
        every {
            quizSolvingRepository.findByQuizBookSolvingIdAndQuizId(3909, 2222)
        } returns quizSolving

        // when
        val result = quizBookSolvingService.getQuizBookSolvingById(3909)

        // then
        assertAll(
            { assertEquals(2993, result.quizBookId) },
            { assertEquals(5544, result.userId) },
            { assertEquals(1, result.quizzes.size) },
            { assertEquals(2222, result.quizzes.first().id) },
        )
    }

    @Test
    fun `getQuizBookSolvingById, 해당 id의 quizBookSolving이 존재하지 않을 경우 NotFoundException`() {
        // given
        every { quizBookSolvingRepository.findById(3909) } returns Optional.empty()

        // when
        // then
        val exception = assertThrows(NotFoundException::class.java) {
            quizBookSolvingService.getQuizBookSolvingById(3909)
        }
        assertEquals("해당 ID의 문제 풀이가 존재하지 않습니다: 3909", exception.message)
    }

    @Test
    fun `getQuizBookSolvingById, 퀴즈북 버전 정보를 찾을 수 없을 경우 RuntimeException`() {
        // given
        val quizBook = createQuizBook(id = 2993, version = 1)
        val quizBookSolver = createUser(id = 5544, nickName = "test nickName")
        val quizBookSolving = createQuizBookSolving(
            id = 3909,
            quizBook = quizBook,
            user = quizBookSolver
        )
        every { quizBookSolvingRepository.findById(3909) } returns Optional.of(quizBookSolving)
        every { vcRepository.findByQuizBookIdAndVersion(2993, 1) } returns null

        // when
        // then
        val exception = assertThrows(RuntimeException::class.java) {
            quizBookSolvingService.getQuizBookSolvingById(3909)
        }
        assertEquals("퀴즈북 버전을 찾을 수 없습니다.", exception.message)
    }

    @Test
    fun `getQuizBookSolvingById, 퀴즈북 버전 정보의 quizzesValue에 올바르지 않은 값이 있을 경우 JsonParseException`() {
        // given
        val quizBook = createQuizBook(id = 2993, version = 1)
        val quizBookSolver = createUser(id = 5544, nickName = "test nickName")
        val quizBookSolving = createQuizBookSolving(
            id = 3909,
            quizBook = quizBook,
            user = quizBookSolver
        )
        every { quizBookSolvingRepository.findById(3909) } returns Optional.of(quizBookSolving)

        val vc = Vc(
            quizBookId = 2993,
            version = 1,
            quizzesValue = "invalid quizzesValue"
        )
        every { vcRepository.findByQuizBookIdAndVersion(2993, 1) } returns vc

        // when
        // then
        assertThrows(JsonParseException::class.java) {
            quizBookSolvingService.getQuizBookSolvingById(3909)
        }
    }

    @Test
    fun `getQuizBookSolvingById, 퀴즈북 정보에 있는 퀴즈를 찾을 수 없는 경우 NotFoundException`() {
        // given
        val quizBook = createQuizBook(id = 2993, version = 1)
        val quizBookSolver = createUser(id = 5544, nickName = "test nickName")
        val quizBookSolving = createQuizBookSolving(
            id = 3909,
            quizBook = quizBook,
            user = quizBookSolver
        )
        every { quizBookSolvingRepository.findById(3909) } returns Optional.of(quizBookSolving)

        val quizResponses = listOf(createQuizResponse(id = 2222))
        val savedQuizBook = quizBook.toSavedQuizBook("test nickName", quizResponses)
        val objectMapper = ObjectMapper()
        val vc = Vc(
            quizBookId = 2993,
            version = 1,
            quizzesValue = objectMapper.writeValueAsString(savedQuizBook)
        )
        every { vcRepository.findByQuizBookIdAndVersion(2993, 1) } returns vc
        every { quizRepository.findById(2222) } returns Optional.empty()

        // when
        // then
        val exception = assertThrows(NotFoundException::class.java) {
            quizBookSolvingService.getQuizBookSolvingById(3909)
        }
        assertEquals("해당 ID의 퀴즈가 존재하지 않습니다: 2222", exception.message)
    }

    private fun createCreateQuizBookSolvingRequest(
        quizBookId: Long = 1L,
        version: Long = 1,
        totalQuizzes: Int = 0,
        correctCount: Int = 0,
        completedAt: LocalDateTime = LocalDateTime.now(),
        solvingTime: Long? = 0L,
        quizzes: List<CreateQuizSolvingRequest> = emptyList(),
    ) = CreateQuizBookSolvingRequest(
        quizBookId = quizBookId,
        version = version,
        totalQuizzes = totalQuizzes,
        correctCount = correctCount,
        completedAt = completedAt,
        solvingTime = solvingTime,
        quizzes = quizzes,
    )

    private fun createCreateQuizSolvingRequest(
        quizId: Long = 1L,
        memo: String? = null,
        userAnswer: String? = null,
        isCorrect: Boolean = false,
        completedAt: LocalDateTime = LocalDateTime.now(),
    ) = CreateQuizSolvingRequest(
        quizId = quizId,
        memo = memo,
        userAnswer = userAnswer,
        isCorrect = isCorrect,
        completedAt = completedAt,
    )

    private fun createUpdateQuizBookSolvingRequest(
        correctCount: Int? = null,
        solvingTime: Long? = null,
        completedAt: LocalDateTime? = null,
    ) = UpdateQuizBookSolvingRequest(
        correctCount = correctCount,
        solvingTime = solvingTime,
        completedAt = completedAt,
    )
}