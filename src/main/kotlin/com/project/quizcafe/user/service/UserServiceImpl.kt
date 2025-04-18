package com.project.quizcafe.user.service

import com.project.quizcafe.user.entity.User
import com.project.quizcafe.user.repository.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import kotlin.IllegalArgumentException

@Service
class UserServiceImpl(private val userRepository: UserRepository) : UserService {
    private val passwordEncoder = BCryptPasswordEncoder()

    override fun changePassword(email: String, oldPassword: String, newPassword: String): Boolean {
        val user = userRepository.findByLoginEmail(email)
            ?: throw IllegalArgumentException("사용자를 찾을 수 없습니다.")

        // 기존 비밀번호가 맞는지 확인
        if (!passwordEncoder.matches(oldPassword, user.password)) {
            throw IllegalArgumentException("기존 비밀번호가 맞지 않습니다.")
        }

        // 새 비밀번호 암호화
        val encodedNewPassword = passwordEncoder.encode(newPassword)

        // 비밀번호 업데이트
        val updatedUser = user.copy(password = encodedNewPassword)
        userRepository.save(updatedUser)

        return true
    }
}