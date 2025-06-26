package com.project.quizcafe.domain.versioncontrol.repository

import com.project.quizcafe.domain.versioncontrol.entity.Vc
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface VcRepository : MongoRepository<Vc, String> {
    fun findAllByQuizBookId(quizBookId: Long): List<Vc>
    fun findByQuizBookIdAndVersion(quizBookId: Long, version: Long): Vc?
}