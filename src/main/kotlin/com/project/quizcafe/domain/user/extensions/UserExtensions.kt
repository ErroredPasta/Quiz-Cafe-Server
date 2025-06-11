package com.project.quizcafe.domain.user.extensions

import com.project.quizcafe.domain.user.dto.request.UpdateUserInfoRequest
import com.project.quizcafe.domain.user.dto.response.UserInfoResponse
import com.project.quizcafe.domain.user.entity.User

fun User.toUserInfoResponse(): UserInfoResponse {
    return UserInfoResponse(
        nickname = this.nickName,
        email = this.loginEmail
    )
}

fun UpdateUserInfoRequest.applyTo(user: User) {
    user.nickName = nickname
}