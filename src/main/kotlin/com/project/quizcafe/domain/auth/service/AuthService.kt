package com.project.quizcafe.domain.auth.service

import com.project.quizcafe.common.exception.ConflictException
import com.project.quizcafe.global.infrastructure.email.EmailSender
import com.project.quizcafe.common.exception.InternalServerErrorException
import com.project.quizcafe.domain.auth.dto.request.SendCodeRequest
import com.project.quizcafe.domain.auth.dto.request.SignInRequest
import com.project.quizcafe.domain.auth.dto.request.SignUpRequest
import com.project.quizcafe.domain.auth.dto.response.TokenResponse
import com.project.quizcafe.domain.auth.entity.VerificationType
import com.project.quizcafe.domain.auth.security.JwtTokenProvider
import com.project.quizcafe.domain.auth.validator.EmailValidator
import com.project.quizcafe.domain.user.entity.User
import com.project.quizcafe.domain.user.repository.UserRepository
import com.project.quizcafe.domain.user.validator.UserValidator
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import kotlin.random.Random

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val emailValidator: EmailValidator,
    private val emailVerificationService: EmailVerificationService,
    private val emailSender: EmailSender,
    private val userValidator: UserValidator
){
    fun signUp(request: SignUpRequest) {
        if (userRepository.existsByNickName(request.nickName)) {
            throw ConflictException("이미 존재하는 닉네임입니다.")
        }

        emailValidator.validateEmailExist(request.loginEmail)

        val encodedPassword = passwordEncoder.encode(request.password)
        val user = User.createUser(request.loginEmail, encodedPassword, request.nickName)

        try {
            userRepository.save(user)
        } catch (e: Exception) {
            throw InternalServerErrorException("회원가입 실패 ${e.message}")
        }
    }

    fun signIn(request: SignInRequest): TokenResponse {
        val user = emailValidator.validateEmailNotExist(request.loginEmail)
        userValidator.validatePasswordCorrect(request.password, user.password)

        val token = jwtTokenProvider.generateToken(user.loginEmail, user.role)
        return TokenResponse(token)
    }

    fun sendCode(request: SendCodeRequest) {
        when (request.type) {
            VerificationType.SIGN_UP -> emailValidator.validateEmailExist(request.toMail)
            VerificationType.RESET_PASSWORD -> emailValidator.validateEmailNotExist(request.toMail)
        }
        val code = emailVerificationService.generateAndSaveCode(request.toMail)
        emailSender.sendVerificationCode(request.toMail, code)
    }

     fun verifyCode(toMail: String, code: String) {
        emailVerificationService.verifyCode(toMail, code)
    }

    fun resetPassword(email: String) {
        val user = emailValidator.validateEmailNotExist(email)

        val newPassword = Random.nextInt(10000000, 100000000).toString()
        val encodedNewPassword = passwordEncoder.encode(newPassword)
        val updatedUser = user.copy(password = encodedNewPassword)

        try {
            userRepository.save(updatedUser)
        } catch (e: Exception) {
            throw InternalServerErrorException("비밀번호 초기화 실패 ${e.message}")
        }

        emailSender.sendPasswordResetEmail(email, newPassword)
    }

}