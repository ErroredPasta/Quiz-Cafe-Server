package com.project.quizcafe.user.service

import com.project.quizcafe.user.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Transactional
@Service
interface UserService {
    fun changePassword(email: String, oldPassword: String, newPassword: String) : Boolean
}