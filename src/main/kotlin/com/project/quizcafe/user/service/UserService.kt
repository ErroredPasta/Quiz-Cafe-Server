package com.project.quizcafe.user.service

import com.project.quizcafe.user.dto.request.UpdateUserInfoRequest
import com.project.quizcafe.user.dto.response.UserInfoResponse
import com.project.quizcafe.user.entity.User
import com.project.quizcafe.user.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Transactional
@Service
interface UserService {
    fun changePassword(email: String, oldPassword: String, newPassword: String) : Boolean
    fun deleteUser(user: User)
    fun updateUserInfo(user: User, request: UpdateUserInfoRequest)
    fun getUserInfo(user: User): UserInfoResponse

}