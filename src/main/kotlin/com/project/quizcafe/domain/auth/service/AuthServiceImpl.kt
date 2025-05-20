package com.project.quizcafe.domain.auth.service

import com.project.quizcafe.common.exception.AuthenticationException
import com.project.quizcafe.common.exception.BadRequestException
import com.project.quizcafe.common.exception.ConflictException
import com.project.quizcafe.common.exception.InternalServerErrorException
import com.project.quizcafe.common.exception.NotFoundException
import com.project.quizcafe.common.model.Role
import com.project.quizcafe.domain.auth.dto.request.SignInRequest
import com.project.quizcafe.domain.auth.dto.request.SignUpRequest
import com.project.quizcafe.domain.auth.dto.response.TokenResponse
import com.project.quizcafe.domain.auth.entity.EmailVerification
import com.project.quizcafe.domain.auth.entity.VerificationType
import com.project.quizcafe.domain.auth.repository.EmailVerificationRepository
import com.project.quizcafe.domain.auth.security.JwtTokenProvider
import com.project.quizcafe.domain.user.entity.User
import com.project.quizcafe.domain.user.repository.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.MailException
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.io.UnsupportedEncodingException
import java.time.LocalDateTime
import kotlin.random.Random

@Service
class AuthServiceImpl(
    private val userRepository: UserRepository,
    private val emailVerificationRepository: EmailVerificationRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val mailSender: JavaMailSender
) : AuthService {
    private val verificationCodes: MutableMap<String, String> = mutableMapOf()

    @Value("\${spring.mail.username}")
    private lateinit var senderEmail: String

    override fun signUp(request: SignUpRequest) {
        if (!request.loginEmail.matches(Regex("^[A-Za-z0-9+_.-]+@(.+)$"))) {
            throw BadRequestException("잘못된 이메일 형식입니다.")
        }

        if (userRepository.existsByLoginEmail(request.loginEmail)) {
            throw ConflictException("이미 존재하는 이메일입니다.")
        }

        if (request.password.length < 8) {
            throw BadRequestException("비밀번호는 최소 8자 이상이어야 합니다.")
        }

        if (userRepository.existsByNickName(request.nickName)) {
            throw ConflictException("이미 사용 중인 닉네임입니다.")
        }

        val encodedPassword = passwordEncoder.encode(request.password)

        val user = User(
            loginEmail = request.loginEmail,
            password = encodedPassword,
            nickName = request.nickName,
            role = Role.USER
        )

        try {
            userRepository.save(user)
        } catch (e: Exception) {
            throw InternalServerErrorException("회원가입 처리 중 오류가 발생했습니다.")
        }
    }


    override fun signIn(request: SignInRequest): TokenResponse {
        val user = userRepository.findByLoginEmail(request.loginEmail)
            ?: throw AuthenticationException("존재하지 않는 이메일입니다.")
        if (!passwordEncoder.matches(request.password, user.password)) {
            throw AuthenticationException("비밀번호가 일치하지 않습니다.")
        }

        val token = jwtTokenProvider.generateToken(user.loginEmail, user.role)
        return TokenResponse(token)
    }

    override fun sendCode(toMail: String, type: VerificationType) {

        // 이메일 사용 여부에 따라 예외 처리
        when (type) {
            VerificationType.SIGN_UP -> {
                if (userRepository.existsByLoginEmail(toMail)) {
                    throw ConflictException("이미 존재하는 이메일입니다.")
                }
            }

            VerificationType.RESET_PASSWORD -> {
                if (!userRepository.existsByLoginEmail(toMail)) {
                    throw NotFoundException("존재하지 않는 이메일입니다.")
                }
            }
        }

        // 인증 코드 생성
        val verificationCode = Random.nextInt(100000, 1000000).toString()

        // 이메일 인증 정보 저장 (기존 기록 삭제 후 저장)
        try {
            emailVerificationRepository.deleteByEmail(toMail)
            emailVerificationRepository.save(
                EmailVerification(
                    email = toMail,
                    verificationCode = verificationCode
                )
            )
        } catch (e: Exception) {
            throw InternalServerErrorException("인증 코드 저장 중 오류가 발생했습니다.")
        }

        // 메일 객체 생성 및 전송
        try {
            val mimeMessage = mailSender.createMimeMessage()
            val helper = MimeMessageHelper(mimeMessage, false, "UTF-8")

            try {
                helper.setFrom(senderEmail, "QuizCafe")
            } catch (e: UnsupportedEncodingException) {
                throw InternalServerErrorException("발신자 이름 인코딩 중 오류가 발생했습니다.")
            }

            helper.setTo(toMail)
            helper.setSubject("QuizCafe 이메일 인증번호")
            helper.setText("인증번호: $verificationCode")

            mailSender.send(mimeMessage)

        } catch (e: MailException) {
            throw InternalServerErrorException("이메일 전송 중 오류가 발생했습니다.")
        } catch (e: Exception) {
            throw InternalServerErrorException("이메일 생성 중 오류가 발생했습니다.")
        }
    }


    // 인증 코드 검증
    override fun verifyCode(toMail: String, code: String): Boolean {
        val emailVerification = emailVerificationRepository.findByEmail(toMail)
            .orElseThrow { NotFoundException("등록된 인증 코드가 없습니다.") }

        return if (emailVerification.verificationCode == code) {
            if (emailVerification.expiresAt.isAfter(LocalDateTime.now())) {
                true
            } else {
                throw BadRequestException("인증 코드가 만료되었습니다.")
            }
        } else {
            throw BadRequestException("인증 코드가 일치하지 않습니다.")
        }
    }

    override fun resetPassword(email: String): Boolean {
        val user = userRepository.findByLoginEmail(email)
            ?: throw NotFoundException("사용자를 찾을 수 없습니다.")

        val mimeMessage = mailSender.createMimeMessage()
        val mimeMessageHelper = MimeMessageHelper(mimeMessage, false, null)
        val senderName = "QuizCafe"

        try {
            mimeMessageHelper.setFrom(senderEmail, senderName)
            mimeMessageHelper.setTo(email)
            mimeMessageHelper.setSubject("비밀번호 재설정")
        } catch (e: UnsupportedEncodingException) {
            throw InternalServerErrorException("발신자 이름 인코딩 중 오류가 발생했습니다.")
        }

        val newPassword = Random.nextInt(10000000, 100000000).toString()
        mimeMessageHelper.setText(newPassword)

        try {
            mailSender.send(mimeMessage)
        } catch (e: MailException) {
            throw InternalServerErrorException("이메일 전송 중 오류가 발생했습니다.")
        }

        val encodedNewPassword = passwordEncoder.encode(newPassword)
        val updatedUser = user.copy(password = encodedNewPassword)

        try {
            userRepository.save(updatedUser)
        } catch (e: Exception) {
            throw InternalServerErrorException("비밀번호 저장 중 오류가 발생했습니다.")
        }

        return true
    }

}