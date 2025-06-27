package com.project.quizcafe.domain.user.service

import com.project.quizcafe.common.exception.AuthenticationException
import com.project.quizcafe.common.exception.InternalServerErrorException
import com.project.quizcafe.common.exception.NotFoundException
import com.project.quizcafe.domain.auth.validator.EmailValidator
import com.project.quizcafe.domain.user.dto.response.UserInfoResponse
import com.project.quizcafe.domain.user.entity.User
import com.project.quizcafe.domain.user.repository.UserRepository
import com.project.quizcafe.domain.user.validator.UserValidator
import com.project.quizcafe.domain.util.createUser
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.slot
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDateTime
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
class UserServiceTest {
    @RelaxedMockK
    private lateinit var userRepository: UserRepository

    private lateinit var emailValidator: EmailValidator
    private lateinit var userValidator: UserValidator
    private lateinit var passwordEncoder: PasswordEncoder

    private lateinit var userService: UserService

    @BeforeEach
    fun setUp() {
        passwordEncoder = BCryptPasswordEncoder()
        emailValidator = EmailValidator(userRepository)
        userValidator = UserValidator(passwordEncoder)
        userService = UserService(
            userRepository = userRepository,
            emailValidator = emailValidator,
            userValidator = userValidator
        )
    }

    @Test
    fun `changePassword, 새로운 비밀번호가 유저 정보에 저장`() {
        // given
        val user = createUser(
            loginEmail = "test@test.com",
            password = passwordEncoder.encode("old password")
        )
        every { userRepository.findByLoginEmail("test@test.com") } returns user

        val userSlot = slot<User>()
        every { userRepository.save(capture(userSlot)) } answers { userSlot.captured }

        // when
        userService.changePassword("test@test.com", "old password", "new password")

        // then
        val capturedUser = userSlot.captured
        assertEquals(true, passwordEncoder.matches("new password", capturedUser.password))
    }

    @Test
    fun `changePassword, 해당 이메일의 유저가 없을 경우 NotFoundException`() {
        // given
        every { userRepository.findByLoginEmail("test@test.com") } returns null

        // when
        // then
        val exception = assertThrows(NotFoundException::class.java) {
            userService.changePassword("test@test.com", "old password", "new password")

        }
        assertEquals("존재하지 않는 이메일입니다.", exception.message)
    }

    @Test
    fun `changePassword, 비밀번호가 다를 경우 AuthenticationException`() {
        // given
        val user = createUser(
            loginEmail = "test@test.com",
            password = passwordEncoder.encode("old password")
        )
        every { userRepository.findByLoginEmail("test@test.com") } returns user

        // when
        // then
        val exception = assertThrows(AuthenticationException::class.java) {
            userService.changePassword("test@test.com", "wrong password", "new password")

        }
        assertEquals("비밀번호가 일치하지 않습니다.", exception.message)
    }

    @Test
    fun `changePassword, 새로운 비밀번호 저장시 예외 발생하면 InternalServerErrorException`() {
        // given
        val user = createUser(
            loginEmail = "test@test.com",
            password = passwordEncoder.encode("old password")
        )
        every { userRepository.findByLoginEmail("test@test.com") } returns user
        every { userRepository.save(any()) } throws Exception()

        // when
        // then
        val exception = assertThrows(InternalServerErrorException::class.java) {
            userService.changePassword("test@test.com", "old password", "new password")

        }
        assertEquals(true, exception.message?.contains("비밀번호 변경 실패"))
    }

    @Test
    fun `deleteUser, 유저 삭제`() {
        // given
        val user = createUser(id = 2331)
        val userSlot = slot<User>()
        every { userRepository.delete(capture(userSlot)) } just Runs

        // when
        userService.deleteUser(user)

        // then
        val capturedUser = userSlot.captured
        assertEquals(2331, capturedUser.id)
    }

    @Test
    fun `deleteUser, 유저 삭제시 예외 발생하면 InternalServerErrorException`() {
        // given
        every { userRepository.delete(any()) } throws Exception()

        // when
        // then
        val exception = assertThrows(InternalServerErrorException::class.java) {
            userService.deleteUser(createUser())
        }
        assertEquals(true, exception.message?.contains("회원 탈퇴 실패"))
    }

    @Test
    fun `getUserInfo, 유저의 메일과 닉네임만 return`() {
        // given
        val user = createUser(
            loginEmail = "test@test.com",
            nickName = "test nickName",
            createdAt = LocalDateTime.of(2025, 1, 1, 1, 1, 0),
        )

        // when
        val result = userService.getUserInfo(user)

        // then
        val expected = UserInfoResponse(
            nickname = "test nickName",
            email = "test@test.com",
            createdAt = LocalDateTime.of(2025, 1, 1, 1, 1, 0)
        )
        assertEquals(expected, result)
    }
}