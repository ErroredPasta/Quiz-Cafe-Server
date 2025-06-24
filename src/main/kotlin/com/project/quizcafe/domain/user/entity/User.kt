package com.project.quizcafe.domain.user.entity

import com.project.quizcafe.domain.quizbook.entity.QuizBook
import com.project.quizcafe.domain.quizbook.entity.QuizBookBookmark
import com.project.quizcafe.domain.quizbooksolving.entity.QuizBookSolving
import com.project.quizcafe.domain.quizsolving.entity.QuizSolving
import com.project.quizcafe.common.model.Role
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

@Entity
@Table(
    uniqueConstraints = [UniqueConstraint(name = "uk_user_login_email", columnNames = ["login_email"])]
)
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "login_email", nullable = false, unique = true)
    val loginEmail: String,

    @Column(nullable = false)
    val password: String,

    @Column(name = "nick_name", nullable = false)
    var nickName: String,

    @Enumerated(EnumType.STRING)
    val role: Role = Role.USER,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val provider: Provider = Provider.LOCAL,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime? = null,

    @OneToMany(mappedBy = "createdBy", cascade = [CascadeType.ALL], orphanRemoval = true)
    val quizBooks: MutableList<QuizBook> = mutableListOf(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    val quizBookBookmarks: MutableList<QuizBookBookmark> = mutableListOf(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    val quizBookSolvings: MutableList<QuizBookSolving> = mutableListOf(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    val quizSolvings: MutableList<QuizSolving> = mutableListOf()
) {
    enum class Provider {
        LOCAL, GOOGLE, KAKAO
    }

    companion object {
        fun createUser(loginEmail: String, encodedPassword: String, nickName: String): User {
            return User(
                loginEmail = loginEmail,
                password = encodedPassword,
                nickName = nickName,
                role = Role.USER,
                provider = Provider.LOCAL
            )
        }

        fun createOAuthUser(
            loginEmail: String,
            nickName: String,
            provider: Provider
        ): User {
            return User(
                loginEmail = loginEmail,
                password = "",
                nickName = nickName,
                role = Role.USER,
                provider = provider,
            )
        }
    }
}

