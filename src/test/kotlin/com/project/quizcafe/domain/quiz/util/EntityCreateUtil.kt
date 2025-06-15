package com.project.quizcafe.domain.quiz.util

import com.project.quizcafe.common.model.Role
import com.project.quizcafe.domain.quiz.entity.McqOption
import com.project.quizcafe.domain.quiz.entity.QuestionType
import com.project.quizcafe.domain.quiz.entity.Quiz
import com.project.quizcafe.domain.quizbook.entity.QuizBook
import com.project.quizcafe.domain.quizbook.entity.QuizLevel
import com.project.quizcafe.domain.user.entity.User
import java.time.LocalDateTime

fun createQuiz(
    id: Long = 1L,
    quizBook: QuizBook,
    questionType: QuestionType = QuestionType.MCQ,
    content: String = "content",
    answer: String = "answer",
    explanation: String = "explanation"
) = Quiz(
    id = id,
    quizBook = quizBook,
    questionType = questionType,
    content = content,
    answer = answer,
    explanation = explanation,
    createdAt = LocalDateTime.now(),
    updatedAt = LocalDateTime.now()
)

fun createQuizBook(
    id: Long = 1L,
    version: Long = 1L,
    createdBy: User? = null,
    level: QuizLevel = QuizLevel.EASY
) = QuizBook(
    id = id,
    version = version,
    category = "default-category",
    title = "default-title",
    description = "default-description",
    createdBy = createdBy,
    createdAt = LocalDateTime.now(),
    updatedAt = LocalDateTime.now(),
    level = level
)

fun createUser(
    id: Long = 1L,
    nickName: String = "DefaultUser"
) = User(
    id = id,
    loginEmail = "$nickName@example.com",
    password = "password",
    nickName = nickName,
    role = Role.USER,
    provider = User.Provider.LOCAL,
    createdAt = LocalDateTime.now()
)

fun createMcqOption(
    id: Long = 1L,
    quiz: Quiz,
    optionNumber: Int = 1,
    optionContent: String = "default mcq option content",
    isCorrect: Boolean = false
) = McqOption(
    id = id,
    quiz = quiz,
    optionNumber = optionNumber,
    optionContent = optionContent,
    isCorrect = isCorrect
)