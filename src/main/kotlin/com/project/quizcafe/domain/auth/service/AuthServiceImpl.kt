package com.project.quizcafe.domain.auth.service

import com.project.quizcafe.global.infrastructure.email.EmailSender
import com.project.quizcafe.common.exception.InternalServerErrorException
import com.project.quizcafe.common.exception.NotFoundException
import com.project.quizcafe.domain.auth.dto.request.SendCodeRequest
import com.project.quizcafe.domain.auth.dto.request.SignInRequest
import com.project.quizcafe.domain.auth.dto.request.SignUpRequest
import com.project.quizcafe.domain.auth.dto.response.TokenResponse
import com.project.quizcafe.domain.auth.entity.VerificationType
import com.project.quizcafe.domain.auth.repository.EmailVerificationRepository
import com.project.quizcafe.domain.auth.security.JwtTokenProvider
import com.project.quizcafe.domain.auth.validator.EmailValidator
import com.project.quizcafe.domain.auth.validator.SignInValidator
import com.project.quizcafe.domain.auth.validator.SignUpValidator
import com.project.quizcafe.domain.user.entity.User
import com.project.quizcafe.domain.user.repository.UserRepository
import com.project.quizcafe.domain.user.validator.UserValidator
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.MailException
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.io.UnsupportedEncodingException
import kotlin.random.Random

@Service
class AuthServiceImpl(
    private val userRepository: UserRepository,
    private val emailVerificationRepository: EmailVerificationRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val mailSender: JavaMailSender,
    private val signUpValidator: SignUpValidator,
    private val signInValidator: SignInValidator,
    private val emailValidator: EmailValidator,
    private val emailVerificationService: EmailVerificationService,
    private val emailSender: EmailSender,
    private val userValidator: UserValidator
) : AuthService {
    private val verificationCodes: MutableMap<String, String> = mutableMapOf()

    @Value("\${spring.mail.username}")
    private lateinit var senderEmail: String

    override fun signUp(request: SignUpRequest) {
        userValidator.validateNickNameExist(request.nickName)
        emailValidator.validateEmailExist(request.loginEmail)

        val encodedPassword = passwordEncoder.encode(request.password)
        val user = User.createUser(request.loginEmail, encodedPassword, request.nickName)

        try {
            userRepository.save(user)
        } catch (e: Exception) {
            throw InternalServerErrorException("회원가입 실패")
        }
    }

    override fun signIn(request: SignInRequest): TokenResponse {
        val user = emailValidator.validateEmailNotExist(request.loginEmail)
        userValidator.validatePasswordCorrect(request.password, user.password)

        val token = jwtTokenProvider.generateToken(user.loginEmail, user.role)
        return TokenResponse(token)
    }

    override fun sendCode(request: SendCodeRequest) {
        when (request.type) {
            VerificationType.SIGN_UP -> emailValidator.validateEmailExist(request.toMail)
            VerificationType.RESET_PASSWORD -> emailValidator.validateEmailNotExist(request.toMail)
        }
        val code = emailVerificationService.generateAndSaveCode(request.toMail)
        emailSender.sendVerificationCode(request.toMail, code)
    }

    override fun verifyCode(toMail: String, code: String) {
        emailVerificationService.verifyCode(toMail, code)
    }

    override fun resetPassword(email: String) {
        val user = emailValidator.validateEmailNotExist(email)

        val newPassword = Random.nextInt(10000000, 100000000).toString()
        val encodedNewPassword = passwordEncoder.encode(newPassword)
        val updatedUser = user.copy(password = encodedNewPassword)

        try {
            userRepository.save(updatedUser)
        } catch (e: Exception) {
            throw InternalServerErrorException("비밀번호 초기화 실패")
        }

        emailSender.sendPasswordResetEmail(email, newPassword)
    }

}