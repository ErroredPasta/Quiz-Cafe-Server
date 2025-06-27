package com.project.quizcafe.domain.quizsolving.service

import com.project.quizcafe.domain.quizsolving.dto.request.CreateSingleMcqOptionSolvingRequest
import com.project.quizcafe.domain.quizsolving.dto.request.UpdateMcqOptionSolvingRequest
import com.project.quizcafe.domain.quizsolving.entity.McqOptionSolving
import com.project.quizcafe.domain.quizsolving.repository.McqOptionSolvingRepository
import com.project.quizcafe.domain.quizsolving.repository.QuizSolvingRepository
import com.project.quizcafe.domain.util.createMcqOptionSolving
import com.project.quizcafe.domain.util.createQuiz
import com.project.quizcafe.domain.util.createQuizBook
import com.project.quizcafe.domain.util.createQuizBookSolving
import com.project.quizcafe.domain.util.createQuizSolving
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
class McqOptionSolvingServiceTest {

    @RelaxedMockK
    private lateinit var mcqOptionSolvingRepository: McqOptionSolvingRepository

    @RelaxedMockK
    private lateinit var quizSolvingRepository: QuizSolvingRepository

    @InjectMockKs
    private lateinit var mcqOptionSolvingService: McqOptionSolvingService

    @Test
    fun `createMcqOptionSolving, 기존에 존재하는 quizSolving을 바탕으로 mcqOptionSolving 생성 후 저장`() {
        // given
        val createRequest = createCreateSingleMcqOptionSolvingRequest(
            quizSolvingId = 5834,
            optionNumber = 4,
            optionContent = "test optionContent",
            isCorrect = true
        )
        val quizBook = createQuizBook()
        val quiz = createQuiz(quizBook = quizBook)
        val quizSolver = createUser()
        val quizBookSolving = createQuizBookSolving(user = quizSolver, quizBook = quizBook)
        val quizSolving = createQuizSolving(
            id = 5834,
            quizBookSolving = quizBookSolving,
            quiz = quiz,
            user = quizSolver,
        )
        every { quizSolvingRepository.findById(5834) } returns Optional.of(quizSolving)

        val mcqOptionSolvingSlot = slot<McqOptionSolving>()
        every {
            mcqOptionSolvingRepository.save(capture(mcqOptionSolvingSlot))
        } answers { mcqOptionSolvingSlot.captured }

        // when
        mcqOptionSolvingService.createMcqOptionSolving(createRequest)

        // then
        val capturedMcqOptionSolving = mcqOptionSolvingSlot.captured
        assertAll(
            { assertEquals(5834, capturedMcqOptionSolving.quizSolving.id) },
            { assertEquals(4, capturedMcqOptionSolving.optionNumber) },
            { assertEquals("test optionContent", capturedMcqOptionSolving.optionContent) },
            { assertEquals(true, capturedMcqOptionSolving.isCorrect) },
        )
    }

    @Test
    fun `createMcqOptionSolving, quizSolving이 없을 경우 RuntimeException`() {
        // given
        val createRequest = createCreateSingleMcqOptionSolvingRequest(
            quizSolvingId = 5834,
            optionNumber = 4,
            optionContent = "test optionContent",
            isCorrect = true
        )
        every { quizSolvingRepository.findById(5834) } returns Optional.empty()


        // when
        // then
        val exception = assertThrows(RuntimeException::class.java) {
            mcqOptionSolvingService.createMcqOptionSolving(createRequest)
        }
        assertEquals("퀴즈 풀이를 찾을 수 없습니다. id=5834", exception.message)
    }

    @Test
    fun `updateMcqOptionSolving, mcqOptionSolving을 업데이트 후 저장`() {
        // given
        val updateRequest = UpdateMcqOptionSolvingRequest(
            optionNumber = 1,
            optionContent = "update optionContent",
            isCorrect = false
        )
        val quizBook = createQuizBook()
        val quiz = createQuiz(quizBook = quizBook)
        val quizSolver = createUser()
        val quizBookSolving = createQuizBookSolving(user = quizSolver, quizBook = quizBook)
        val quizSolving = createQuizSolving(
            id = 5834,
            quizBookSolving = quizBookSolving,
            quiz = quiz,
            user = quizSolver,
        )
        val mcqOptionSolving = createMcqOptionSolving(id = 5342, quizSolving = quizSolving)
        every { mcqOptionSolvingRepository.findById(5342) } returns Optional.of(mcqOptionSolving)

        val mcqOptionSolvingSlot = slot<McqOptionSolving>()
        every {
            mcqOptionSolvingRepository.save(capture(mcqOptionSolvingSlot))
        } answers { mcqOptionSolvingSlot.captured }

        // when
        mcqOptionSolvingService.updateMcqOptionSolving(5342, updateRequest)

        // then
        val capturedMcqOptionSolving = mcqOptionSolvingSlot.captured
        assertAll(
            { assertEquals(5342, capturedMcqOptionSolving.id) },
            { assertEquals(1, capturedMcqOptionSolving.optionNumber) },
            { assertEquals("update optionContent", capturedMcqOptionSolving.optionContent) },
            { assertEquals(false, capturedMcqOptionSolving.isCorrect) },
        )
    }

    @Test
    fun `updateMcqOptionSolving, 존재하지 않는 mcqOptionSolving을 업데이트시 RuntimeException`() {
        // given
        val updateRequest = UpdateMcqOptionSolvingRequest(
            optionNumber = 1,
            optionContent = "update optionContent",
            isCorrect = false
        )
        every { mcqOptionSolvingRepository.findById(5342) } returns Optional.empty()

        // when
        // then
        val exception = assertThrows(RuntimeException::class.java) {
            mcqOptionSolvingService.updateMcqOptionSolving(5342, updateRequest)
        }
        assertEquals("옵션 풀이를 찾을 수 없습니다. id=5342", exception.message)
    }

    @Test
    fun `deleteMcqOptionSolving, 해당 id의 mcqOptionSolving 삭제`() {
        // given
        val quizBook = createQuizBook()
        val quiz = createQuiz(quizBook = quizBook)
        val quizSolver = createUser()
        val quizBookSolving = createQuizBookSolving(user = quizSolver, quizBook = quizBook)
        val quizSolving = createQuizSolving(
            id = 5834,
            quizBookSolving = quizBookSolving,
            quiz = quiz,
            user = quizSolver,
        )
        val mcqOptionSolving = createMcqOptionSolving(id = 5342, quizSolving = quizSolving)
        every { mcqOptionSolvingRepository.findById(5342) } returns Optional.of(mcqOptionSolving)

        val mcqOptionSolvingSlot = slot<McqOptionSolving>()
        every { mcqOptionSolvingRepository.delete(capture(mcqOptionSolvingSlot)) } just Runs

        // when
        mcqOptionSolvingService.deleteMcqOptionSolving(5342)

        // then
        val capturedMcqOptionSolving = mcqOptionSolvingSlot.captured
        assertEquals(5342, capturedMcqOptionSolving.id)
    }

    @Test
    fun `deleteMcqOptionSolving, 존재하지 않는 mcqOptionSolving 삭제시 RuntimeException`() {
        // given
        every { mcqOptionSolvingRepository.findById(5342) } returns Optional.empty()

        // when
        // then
        val exception = assertThrows(RuntimeException::class.java) {
            mcqOptionSolvingService.deleteMcqOptionSolving(5342)
        }
        assertEquals("옵션 풀이를 찾을 수 없습니다. id=5342", exception.message)
    }

    @Test
    fun `getMcqOptionsByQuizSolvingId, quizSolvingId의 모든 mcqOptionSolving들을 return`() {
        // given
        val quizSolver = createUser()
        val quizBook = createQuizBook()
        val quiz = createQuiz(quizBook = quizBook)
        val quizBookSolving = createQuizBookSolving(user = quizSolver, quizBook = quizBook)
        val quizSolving = createQuizSolving(
            id = 8843,
            quizBookSolving = quizBookSolving,
            quiz = quiz,
            user = quizSolver,
        )
        val mcqOptions = (1L..5L).map {
            createMcqOptionSolving(id = it, quizSolving = quizSolving)
        }
        every { mcqOptionSolvingRepository.findByQuizSolvingId(8843) } returns mcqOptions

        // when
        val result = mcqOptionSolvingService.getMcqOptionsByQuizSolvingId(8843)

        // then
        assertAll(
            { assertEquals(5, result.size) },
            { assertEquals(true, result.all { it.quizSolvingId == 8843L }) },
        )
    }

    @Test
    fun `getMcqOptionsByQuizSolvingId, quizSolvingId의 mcqOptionSolving이 없을 경우 빈 리스트 return`() {
        // given
        every { mcqOptionSolvingRepository.findByQuizSolvingId(8843) } returns emptyList()

        // when
        val result = mcqOptionSolvingService.getMcqOptionsByQuizSolvingId(8843)

        // then
        assertEquals(true, result.isEmpty())
    }

    private fun createCreateSingleMcqOptionSolvingRequest(
        quizSolvingId: Long = 1L,
        optionNumber: Int = 1,
        optionContent: String = "default optionContent",
        isCorrect: Boolean = false,
    ) = CreateSingleMcqOptionSolvingRequest(
        quizSolvingId = quizSolvingId,
        optionNumber = optionNumber,
        optionContent = optionContent,
        isCorrect = isCorrect,
    )
}
