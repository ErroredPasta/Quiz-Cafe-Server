package com.project.quizcafe.domain.auth.service

import com.project.quizcafe.common.exception.BadRequestException
import com.project.quizcafe.common.exception.InternalServerErrorException
import com.project.quizcafe.common.exception.NotFoundException
import com.project.quizcafe.domain.auth.entity.EmailVerification
import com.project.quizcafe.domain.auth.repository.EmailVerificationRepository
import com.project.quizcafe.domain.auth.validator.EmailVerificationValidator
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockkObject
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDateTime
import java.util.Optional
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
class EmailVerificationServiceTest {
    @RelaxedMockK
    private lateinit var emailVerificationRepository: EmailVerificationRepository

    private lateinit var emailVerificationValidator: EmailVerificationValidator

    private lateinit var emailVerificationService: EmailVerificationService

    @BeforeEach
    fun setUp() {
        mockkObject(Random.Default)
        emailVerificationValidator = EmailVerificationValidator()
        emailVerificationService = EmailVerificationService(
            emailVerificationRepository = emailVerificationRepository,
            emailVerificationValidator = emailVerificationValidator
        )
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `generateAndSaveCode, 생성된 인증 코드를 return`() {
        // given
        every { Random.nextInt(100000, 1000000) } returns 123456

        val emailVerificationSlot = slot<EmailVerification>()
        every {
            emailVerificationRepository.save(capture(emailVerificationSlot))
        } answers { emailVerificationSlot.captured }

        // when
        val result = emailVerificationService.generateAndSaveCode("test@test.com")

        // then
        assertEquals("123456", result)
    }

    @Test
    fun `generateAndSaveCode, 생성된 인증 코드 정보를 저장`() {
        // given
        every { Random.nextInt(100000, 1000000) } returns 123456

        val emailVerificationSlot = slot<EmailVerification>()
        every {
            emailVerificationRepository.save(capture(emailVerificationSlot))
        } answers { emailVerificationSlot.captured }

        // when
        emailVerificationService.generateAndSaveCode("test@test.com")

        // then
        val capturedEmailVerification = emailVerificationSlot.captured
        assertAll(
            { assertEquals("test@test.com", capturedEmailVerification.email) },
            { assertEquals("123456", capturedEmailVerification.verificationCode) },
        )
    }

    @Test
    fun `generateAndSaveCode, 기존에 생성된 인증 코드 정보 삭제`() {
        // given
        every { Random.nextInt(100000, 1000000) } returns 123456

        val emailVerificationSlot = slot<EmailVerification>()
        every {
            emailVerificationRepository.save(capture(emailVerificationSlot))
        } answers { emailVerificationSlot.captured }

        // when
        emailVerificationService.generateAndSaveCode("test@test.com")

        // then
        verify(exactly = 1) {
            emailVerificationRepository.deleteByEmail("test@test.com")
        }
    }

    @Test
    fun `generateAndSaveCode, 인증 정보 저장 중 예외 발생시 InternalServerErrorException`() {
        // given
        every { emailVerificationRepository.save(any()) } throws Exception()

        // when
        // then
        val exception = assertThrows(InternalServerErrorException::class.java) {
            emailVerificationService.generateAndSaveCode("test@test.com")
        }
        assertEquals("인증 코드 저장 중 오류가 발생했습니다.", exception.message)
    }

    @Test
    fun `verifyCode, 이메일 정보로 저장된 코드와 입력받은 코드가 일치할 경우 true return`() {
        // given
        val emailVerification = EmailVerification(email = "test@test.com", verificationCode = "123456")
        every { emailVerificationRepository.findByEmail("test@test.com") } returns Optional.of(emailVerification)

        // when
        val result = emailVerificationService.verifyCode("test@test.com", "123456")

        // then
        assertEquals(true, result)
    }

    @Test
    fun `verifyCode, 이메일 정보로 저장된 인증 코드 정보가 없을 경우 NotFoundException`() {
        // given
        every { emailVerificationRepository.findByEmail("test@test.com") } returns Optional.empty()

        // when
        // then
        val exception = assertThrows(NotFoundException::class.java) {
            emailVerificationService.verifyCode("test@test.com", "123456")
        }
        assertEquals("등록된 인증 코드가 없습니다.", exception.message)
    }

    @Test
    fun `verifyCode, 이메일 정보로 저장된 코드와 입력받은 코드가 일치하지 않을 경우 BadRequestException`() {
        // given
        val emailVerification = EmailVerification(email = "test@test.com", verificationCode = "234567")
        every { emailVerificationRepository.findByEmail("test@test.com") } returns Optional.of(emailVerification)

        // when
        // then
        val exception = assertThrows(BadRequestException::class.java) {
            emailVerificationService.verifyCode("test@test.com", "123456")
        }
        assertEquals("인증 코드가 일치하지 않습니다.", exception.message)
    }

    @Test
    fun `verifyCode, 인증 코드가 만료된 경우 BadRequestException`() {
        // given
        val emailVerification = EmailVerification(
            email = "test@test.com",
            verificationCode = "123456",
            expiresAt = LocalDateTime.now().minusMinutes(1)
        )
        every { emailVerificationRepository.findByEmail("test@test.com") } returns Optional.of(emailVerification)

        // when
        // then
        val exception = assertThrows(BadRequestException::class.java) {
            emailVerificationService.verifyCode("test@test.com", "123456")
        }
        assertEquals("인증 코드가 만료되었습니다.", exception.message)
    }
}