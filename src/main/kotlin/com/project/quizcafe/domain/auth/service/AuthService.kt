package com.project.quizcafe.domain.auth.service

import com.project.quizcafe.common.exception.AuthenticationException
import com.project.quizcafe.common.exception.ConflictException
import com.project.quizcafe.global.infrastructure.email.EmailSender
import com.project.quizcafe.common.exception.InternalServerErrorException
import com.project.quizcafe.common.model.Role
import com.project.quizcafe.domain.auth.dto.request.SendCodeRequest
import com.project.quizcafe.domain.auth.dto.request.SignInRequest
import com.project.quizcafe.domain.auth.dto.request.SignUpRequest
import com.project.quizcafe.domain.auth.dto.response.TokenResponse
import com.project.quizcafe.domain.auth.entity.VerificationType
import com.project.quizcafe.domain.auth.security.JwtTokenProvider
import com.project.quizcafe.domain.auth.security.oauth.GoogleTokenVerifier
import com.project.quizcafe.domain.auth.validator.EmailValidator
import com.project.quizcafe.domain.user.entity.User
import com.project.quizcafe.domain.user.repository.UserRepository
import com.project.quizcafe.domain.user.validator.UserValidator
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.Duration
import kotlin.random.Random


@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val emailValidator: EmailValidator,
    private val emailVerificationService: EmailVerificationService,
    private val emailSender: EmailSender,
    private val userValidator: UserValidator,
    private val googleTokenVerifier: GoogleTokenVerifier,
    private val redisTemplate: RedisTemplate<String, String>,
    ){
    fun signUp(request: SignUpRequest) {

//        if (userRepository.existsByNickName(request.nickName)) {
//            throw ConflictException("이미 존재하는 닉네임입니다.")
//        }

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
        userValidator.validateOauthUser(user)

        val token = jwtTokenProvider.generateToken(user.loginEmail, user.role)
        val refreshToken = jwtTokenProvider.generateRefreshToken(user.loginEmail,user.role)
        redisTemplate.opsForValue().set(user.loginEmail, refreshToken, Duration.ofDays(7))

        return TokenResponse(
            accessToken = token,
            refreshToken = refreshToken
        )
    }

    fun reissue(refreshToken: String) : TokenResponse{
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw AuthenticationException("유효하지 않은 Refresh Token 입니다.")
        }
        val email = jwtTokenProvider.getEmail(refreshToken)
        val roleString = jwtTokenProvider.getRole(refreshToken)
        val role = Role.valueOf(roleString)
        val savedRefreshToken = redisTemplate.opsForValue().get(email)
            ?: throw AuthenticationException("로그아웃되었거나 토큰이 만료되었습니다.")
        if (savedRefreshToken != refreshToken) {
            throw AuthenticationException("Refresh Token 정보가 일치하지 않습니다.")
        }
        val newAccessToken = jwtTokenProvider.generateToken(email, role)
        val newRefreshToken = jwtTokenProvider.generateRefreshToken(email, role)
        redisTemplate.opsForValue().set(email, newRefreshToken, Duration.ofDays(7))
        return TokenResponse(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken
        )
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

    fun googleLogin(idToken: String): TokenResponse {
        val payload = googleTokenVerifier.verify(idToken)
            ?: throw AuthenticationException("구글 ID 토큰 검증 실패")

        val email = payload.email
        val name = payload["name"] as? String ?: "Unknown"
        var user = userRepository.findByLoginEmail(email)

        if(user != null){
            if(user.provider != User.Provider.GOOGLE)
                throw ConflictException("이미 존재하는 이메일입니다.")
        }else{
            user = User.createOAuthUser(email, name, User.Provider.GOOGLE)
            user = userRepository.save(user)
        }

        val token = jwtTokenProvider.generateToken(user.loginEmail, user.role)
        val refreshToken = jwtTokenProvider.generateRefreshToken(user.loginEmail,user.role)
        redisTemplate.opsForValue().set(user.loginEmail, refreshToken, Duration.ofDays(7))

        return TokenResponse(
            accessToken = token,
            refreshToken = refreshToken
        )    }

}