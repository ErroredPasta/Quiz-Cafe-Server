package com.project.quizcafe.quizbook.entity

enum class QuizCategory(val group: String, val categoryName: String) {

    // Computer Science
    NETWORK("Computer Science", "네트워크"),
    OPERATING_SYSTEM("Computer Science", "운영체제"),
    ALGORITHMS("Computer Science", "알고리즘"),
    DATABASE("Computer Science", "데이터베이스"),
    SOFTWARE_ENGINEERING("Computer Science", "소프트웨어 공학"),
    ARTIFICIAL_INTELLIGENCE("Computer Science", "인공지능"),
    SECURITY("Computer Science", "보안"),

    // Framework
    ANDROID("Framework", "안드로이드"),
    SPRING("Framework", "스프링"),

    // Programming Language
    KOTLIN("Programming Language", "코틀린"),
    JAVA("Programming Language", "자바"),
    PYTHON("Programming Language", "파이썬"),
    C("Programming Language", "C언어");

}