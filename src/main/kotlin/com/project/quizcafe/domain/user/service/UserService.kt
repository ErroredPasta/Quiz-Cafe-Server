package com.project.quizcafe.domain.user.service

import com.project.quizcafe.common.exception.InternalServerErrorException
import com.project.quizcafe.domain.auth.validator.EmailValidator
import com.project.quizcafe.domain.user.dto.request.UpdateUserInfoRequest
import com.project.quizcafe.domain.user.dto.response.UserInfoResponse
import com.project.quizcafe.domain.user.entity.User
import com.project.quizcafe.domain.user.extensions.applyTo
import com.project.quizcafe.domain.user.extensions.toUserInfoResponse
import com.project.quizcafe.domain.user.repository.UserRepository
import com.project.quizcafe.domain.user.validator.UserValidator
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val emailValidator: EmailValidator,
    private val userValidator: UserValidator
) {
    private val passwordEncoder = BCryptPasswordEncoder()

    fun changePassword(email: String, oldPassword: String, newPassword: String) {
        val user = emailValidator.validateEmailNotExist(email)
        userValidator.validatePasswordCorrect(oldPassword, user.password)

        val encodedNewPassword = passwordEncoder.encode(newPassword)
        val updatedUser = user.copy(password = encodedNewPassword)

        try {
            userRepository.save(updatedUser)
        } catch (e: Exception) {
            throw InternalServerErrorException("비밀번호 변경 실패 ${e.message}")
        }
    }

    fun deleteUser(user: User) {
        try {
            userRepository.delete(user)
        } catch (e: Exception) {
            throw InternalServerErrorException("회원 탈퇴 실패 ${e.message}")
        }
    }

    fun updateUserInfo(user: User, request: UpdateUserInfoRequest) {
        request.applyTo(user)
    }

    fun getUserInfo(user: User): UserInfoResponse {
        return user.toUserInfoResponse()
    }
}