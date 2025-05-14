package com.project.quizcafe.domain.user.service

import com.project.quizcafe.domain.user.dto.request.UpdateUserInfoRequest
import com.project.quizcafe.domain.user.dto.response.UserInfoResponse
import com.project.quizcafe.domain.user.entity.User
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