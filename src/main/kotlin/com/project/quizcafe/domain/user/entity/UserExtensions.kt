package com.project.quizcafe.domain.user.entity

import com.project.quizcafe.domain.user.dto.response.UserInfoResponse

fun User.toUserInfoResponse(): UserInfoResponse {
    return UserInfoResponse(
        nickname = this.nickName,
        email = this.loginEmail
    )
}
