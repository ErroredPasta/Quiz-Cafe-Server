package com.project.quizcafe.domain.quiz.service

import com.project.quizcafe.common.exception.ForbiddenException
import com.project.quizcafe.common.exception.NotFoundException
import com.project.quizcafe.domain.quiz.dto.request.CreateQuizRequest
import com.project.quizcafe.domain.quiz.dto.request.UpdateQuizRequest
import com.project.quizcafe.domain.quiz.entity.QuestionType
import com.project.quizcafe.domain.quiz.entity.Quiz
import com.project.quizcafe.domain.quiz.extensions.toMcqOptionResponse
import com.project.quizcafe.domain.quiz.extensions.toQuiz
import com.project.quizcafe.domain.quiz.repository.QuizRepository
import com.project.quizcafe.domain.util.createMcqOption
import com.project.quizcafe.domain.util.createQuiz
import com.project.quizcafe.domain.util.createQuizBook
import com.project.quizcafe.domain.util.createUser
import com.project.quizcafe.domain.quiz.validator.QuizValidator
import com.project.quizcafe.domain.quizbook.repository.QuizBookRepository
import com.project.quizcafe.domain.versioncontrol.service.VcService
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
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

@ExtendWith(MockitoExtension::class)
class QuizServiceTest {
    @Mock
    private lateinit var mcqOptionService: McqOptionService

    @Mock
    private lateinit var vcService: VcService

    @Mock
    private lateinit var quizRepository: QuizRepository

    @Mock
    private lateinit var quizBookRepository: QuizBookRepository
    private lateinit var quizValidator: QuizValidator

    private lateinit var quizService: QuizService

    @BeforeEach
    fun setUp() {
        quizValidator = QuizValidator()
        quizService = QuizService(quizRepository, mcqOptionService, vcService, quizBookRepository, quizValidator)
    }

    @Test
    fun `createQuiz, 올바른 request가 넘어올 경우 quiz를 생성 후 저장`() {
        // given
        val createRequest = createCreateQuizRequest(
            quizBookId = 1168,
            content = "test content",
            answer = "test answer",
            questionType = QuestionType.MCQ
        )
        val quizBook = createQuizBook(id = 1168)
        `when`(quizBookRepository.findById(1168)).thenReturn(Optional.of(quizBook))
        `when`(quizRepository.save(any())).thenReturn(createRequest.toQuiz(quizBook))

        // when
        quizService.createQuiz(createRequest)

        // then
        val captor = ArgumentCaptor.forClass(Quiz::class.java)
        verify(quizRepository, times(1)).save(captor.capture())

        val capturedQuizBook = captor.value
        assertAll(
            { assertEquals(capturedQuizBook.quizBook.id, 1168) },
            { assertEquals(capturedQuizBook.quizBook, quizBook) },
            { assertEquals(capturedQuizBook.content, "test content") },
            { assertEquals(capturedQuizBook.answer, "test answer") },
            { assertEquals(capturedQuizBook.explanation, "test explanation") },
            { assertEquals(capturedQuizBook.questionType, QuestionType.MCQ) }
        )
    }

    @Test
    fun `createQuiz, 존재하지 않는 quizBookId가 넘어올 경우 NotFoundException`() {
        // given
        val createRequest = createCreateQuizRequest(quizBookId = 1168)
        `when`(quizBookRepository.findById(1168)).thenReturn(Optional.empty())

        // when
        // then
        val exception = assertThrows(NotFoundException::class.java) {
            quizService.createQuiz(createRequest)
        }

        assertEquals(exception.message, "해당 ID의 퀴즈북이 존재하지 않습니다: 1168")
    }

    @Test
    fun `createQuiz, 퀴즈 저장시 퀴즈북의 버전 업데이트`() {
        // given
        val createRequest = createCreateQuizRequest(quizBookId = 1168)
        val quizBook = createQuizBook(id = 1168, version = 1)
        `when`(quizBookRepository.findById(1168)).thenReturn(Optional.of(quizBook))
        `when`(quizRepository.save(any())).thenReturn(createRequest.toQuiz(quizBook))

        // when
        quizService.createQuiz(createRequest)

        // then
        verify(vcService, times(1)).save(eq(1168L), eq(2L))
    }

    @Test
    fun `getQuizzesByQuizBookId, 퀴즈북의 퀴즈를 return`() {
        // given
        val quizBook = createQuizBook(id = 3484)
        val quizzes = listOf(
            createQuiz(id = 1L, questionType = QuestionType.SHORT_ANSWER, quizBook = quizBook),
            createQuiz(id = 2L, questionType = QuestionType.OX, quizBook = quizBook),
            createQuiz(id = 3L, questionType = QuestionType.MCQ, quizBook = quizBook),
        )
        val mcqOptions = (1L..5L).map {
            createMcqOption(
                id = it,
                quiz = quizzes.last(),
                optionNumber = it.toInt(),
                isCorrect = it == 5L
            ).toMcqOptionResponse()
        }
        `when`(quizRepository.findAllByQuizBookId(3484)).thenReturn(quizzes)
        `when`(mcqOptionService.getMcqOptionsByQuizId(anyLong())).thenReturn(mcqOptions)

        // when
        val result = quizService.getQuizzesByQuizBookId(3484)

        // then
        val mcqQuiz = result.last()
        assertAll(
            { assertEquals(result.size, 3) },
            { assertEquals(result.first().mcqOption, null) },
            { assertEquals(result.first().quizBookId, 3484) },
            { assertEquals(mcqQuiz.quizBookId, 3484) },
            { assertEquals(mcqQuiz.mcqOption?.size, 5) },
            { assertEquals(mcqQuiz.mcqOption?.first()?.isCorrect, false) },
            { assertEquals(mcqQuiz.mcqOption?.first()?.optionNumber, 1) },
            { assertEquals(mcqQuiz.mcqOption?.last()?.isCorrect, true) },
            { assertEquals(mcqQuiz.mcqOption?.last()?.optionNumber, 5) },
        )
    }

    @Test
    fun `getQuizzesByQuizBookId, 퀴즈북이 존재하지 않을 경우 빈 리스트 return`() {
        // given
        `when`(quizRepository.findAllByQuizBookId(3484)).thenReturn(emptyList())

        // when
        val result = quizService.getQuizzesByQuizBookId(3484)

        // then
        assertEquals(result.size, 0)
    }

    @Test
    fun `getQuizzesByQuizId, mcq 퀴즈가 아닐 경우 보기가 없음`() {
        // given
        val quizBook = createQuizBook(id = 2232)
        val quiz = createQuiz(id = 3929, questionType = QuestionType.SHORT_ANSWER, quizBook = quizBook)
        `when`(quizRepository.findById(3929)).thenReturn(Optional.of(quiz))

        // when
        val result = quizService.getQuizzesByQuizId(3929)

        // then
        assertAll(
            { assertEquals(result.id, 3929) },
            { assertEquals(result.quizBookId, 2232) },
            { assertEquals(result.mcqOption, null) },
        )
    }

    @Test
    fun `getQuizzesByQuizId, mcq 퀴즈일 경우 보기까지 포함`() {
        // given
        val quizBook = createQuizBook(id = 2232)
        val quiz = createQuiz(id = 3929, questionType = QuestionType.MCQ, quizBook = quizBook)
        val mcqOptions = (1L..5L).map {
            createMcqOption(
                id = it,
                quiz = quiz,
                optionNumber = it.toInt(),
                isCorrect = it == 5L
            ).toMcqOptionResponse()
        }
        `when`(quizRepository.findById(3929)).thenReturn(Optional.of(quiz))
        `when`(mcqOptionService.getMcqOptionsByQuizId(3929)).thenReturn(mcqOptions)

        // when
        val result = quizService.getQuizzesByQuizId(3929)

        // then
        assertAll(
            { assertEquals(result.id, 3929) },
            { assertEquals(result.quizBookId, 2232) },
            { assertEquals(result.mcqOption?.size, 5) },
            { assertEquals(result.mcqOption?.first()?.isCorrect, false) },
            { assertEquals(result.mcqOption?.first()?.optionNumber, 1) },
            { assertEquals(result.mcqOption?.last()?.isCorrect, true) },
            { assertEquals(result.mcqOption?.last()?.optionNumber, 5) },
        )
    }

    @Test
    fun `getQuizzesByQuizId, 없는 퀴즈를 가져올 경우 NotFoundException`() {
        // given
        `when`(quizRepository.findById(3929)).thenReturn(Optional.empty())

        // when
        // then
        val exception = assertThrows(NotFoundException::class.java) {
            quizService.getQuizzesByQuizId(3929)
        }
        assertEquals(exception.message, "해당 ID의 퀴즈가 존재하지 않습니다: 3929")
    }

    @Test
    fun `updateQuiz, 퀴즈 업데이트 시 버전 업데이트`() {
        // given
        val updateRequest = createUpdateQuizRequest(
            content = "update content",
            answer = "update answer",
            explanation = "update explanation"
        )
        val currentUser = createUser()
        val quizBook = createQuizBook(id = 5371, version = 2, createdBy = currentUser)
        val quiz = createQuiz(id = 1423, quizBook = quizBook)
        `when`(quizRepository.findById(1423)).thenReturn(Optional.of(quiz))
        `when`(quizBookRepository.findById(anyLong())).thenReturn(Optional.of(quizBook))

        // when
        quizService.updateQuiz(1423, updateRequest, currentUser)

        // then
        verify(vcService, times(1)).save(eq(5371L), eq(3L))
    }

    @Test
    fun `updateQuiz, 퀴즈 생성자가 없는 퀴즈 업데이트 시 버전 업데이트`() {
        // given
        val updateRequest = createUpdateQuizRequest()
        val currentUser = createUser(id = 6043)
        val quizBook = createQuizBook(id = 5371, createdBy = null, version = 2)
        val quiz = createQuiz(id = 1423, quizBook = quizBook)
        `when`(quizRepository.findById(1423)).thenReturn(Optional.of(quiz))
        `when`(quizBookRepository.findById(anyLong())).thenReturn(Optional.of(quizBook))

        // when
        quizService.updateQuiz(1423, updateRequest, currentUser)

        // then
        verify(vcService, times(1)).save(eq(5371L), eq(3L))
    }


    @Test
    fun `updateQuiz, 자신이 생성하지 않은 퀴즈 수정시 ForbiddenException`() {
        // given
        val updateRequest = createUpdateQuizRequest()
        val currentUser = createUser(id = 5085)
        val quizBookCreator = createUser(id = 4023)
        val quizBook = createQuizBook(createdBy = quizBookCreator)
        val quiz = createQuiz(id = 1423, quizBook = quizBook)
        `when`(quizRepository.findById(1423)).thenReturn(Optional.of(quiz))

        // when
        // then
        val exception = assertThrows(ForbiddenException::class.java) {
            quizService.updateQuiz(1423, updateRequest, currentUser)
        }
        assertEquals(exception.message, "문제를 수정할 권한이 없습니다.")
    }

    @Test
    fun `deleteQuiz, 퀴즈 삭제시 올바르게 삭제`() {
        // given
        val currentUser = createUser()
        val quizBook = createQuizBook(createdBy = currentUser)
        val quiz = createQuiz(id = 3156, quizBook = quizBook)
        `when`(quizRepository.findById(3156)).thenReturn(Optional.of(quiz))

        // when
        quizService.deleteQuiz(3156, currentUser)

        // then
        val captor = ArgumentCaptor.forClass(Quiz::class.java)
        verify(quizRepository).delete(captor.capture())
        assertEquals(captor.value.id, 3156)
    }

    @Test
    fun `deleteQuiz, 퀴즈 생성자가 없는 퀴즈 삭제시 성공적으로 삭제`() {
        // given
        val currentUser = createUser()
        val quizBook = createQuizBook(createdBy = null)
        val quiz = createQuiz(id = 3156, quizBook = quizBook)
        `when`(quizRepository.findById(3156)).thenReturn(Optional.of(quiz))

        // when
        quizService.deleteQuiz(3156, currentUser)

        // then
        val captor = ArgumentCaptor.forClass(Quiz::class.java)
        verify(quizRepository).delete(captor.capture())
        assertEquals(captor.value.id, 3156)
    }

    @Test
    fun `deleteQuiz, 자신이 생성하지 않은 퀴즈 삭제시 ForbiddenException`() {
        // given
        val currentUser = createUser(id = 4039)
        val quizCreator = createUser(id = 4993)
        val quizBook = createQuizBook(createdBy = quizCreator)
        val quiz = createQuiz(id = 3156, quizBook = quizBook)
        `when`(quizRepository.findById(3156)).thenReturn(Optional.of(quiz))

        // when
        // then
        val exception = assertThrows(ForbiddenException::class.java) {
            quizService.deleteQuiz(3156, currentUser)
        }
        assertEquals(exception.message, "문제를 수정할 권한이 없습니다.")
    }

    @Test
    fun `deleteQuiz, 존재하지 않는 퀴즈 삭제시 NotFoundException`() {
        // given
        val currentUser = createUser()

        `when`(quizRepository.findById(3156)).thenReturn(Optional.empty())

        // when
        // then
        val exception = assertThrows(NotFoundException::class.java) {
            quizService.deleteQuiz(3156, currentUser)
        }
        assertEquals(exception.message, "해당 ID의 퀴즈가 존재하지 않습니다: 3156")
    }

    private fun createCreateQuizRequest(
        quizBookId: Long = 1L,
        questionType: QuestionType = QuestionType.MCQ,
        content: String = "test content",
        answer: String = "test answer",
        explanation: String = "test explanation"
    ) = CreateQuizRequest(
        quizBookId = quizBookId,
        questionType = questionType,
        content = content,
        answer = answer,
        explanation = explanation
    )

    private fun createUpdateQuizRequest(
        content: String = "update content",
        answer: String = "update answer",
        explanation: String = "update explanation"
    ) = UpdateQuizRequest(
        content = content,
        answer = answer,
        explanation = explanation
    )
}