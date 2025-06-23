package com.project.quizcafe.domain.auth.service

import com.project.quizcafe.common.exception.AuthenticationException
import com.project.quizcafe.common.exception.ConflictException
import com.project.quizcafe.common.exception.InternalServerErrorException
import com.project.quizcafe.common.exception.NotFoundException
import com.project.quizcafe.common.model.Role
import com.project.quizcafe.domain.auth.dto.request.SendCodeRequest
import com.project.quizcafe.domain.auth.dto.request.SignInRequest
import com.project.quizcafe.domain.auth.dto.request.SignUpRequest
import com.project.quizcafe.domain.auth.entity.VerificationType
import com.project.quizcafe.domain.auth.security.JwtTokenProvider
import com.project.quizcafe.domain.auth.security.oauth.GoogleTokenVerifier
import com.project.quizcafe.domain.auth.validator.EmailValidator
import com.project.quizcafe.domain.user.entity.User
import com.project.quizcafe.domain.user.repository.UserRepository
import com.project.quizcafe.domain.user.validator.UserValidator
import com.project.quizcafe.domain.util.createUser
import com.project.quizcafe.global.infrastructure.email.EmailSender
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
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import kotlin.random.Random
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
class AuthServiceTest {
    @RelaxedMockK
    private lateinit var userRepository: UserRepository

    private lateinit var passwordEncoder: PasswordEncoder

    @RelaxedMockK
    private lateinit var jwtTokenProvider: JwtTokenProvider

    private lateinit var emailValidator: EmailValidator

    @RelaxedMockK
    private lateinit var emailVerificationService: EmailVerificationService

    @RelaxedMockK
    private lateinit var emailSender: EmailSender

    private lateinit var userValidator: UserValidator

    private lateinit var authService: AuthService

    @RelaxedMockK
    private lateinit var googleTokenProvider: GoogleTokenVerifier

    @BeforeEach
    fun setUp() {
        mockkObject(Random.Default)
        passwordEncoder = BCryptPasswordEncoder()
        emailValidator = EmailValidator(userRepository)
        userValidator = UserValidator(passwordEncoder)
        authService = AuthService(
            userRepository = userRepository,
            passwordEncoder = passwordEncoder,
            jwtTokenProvider = jwtTokenProvider,
            emailValidator = emailValidator,
            emailVerificationService = emailVerificationService,
            emailSender = emailSender,
            userValidator = userValidator,
            googleTokenVerifier = googleTokenProvider
        )
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `signUp, 새로운 로컬 유저 생성 후 저장`() {
        // given
        val signUpRequest = createSignUpRequest(
            loginEmail = "test@test.com",
            password = "test password",
            nickName = "test nickName",
        )
        val userSlot = slot<User>()
        every { userRepository.existsByNickName("test nickName") } returns false
        every { userRepository.existsByLoginEmail("test@test.com") } returns false
        every { userRepository.save(capture(userSlot)) } answers { userSlot.captured }

        // when
        authService.signUp(signUpRequest)

        // then
        verify(exactly = 1) { userRepository.save(any()) }

        val capturedUser = userSlot.captured
        assertAll(
            { assertEquals(capturedUser.loginEmail, "test@test.com") },
            { assertEquals(capturedUser.nickName, "test nickName") },
            { assertEquals(passwordEncoder.matches("test password", capturedUser.password), true) },
            { assertEquals(capturedUser.role, Role.USER) },
            { assertEquals(capturedUser.nickName, "test nickName") },
        )
    }

    @Test
    fun `signUp, 이미 같은 이메일이 존재하는 경우 ConflictException`() {
        // given
        val signUpRequest = createSignUpRequest(
            loginEmail = "test@test.com",
            password = "test password",
            nickName = "test nickName",
        )
        every { userRepository.existsByNickName("test nickName") } returns false
        every { userRepository.existsByLoginEmail("test@test.com") } returns true

        // when
        // then
        val exception = assertThrows(ConflictException::class.java) {
            authService.signUp(signUpRequest)
        }
        assertEquals(exception.message, "이미 존재하는 이메일입니다.")
    }

    @Test
    fun `signIn, 알맞은 메일과 비밀번호 입력시 token return`() {
        // given
        val signInRequest = createSignInRequest(
            loginEmail = "test@test.com",
            password = "test password",
        )
        val user = createUser(
            loginEmail = "test@test.com",
            password = passwordEncoder.encode("test password"),
        )
        val expectedJwt =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0QHRlc3QuY29tIiwicm9sZSI6IlVTRVIiLCJpYXQiOjE3Mjk1MzE1NDcsImV4cCI6MTcyOTUzNTE0N30.80355336VJlgrkFwqPwnHMwKY3nfkiYqvnP1Hno5J5M"

        every { userRepository.findByLoginEmail("test@test.com") } returns user
        every { jwtTokenProvider.generateToken("test@test.com", Role.USER) } returns expectedJwt

        // when
        val result = authService.signIn(signInRequest)

        // then
        assertEquals(result.accessToken, expectedJwt)
    }

    @Test
    fun `signIn, 유저가 없을 경우 NotFoundException`() {
        // given
        val signInRequest = createSignInRequest(
            loginEmail = "notfound@test.com",
            password = "test password",
        )
        every { userRepository.findByLoginEmail("notfound@test.com") } returns null

        // when
        // then
        val exception = assertThrows(NotFoundException::class.java) {
            authService.signIn(signInRequest)
        }
        assertEquals(exception.message, "존재하지 않는 이메일입니다.")
    }

    @Test
    fun `signIn, 비밀번호가 올바르지 않을 경우 AuthenticationException`() {
        // given
        val signInRequest = createSignInRequest(
            loginEmail = "notfound@test.com",
            password = "invalid password",
        )
        val user = createUser(
            loginEmail = "test@test.com",
            password = passwordEncoder.encode("test password"),
        )
        every { userRepository.findByLoginEmail("notfound@test.com") } returns user

        // when
        // then
        val exception = assertThrows(AuthenticationException::class.java) {
            authService.signIn(signInRequest)
        }
        assertEquals(exception.message, "비밀번호가 일치하지 않습니다.")
    }

    @Test
    fun `sendCode, 회원 가입시 인증 메일 전송`() {
        // given
        val sendCodeRequest = createSendCodeRequest(
            toMail = "test@test.com",
            type = VerificationType.SIGN_UP
        )
        every { userRepository.existsByLoginEmail("test@test.com") } returns false
        every { emailVerificationService.generateAndSaveCode("test@test.com") } returns "123456"

        // when
        authService.sendCode(sendCodeRequest)

        // then
        verify(exactly = 1) { emailSender.sendVerificationCode("test@test.com", "123456") }
    }

    @Test
    fun `sendCode, 가입 인증 메일 전송시 이미 가입한 메일이 있을 경우 ConflictException`() {
        // given
        val sendCodeRequest = createSendCodeRequest(
            toMail = "test@test.com",
            type = VerificationType.SIGN_UP
        )
        every { userRepository.existsByLoginEmail("test@test.com") } returns true

        // when
        // then
        val exception = assertThrows(ConflictException::class.java) {
            authService.sendCode(sendCodeRequest)
        }
        assertEquals(exception.message, "이미 존재하는 이메일입니다.")
    }

    @Test
    fun `sendCode, 비밀번호 리셋 메일 전송`() {
        // given
        val sendCodeRequest = createSendCodeRequest(
            toMail = "test@test.com",
            type = VerificationType.RESET_PASSWORD
        )
        val user = createUser(
            loginEmail = "test@test.com",
            password = passwordEncoder.encode("test password"),
        )
        every { userRepository.findByLoginEmail("test@test.com") } returns user
        every { emailVerificationService.generateAndSaveCode("test@test.com") } returns "123456"

        // when
        authService.sendCode(sendCodeRequest)

        // then
        verify(exactly = 1) { emailSender.sendVerificationCode("test@test.com", "123456") }
    }

    @Test
    fun `sendCode, 비밀번호 리셋 메일시 가입한 메일이 없을 경우 NotFoundException`() {
        // given
        val sendCodeRequest = createSendCodeRequest(
            toMail = "test@test.com",
            type = VerificationType.RESET_PASSWORD
        )
        every { userRepository.findByLoginEmail("test@test.com") } returns null

        // when
        // then
        val exception = assertThrows(NotFoundException::class.java) {
            authService.sendCode(sendCodeRequest)
        }
        assertEquals(exception.message, "존재하지 않는 이메일입니다.")
    }

    @Test
    fun `resetPassword, 메일로 새로운 비밀번호 전송`() {
        // given
        val user = createUser(loginEmail = "test@test.com")
        every { userRepository.findByLoginEmail("test@test.com") } returns user
        every { Random.nextInt(10000000, 100000000) } returns 12345678

        val userSlot = slot<User>()
        every { userRepository.save(capture(userSlot)) } answers { userSlot.captured }

        // when
        authService.resetPassword("test@test.com")

        // then
        verify(exactly = 1) { emailSender.sendPasswordResetEmail("test@test.com", "12345678") }
    }

    @Test
    fun `resetPassword, 유저 정보에 새로운 비밀번호를 저장`() {
        // given
        val user = createUser(loginEmail = "test@test.com")
        every { userRepository.findByLoginEmail("test@test.com") } returns user
        every { Random.nextInt(10000000, 100000000) } returns 12345678

        val userSlot = slot<User>()
        every { userRepository.save(capture(userSlot)) } answers { userSlot.captured }

        // when
        authService.resetPassword("test@test.com")

        // then
        val capturedUser = userSlot.captured
        assertEquals(passwordEncoder.matches("12345678", capturedUser.password), true)
    }

    @Test
    fun `resetPassword, 존재하지 않는 유저의 비밀번호 리셋시 NotFoundException`() {
        // given
        every { userRepository.findByLoginEmail("test@test.com") } returns null

        // when
        // then
        val exception = assertThrows(NotFoundException::class.java) {
            authService.resetPassword("test@test.com")
        }
        assertEquals(exception.message, "존재하지 않는 이메일입니다.")
    }

    @Test
    fun `resetPassword, 유저 저장시 예외가 발생하면 InternalServerErrorException`() {
        // given
        val user = createUser(loginEmail = "test@test.com")
        every { userRepository.findByLoginEmail("test@test.com") } returns user
        every { Random.nextInt(10000000, 100000000) } returns 12345678
        every { userRepository.save(any()) } throws Exception()

        // when
        // then
        val exception = assertThrows(InternalServerErrorException::class.java) {
            authService.resetPassword("test@test.com")
        }
        assertEquals(exception.message?.contains("비밀번호 초기화 실패"), true)
    }

    private fun createSignUpRequest(
        loginEmail: String = "default@email.com",
        password: String = "default password",
        nickName: String = "default nickName",
    ) = SignUpRequest(
        loginEmail = loginEmail,
        password = password,
        nickName = nickName
    )

    private fun createSignInRequest(
        loginEmail: String = "default@email.com",
        password: String = "default password",
    ) = SignInRequest(
        loginEmail = loginEmail,
        password = password
    )

    private fun createSendCodeRequest(
        toMail: String = "default@email.com",
        type: VerificationType
    ) = SendCodeRequest(
        toMail = toMail, type = type

    )
}