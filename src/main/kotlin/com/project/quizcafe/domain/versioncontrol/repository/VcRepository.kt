package com.project.quizcafe.domain.versioncontrol.repository

import com.project.quizcafe.domain.versioncontrol.entity.Vc
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface VcRepository : JpaRepository<Vc, Long> {
    fun findAllByQuizBookId(quizBookId: Long): List<Vc>
    fun findByQuizBookIdAndVersion(quizBookId: Long, version: Long): Vc?
}