package com.project.quizcafe.domain.quiz.entity

import jakarta.persistence.*

@Entity
@Table(name = "mcq_option")
class McqOption(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    val quiz: Quiz,

    @Column(nullable = false)
    var optionNumber: Int, // 선택지 번호

    @Column(nullable = false)
    var optionContent: String, // 선택지 내용

    @Column(nullable = false)
    var isCorrect: Boolean // 정답 여부
)