package com.project.quizcafe.domain.versioncontrol.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes

@Document(collection = "version")
@CompoundIndexes(
    CompoundIndex(name = "quizBookId_version_idx", def = "{'quizBookId': 1, 'version': 1}", unique = true)
)
data class Vc(
    @Id
    val id: String? = null,  // MongoDB ObjectId 문자열

    val quizBookId: Long,

    val version: Long,

    val quizzesValue: String
)