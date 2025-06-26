package com.project.quizcafe.domain.util

import com.project.quizcafe.common.model.Role
import com.project.quizcafe.domain.quiz.dto.response.McqOptionResponse
import com.project.quizcafe.domain.quiz.dto.response.QuizResponse
import com.project.quizcafe.domain.quiz.entity.McqOption
import com.project.quizcafe.domain.quiz.entity.QuestionType
import com.project.quizcafe.domain.quiz.entity.Quiz
import com.project.quizcafe.domain.quizbook.entity.QuizBook
import com.project.quizcafe.domain.quizbook.entity.QuizBookBookmark
import com.project.quizcafe.domain.quizbook.entity.QuizLevel
import com.project.quizcafe.domain.quizbooksolving.entity.QuizBookSolving
import com.project.quizcafe.domain.quizsolving.entity.McqOptionSolving
import com.project.quizcafe.domain.quizsolving.entity.QuizSolving
import com.project.quizcafe.domain.user.entity.User
import com.project.quizcafe.domain.versioncontrol.dto.SavedQuizBook
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
    category: String = "default-category",
    createdBy: User? = null,
    level: QuizLevel = QuizLevel.EASY
) = QuizBook(
    id = id,
    version = version,
    category = category,
    title = "default-title",
    description = "default-description",
    createdBy = createdBy,
    createdAt = LocalDateTime.now(),
    updatedAt = LocalDateTime.now(),
    level = level
)

fun createUser(
    id: Long = 1L,
    loginEmail: String = "default@email.com",
    password: String = "default password",
    nickName: String = "DefaultUser"
) = User(
    id = id,
    loginEmail = loginEmail,
    password = password,
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

fun createQuizBookBookmark(
    id: Long = 1L,
    user: User = createUser(),
    quizBook: QuizBook,
) = QuizBookBookmark(
    id = id,
    user = user,
    quizBook = quizBook,
    createdAt = LocalDateTime.now()
)

fun createQuizResponse(
    id: Long = 1L,
    quizBookId: Long = 1L,
    questionType: QuestionType = QuestionType.SHORT_ANSWER,
    content: String = "default content",
    answer: String = "default answer",
    explanation: String? = null,
    mcqOption: List<McqOptionResponse>? = null
) = QuizResponse(
    id = id,
    quizBookId = quizBookId,
    questionType = questionType,
    content = content,
    answer = answer,
    explanation = explanation,
    mcqOption = mcqOption
)

fun createQuizSolving(
    id: Long = 1L,
    quizBookSolving: QuizBookSolving,
    quiz: Quiz,
    user: User,
    memo: String? = null,
    userAnswer: String? = null,
    isCorrect: Boolean = false,
    completedAt: LocalDateTime = LocalDateTime.now(),
) = QuizSolving(
    id = id,
    quizBookSolving = quizBookSolving,
    quiz = quiz,
    user = user,
    memo = memo,
    userAnswer = userAnswer,
    isCorrect = isCorrect,
    completedAt = completedAt,
)

fun createQuizBookSolving(
    id: Long = 1L,
    user: User,
    quizBook: QuizBook,
    version: Long = 1L,
    totalQuizzes: Int = 0,
    correctCount: Int = 0,
    completedAt: LocalDateTime = LocalDateTime.now(),
    solvingTimeSeconds: Long? = null,
) = QuizBookSolving(
    id = id,
    user = user,
    quizBook = quizBook,
    version = version,
    totalQuizzes = totalQuizzes,
    correctCount = correctCount,
    completedAt = completedAt,
    solvingTimeSeconds = solvingTimeSeconds
)

fun QuizBook.toSavedQuizBook(
    creatorNickName: String,
    quizzes: List<QuizResponse>
) = SavedQuizBook(
    category = category,
    title = title,
    level = level,
    description = description,
    createdBy = creatorNickName,
    quizzes = quizzes
)

fun createMcqOptionSolving(
    id: Long = 1L,
    quizSolving: QuizSolving,
    optionNumber: Int = 1,
    optionContent: String = "default optionContent",
    isCorrect: Boolean = false,
) = McqOptionSolving(
    id = id,
    quizSolving = quizSolving,
    optionNumber = optionNumber,
    optionContent = optionContent,
    isCorrect = isCorrect,
)