package com.project.quizcafe.domain.quizsolving.service

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.ObjectMapper
import com.project.quizcafe.common.exception.ForbiddenException
import com.project.quizcafe.common.exception.NotFoundException
import com.project.quizcafe.domain.quiz.entity.QuestionType
import com.project.quizcafe.domain.quiz.repository.QuizRepository
import com.project.quizcafe.domain.quiz.validator.QuizValidator
import com.project.quizcafe.domain.quizbooksolving.repository.QuizBookSolvingRepository
import com.project.quizcafe.domain.quizsolving.dto.request.UpdateQuizSolvingRequest
import com.project.quizcafe.domain.quizsolving.dto.response.QuizSolvingResponse
import com.project.quizcafe.domain.quizsolving.entity.QuizSolving
import com.project.quizcafe.domain.quizsolving.repository.McqOptionSolvingRepository
import com.project.quizcafe.domain.quizsolving.repository.QuizSolvingRepository
import com.project.quizcafe.domain.quizsolving.validator.QuizSolvingValidator
import com.project.quizcafe.domain.user.repository.UserRepository
import com.project.quizcafe.domain.util.createQuiz
import com.project.quizcafe.domain.util.createQuizBook
import com.project.quizcafe.domain.util.createQuizBookSolving
import com.project.quizcafe.domain.util.createQuizResponse
import com.project.quizcafe.domain.util.createQuizSolving
import com.project.quizcafe.domain.util.createUser
import com.project.quizcafe.domain.util.toSavedQuizBook
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
import java.time.LocalDateTime
import java.util.Optional
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
class QuizSolvingServiceTest {
    @RelaxedMockK
    private lateinit var quizSolvingRepository: QuizSolvingRepository

    @RelaxedMockK
    private lateinit var quizRepository: QuizRepository

    @RelaxedMockK
    private lateinit var mcqOptionSolvingService: McqOptionSolvingService

    private lateinit var quizSolvingValidator: QuizSolvingValidator

    @RelaxedMockK
    private lateinit var quizValidator: QuizValidator

    @RelaxedMockK
    private lateinit var vcRepository: VcRepository

    @RelaxedMockK
    private lateinit var quizBookSolvingRepository: QuizBookSolvingRepository

    @RelaxedMockK
    private lateinit var userRepository: UserRepository

    @RelaxedMockK
    private lateinit var mcqOptionSolvingRepository: McqOptionSolvingRepository

    private lateinit var objectMapper: ObjectMapper

    private lateinit var quizSolvingService: QuizSolvingService

    @BeforeEach
    fun setUp() {
        objectMapper = ObjectMapper()
        quizSolvingValidator = QuizSolvingValidator()
        quizSolvingService = QuizSolvingService(
            quizSolvingRepository = quizSolvingRepository,
            quizRepository = quizRepository,
            mcqOptionSolvingService = mcqOptionSolvingService,
            quizSolvingValidator = quizSolvingValidator,
            quizValidator = quizValidator,
            vcRepository = vcRepository,
            quizBookSolvingRepository = quizBookSolvingRepository,
            userRepository = userRepository,
            mcqOptionSolvingRepository = mcqOptionSolvingRepository
        )
    }

    @Test
    fun `getQuizSolving, 현재 유저의 quizSolving을 return`() {
        // given
        val currentUser = createUser(nickName = "test nickName")
        val quizBook = createQuizBook(id = 3382, version = 2)
        val quizBookSolving = createQuizBookSolving(
            id = 2333,
            user = currentUser,
            quizBook = quizBook,
            version = 2
        )
        val quiz = createQuiz(
            id = 9023,
            quizBook = quizBook,
            questionType = QuestionType.SHORT_ANSWER
        )
        val quizSolving = createQuizSolving(
            id = 3244,
            quizBookSolving = quizBookSolving,
            quiz = quiz,
            user = currentUser,
            completedAt = LocalDateTime.of(2025, 1, 1, 1, 1, 0)
        )
        val quizResponse = createQuizResponse(id = 9023, questionType = QuestionType.SHORT_ANSWER)
        val savedQuizBook = quizBook.toSavedQuizBook(creatorNickName = "test nickName", quizzes = listOf(quizResponse))
        val vc = Vc(
            quizBookId = 3382,
            version = 2,
            quizzesValue = objectMapper.writeValueAsString(savedQuizBook)
        )
        every { quizSolvingRepository.findById(3244) } returns Optional.of(quizSolving)
        every { quizRepository.findById(9023) } returns Optional.of(quiz)
        every { vcRepository.findByQuizBookIdAndVersion(3382, 2) } returns vc

        // when
        val result = quizSolvingService.getQuizSolving(3244, currentUser)

        // then
        val expectedQuizSolvingResponse = QuizSolvingResponse(
            id = 9023,
            quizBookSolvingId = 2333,
            quizId = 9023,
            questionType = QuestionType.SHORT_ANSWER,
            content = "default content",
            answer = "default answer",
            explanation = null,
            memo = null,
            userAnswer = null,
            isCorrect = false,
            completedAt = LocalDateTime.of(2025, 1, 1, 1, 1, 0),
            mcqOptions = emptyList()
        )
        assertEquals(expectedQuizSolvingResponse, result)
    }

    @Test
    fun `getQuizSolving, 해당 id의 quizSolving이 없을 경우 NotFoundException`() {
        // given
        val currentUser = createUser(nickName = "test nickName")
        val quizBook = createQuizBook(id = 3382, version = 2)
        val quizBookSolving = createQuizBookSolving(
            id = 2333,
            user = currentUser,
            quizBook = quizBook,
            version = 2
        )
        val quiz = createQuiz(
            id = 9023,
            quizBook = quizBook,
            questionType = QuestionType.SHORT_ANSWER
        )
        val quizSolving = createQuizSolving(
            id = 3244,
            quizBookSolving = quizBookSolving,
            quiz = quiz,
            user = currentUser,
            completedAt = LocalDateTime.of(2025, 1, 1, 1, 1, 0)
        )
        every { quizSolvingRepository.findById(3244) } returns Optional.of(quizSolving)
        every { quizRepository.findById(9023) } returns Optional.empty()

        // when
        // then
        val exception = assertThrows(NotFoundException::class.java) {
            quizSolvingService.getQuizSolving(3244, currentUser)
        }
        assertEquals("해당 ID의 퀴즈가 존재하지 않습니다: 9023", exception.message)
    }

    @Test
    fun `getQuizSolving, quizSolving의 quiz id의 퀴즈가 존재하지 않을 경우`() {
        // given
        val currentUser = createUser(nickName = "test nickName")
        every { quizSolvingRepository.findById(3244) } returns Optional.empty()

        // when
        // then
        val exception = assertThrows(NotFoundException::class.java) {
            quizSolvingService.getQuizSolving(3244, currentUser)
        }
        assertEquals("해당 ID의 문제 풀이가 존재하지 않습니다: 3244", exception.message)
    }

    @Test
    fun `getQuizSolving, 자신의 quizSolving이 아닐 경우 ForbiddenException`() {
        // given
        val currentUser = createUser(id = 3112, nickName = "test nickName")
        val quizBook = createQuizBook(id = 3382, version = 2)
        val quizSolver = createUser(id = 6554, nickName = "test quizSolver")
        val quizBookSolving = createQuizBookSolving(
            id = 2333,
            user = quizSolver,
            quizBook = quizBook,
            version = 2
        )
        val quiz = createQuiz(
            id = 9023,
            quizBook = quizBook,
            questionType = QuestionType.SHORT_ANSWER
        )
        val quizSolving = createQuizSolving(
            id = 3244,
            quizBookSolving = quizBookSolving,
            quiz = quiz,
            user = quizSolver,
            completedAt = LocalDateTime.of(2025, 1, 1, 1, 1, 0)
        )
        every { quizSolvingRepository.findById(3244) } returns Optional.of(quizSolving)
        every { quizRepository.findById(9023) } returns Optional.of(quiz)

        // when
        // then
        val exception = assertThrows(ForbiddenException::class.java) {
            quizSolvingService.getQuizSolving(3244, currentUser)
        }
        assertEquals("조회 권한이 없습니다.", exception.message)
    }

    @Test
    fun `getQuizSolving, quizBookId와 version이 일치하는 vc가 존재하지 않을 경우 RuntimeException`() {
        // given
        val currentUser = createUser(nickName = "test nickName")
        val quizBook = createQuizBook(id = 3382, version = 2)
        val quizBookSolving = createQuizBookSolving(
            id = 2333,
            user = currentUser,
            quizBook = quizBook,
            version = 2
        )
        val quiz = createQuiz(
            id = 9023,
            quizBook = quizBook,
            questionType = QuestionType.SHORT_ANSWER
        )
        val quizSolving = createQuizSolving(
            id = 3244,
            quizBookSolving = quizBookSolving,
            quiz = quiz,
            user = currentUser,
            completedAt = LocalDateTime.of(2025, 1, 1, 1, 1, 0)
        )

        every { quizSolvingRepository.findById(3244) } returns Optional.of(quizSolving)
        every { quizRepository.findById(9023) } returns Optional.of(quiz)
        every { vcRepository.findByQuizBookIdAndVersion(3382, 2) } returns null

        // when
        // then
        val exception = assertThrows(RuntimeException::class.java) {
            quizSolvingService.getQuizSolving(3244, currentUser)
        }
        assertEquals("퀴즈북 버전을 찾을 수 없습니다.", exception.message)
    }

    @Test
    fun `getQuizSolving, vc의 quizzesValue에 퀴즈 id와 일치하는 퀴즈가 없을 경우 RuntimeException`() {
        // given
        val currentUser = createUser(nickName = "test nickName")
        val quizBook = createQuizBook(id = 3382, version = 2)
        val quizBookSolving = createQuizBookSolving(
            id = 2333,
            user = currentUser,
            quizBook = quizBook,
            version = 2
        )
        val quiz = createQuiz(
            id = 9023,
            quizBook = quizBook,
            questionType = QuestionType.SHORT_ANSWER
        )
        val quizSolving = createQuizSolving(
            id = 3244,
            quizBookSolving = quizBookSolving,
            quiz = quiz,
            user = currentUser,
            completedAt = LocalDateTime.of(2025, 1, 1, 1, 1, 0)
        )
        val vc = Vc(
            quizBookId = 3382,
            version = 2,
            quizzesValue = "invalid quizzesValue"
        )
        every { quizSolvingRepository.findById(3244) } returns Optional.of(quizSolving)
        every { quizRepository.findById(9023) } returns Optional.of(quiz)
        every { vcRepository.findByQuizBookIdAndVersion(3382, 2) } returns vc

        // when
        // then
        assertThrows(JsonParseException::class.java) { quizSolvingService.getQuizSolving(3244, currentUser) }
    }

    @Test
    fun `getQuizSolving, vc에 올바르지 않은 quizzesValue가 있을 경우 JsonParseException`() {
        // given
        val currentUser = createUser(nickName = "test nickName")
        val quizBook = createQuizBook(id = 3382, version = 2)
        val quizBookSolving = createQuizBookSolving(
            id = 2333,
            user = currentUser,
            quizBook = quizBook,
            version = 2
        )
        val quiz = createQuiz(
            id = 9023,
            quizBook = quizBook,
            questionType = QuestionType.SHORT_ANSWER
        )
        val quizSolving = createQuizSolving(
            id = 3244,
            quizBookSolving = quizBookSolving,
            quiz = quiz,
            user = currentUser,
            completedAt = LocalDateTime.of(2025, 1, 1, 1, 1, 0)
        )
        val quizResponse = createQuizResponse(id = 5254, questionType = QuestionType.SHORT_ANSWER)
        val savedQuizBook = quizBook.toSavedQuizBook(creatorNickName = "test nickName", quizzes = listOf(quizResponse))
        val vc = Vc(
            quizBookId = 3382,
            version = 2,
            quizzesValue = objectMapper.writeValueAsString(savedQuizBook)
        )
        every { quizSolvingRepository.findById(3244) } returns Optional.of(quizSolving)
        every { quizRepository.findById(9023) } returns Optional.of(quiz)
        every { vcRepository.findByQuizBookIdAndVersion(3382, 2) } returns vc

        // when
        // then
        val exception = assertThrows(RuntimeException::class.java) {
            quizSolvingService.getQuizSolving(3244, currentUser)
        }
        assertEquals("저장된 퀴즈(savedQuiz)를 찾을 수 없습니다. quizId=9023", exception.message)
    }

    @Test
    fun `updateQuizSolving, 존재하지 않는 quizSolving 수정시 NotFoundException`() {
        // given
        val updateRequest = UpdateQuizSolvingRequest(
            userAnswer = "test update userAnswer",
            isCorrect = true,
            memo = "test update memo",
        )
        val currentUser = createUser(id = 3112, nickName = "test nickName")
        every { quizSolvingRepository.findById(3244) } returns Optional.empty()

        // when
        // then
        val exception = assertThrows(NotFoundException::class.java) {
            quizSolvingService.updateQuizSolving(3244, updateRequest, currentUser)
        }
        assertEquals("해당 ID의 문제 풀이가 존재하지 않습니다: 3244", exception.message)
    }

    @Test
    fun `updateQuizSolving, 자신이 생성하지 않은 quizSolving 수정시 ForbiddenException`() {
        // given
        val updateRequest = UpdateQuizSolvingRequest(
            userAnswer = "test update userAnswer",
            isCorrect = true,
            memo = "test update memo",
        )
        val currentUser = createUser(id = 3112, nickName = "test nickName")
        val quizBook = createQuizBook(id = 3382, version = 2)
        val quizSolver = createUser(id = 6554, nickName = "test quizSolver")
        val quizBookSolving = createQuizBookSolving(
            id = 2333,
            user = quizSolver,
            quizBook = quizBook,
            version = 2
        )
        val quiz = createQuiz(
            id = 9023,
            quizBook = quizBook,
            questionType = QuestionType.SHORT_ANSWER
        )
        val quizSolving = createQuizSolving(
            id = 3244,
            quizBookSolving = quizBookSolving,
            quiz = quiz,
            user = quizSolver,
            completedAt = LocalDateTime.of(2025, 1, 1, 1, 1, 0)
        )
        every { quizSolvingRepository.findById(3244) } returns Optional.of(quizSolving)

        // when
        // then
        val exception = assertThrows(ForbiddenException::class.java) {
            quizSolvingService.updateQuizSolving(3244, updateRequest, currentUser)
        }
        assertEquals("조회 권한이 없습니다.", exception.message)
    }

    @Test
    fun `deleteQuizSolving, 해당 id의 quizSolving 삭제`() {
        // given
        val currentUser = createUser(nickName = "test nickName")
        val quizBook = createQuizBook(id = 3382, version = 2)
        val quizBookSolving = createQuizBookSolving(
            id = 2333,
            user = currentUser,
            quizBook = quizBook,
            version = 2
        )
        val quiz = createQuiz(
            id = 9023,
            quizBook = quizBook,
            questionType = QuestionType.SHORT_ANSWER
        )
        val quizSolving = createQuizSolving(
            id = 3244,
            quizBookSolving = quizBookSolving,
            quiz = quiz,
            user = currentUser,
            completedAt = LocalDateTime.of(2025, 1, 1, 1, 1, 0)
        )
        every { quizSolvingRepository.findById(3244) } returns Optional.of(quizSolving)

        val quizSolvingSlot = slot<QuizSolving>()
        every { quizSolvingRepository.delete(capture(quizSolvingSlot)) } answers { callOriginal() }

        // when
        quizSolvingService.deleteQuizSolving(3244, currentUser)

        // then
        val capturedQuizSolving = quizSolvingSlot.captured
        assertEquals(3244, capturedQuizSolving.id)
    }

    @Test
    fun `deleteQuizSolving, 존재하지 않는 quizSolving 삭제시 NotFoundException`() {
        // given
        val currentUser = createUser(nickName = "test nickName")
        every { quizSolvingRepository.findById(3244) } returns Optional.empty()

        val quizSolvingSlot = slot<QuizSolving>()
        every { quizSolvingRepository.delete(capture(quizSolvingSlot)) } answers { callOriginal() }

        // when
        // then
        val exception = assertThrows(NotFoundException::class.java) {
            quizSolvingService.deleteQuizSolving(3244, currentUser)
        }
        assertEquals("해당 ID의 문제 풀이가 존재하지 않습니다: 3244", exception.message)
    }

    @Test
    fun `deleteQuizSolving, 자신이 생성하지 않은 quizSolving 삭제시 NotFoundException`() {
        // given
        val currentUser = createUser(id = 3112, nickName = "test nickName")
        val quizBook = createQuizBook(id = 3382, version = 2)
        val quizSolver = createUser(id = 6554, nickName = "test quizSolver")
        val quizBookSolving = createQuizBookSolving(
            id = 2333,
            user = quizSolver,
            quizBook = quizBook,
            version = 2
        )
        val quiz = createQuiz(
            id = 9023,
            quizBook = quizBook,
            questionType = QuestionType.SHORT_ANSWER
        )
        val quizSolving = createQuizSolving(
            id = 3244,
            quizBookSolving = quizBookSolving,
            quiz = quiz,
            user = quizSolver,
            completedAt = LocalDateTime.of(2025, 1, 1, 1, 1, 0)
        )
        every { quizSolvingRepository.findById(3244) } returns Optional.of(quizSolving)

        // when
        // then
        val exception = assertThrows(ForbiddenException::class.java) {
            quizSolvingService.deleteQuizSolving(3244, currentUser)
        }
        assertEquals("조회 권한이 없습니다.", exception.message)
    }
}
