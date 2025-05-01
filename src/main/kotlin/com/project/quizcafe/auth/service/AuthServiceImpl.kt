package com.project.quizcafe.auth.service

import com.project.quizcafe.auth.dto.request.SendCodeRequest
import com.project.quizcafe.auth.dto.request.SignInRequest
import com.project.quizcafe.auth.dto.request.SignUpRequest
import com.project.quizcafe.auth.dto.response.TokenResponse
import com.project.quizcafe.auth.entity.EmailVerification
import com.project.quizcafe.auth.entity.VerificationType
import com.project.quizcafe.auth.repository.EmailVerificationRepository
import com.project.quizcafe.auth.security.JwtTokenProvider
import com.project.quizcafe.common.model.Role
import com.project.quizcafe.user.entity.User
import com.project.quizcafe.user.repository.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
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

    override fun signUp(request: SignUpRequest){
        if (userRepository.existsByLoginEmail(request.loginEmail)) {
            throw IllegalArgumentException("이미 존재하는 이메일입니다.")
        }
        val encodedPassword = passwordEncoder.encode(request.password)

        val user = User(
            null,
            loginEmail = request.loginEmail,
            password = encodedPassword,
            nickName = request.nickName,
            role = Role.USER
        )

        userRepository.save(user)
    }

    override fun signIn(request: SignInRequest): TokenResponse {
        val user = userRepository.findByLoginEmail(request.loginEmail)
            ?: throw IllegalArgumentException("존재하지 않는 이메일입니다.")
        if (!passwordEncoder.matches(request.password, user.password)) {
            throw IllegalArgumentException("비밀번호가 일치하지 않습니다.")
        }

        val token = jwtTokenProvider.generateToken(user.loginEmail, user.role)
        return TokenResponse(token)
    }

    override fun sendCode(toMail: String, type: VerificationType){

        when (type) {
            VerificationType.SIGN_UP -> {
                if (userRepository.existsByLoginEmail(toMail)) {
                    throw IllegalArgumentException("이미 존재하는 이메일입니다.")
                }
            }
            VerificationType.RESET_PASSWORD -> {
                if (!userRepository.existsByLoginEmail(toMail)) {
                    throw IllegalArgumentException("존재하지 않는 이메일입니다.")
                }
            }
        }

        val mimeMessage = mailSender.createMimeMessage()
        val mimeMessageHelper = MimeMessageHelper(mimeMessage, false, null)
        val senderName = "QuizCafe"
        mimeMessageHelper.setFrom(senderEmail, senderName)
        mimeMessageHelper.setTo(toMail)
        mimeMessageHelper.setSubject("인증번호")
        val verificationCode  = Random.nextInt(100000,1000000).toString()
        val emailVerification = EmailVerification(
            email = toMail,
            verificationCode = verificationCode
        )

        emailVerificationRepository.deleteByEmail(toMail)
        emailVerificationRepository.save(emailVerification)

        mimeMessageHelper.setText(verificationCode )
        mailSender.send(mimeMessage)
    }

    // 인증 코드 검증
    override fun verifyCode(toMail: String, code: String): Boolean {
        val emailVerification = emailVerificationRepository.findByEmail(toMail)
            .orElseThrow { IllegalArgumentException("등록된 인증 코드가 없습니다.") }

        // 인증 코드가 일치하고, 유효 기간이 지나지 않았는지 확인
        return emailVerification.verificationCode == code && emailVerification.expiresAt.isAfter(LocalDateTime.now())
    }

    override fun resetPassword(email: String) : Boolean{
        val user = userRepository.findByLoginEmail(email)
            ?: throw IllegalArgumentException("사용자를 찾을 수 없습니다.")

        val mimeMessage = mailSender.createMimeMessage()
        val mimeMessageHelper = MimeMessageHelper(mimeMessage, false, null)
        val senderName = "QuizCafe"
        mimeMessageHelper.setFrom(senderEmail, senderName)
        mimeMessageHelper.setTo(email)
        mimeMessageHelper.setSubject("비밀번호 재설정")
        val newPassword = Random.nextInt(10000000,100000000).toString()
        mimeMessageHelper.setText(newPassword)
        mailSender.send(mimeMessage)

        val encodedNewPassword = passwordEncoder.encode(newPassword)
        val updatedUser = user.copy(password = encodedNewPassword)
        userRepository.save(updatedUser)
        return true
    }

}