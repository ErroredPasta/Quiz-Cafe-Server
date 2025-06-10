package com.project.quizcafe.domain.quizbook.extentions

import com.project.quizcafe.domain.quizbook.dto.response.GetAllCategoriesResponse
import com.project.quizcafe.domain.quizbook.entity.QuizCategory

fun QuizCategory.toGetAllCategoriesResponse(): GetAllCategoriesResponse {
    return GetAllCategoriesResponse(
        category = this.name,
        name = this.categoryName,
        group = this.group
    )
}
