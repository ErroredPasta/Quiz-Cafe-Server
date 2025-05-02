package com.project.quizcafe.user.repository
import com.project.quizcafe.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository :  JpaRepository<User, Long>{
    fun findByLoginEmail(loginEmail: String): User?
    fun existsByLoginEmail(loginEmail: String): Boolean
    fun existsByNickName(nickName: String): Boolean

}