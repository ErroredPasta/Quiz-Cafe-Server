-- 기존 데이터베이스 삭제 및 생성
DROP DATABASE IF EXISTS Quiz_Cafe_db;
CREATE DATABASE Quiz_Cafe_db;
USE Quiz_Cafe_db;

-- user 테이블
CREATE TABLE user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    login_email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    nick_name VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL DEFAULT 'ROLE_USER',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT uk_user_login_email UNIQUE (login_email)
);

-- 이메일 인증 테이블
CREATE TABLE email_verification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    verification_code VARCHAR(255) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    expires_at TIMESTAMP,
    CONSTRAINT uk_email_verification_email UNIQUE (email)
);

CREATE TABLE quiz_book (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    version BIGINT NOT NULL,
    category VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    level ENUM('EASY', 'MEDIUM', 'HARD') NOT NULL,
    description TEXT,
    created_by BIGINT,  -- NULL 가능하도록 설정
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_quiz_book_user FOREIGN KEY (created_by) REFERENCES user(id)
);

-- 퀴즈 테이블 (quiz)
CREATE TABLE quiz (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quiz_book_id BIGINT NOT NULL,
    version BIGINT NOT NULL,
    question_type ENUM('MCQ', 'SHORT_ANSWER', 'OX') NOT NULL,
    content TEXT NOT NULL,
    answer TEXT NOT NULL,
    explanation TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_quiz_book FOREIGN KEY (quiz_book_id) REFERENCES quiz_book(id)
);

-- 객관식 선택지 테이블 (mcq_option)
CREATE TABLE mcq_option (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quiz_id BIGINT NOT NULL,
    option_number INT NOT NULL,
    option_content TEXT NOT NULL,
    is_correct BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_quiz FOREIGN KEY (quiz_id) REFERENCES quiz(id)
);

CREATE TABLE quiz_book_bookmark (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    quiz_book_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_qbb_user FOREIGN KEY (user_id) REFERENCES user(id),
    CONSTRAINT fk_qbb_quiz_book FOREIGN KEY (quiz_book_id) REFERENCES quiz_book(id),
    CONSTRAINT unique_user_quiz_book UNIQUE (user_id, quiz_book_id)  -- 중복 북마크 방지
);

CREATE TABLE quiz_book_solving (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    quiz_book_id BIGINT NOT NULL,
    version BIGINT NOT NULL,
    level ENUM('EASY', 'MEDIUM', 'HARD') NOT NULL,
    category VARCHAR(255) NOT NULL, -- 카테고리
    title VARCHAR(255) NOT NULL,    -- 제목
    description TEXT,               -- 설명
    total_quizzes INT NOT NULL,     -- 전체 문제 수
    correct_count INT NOT NULL,     -- 맞춘 문제 수
    completed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 완료 시간
    CONSTRAINT fk_solving_user FOREIGN KEY (user_id) REFERENCES user(id),
    CONSTRAINT fk_solving_quiz_book FOREIGN KEY (quiz_book_id) REFERENCES quiz_book(id)
);

CREATE TABLE quiz_solving (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quiz_book_solving_id BIGINT NOT NULL,
    quiz_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    version BIGINT NOT NULL,
    question_type ENUM('MCQ', 'SHORT_ANSWER', 'OX') NOT NULL,
    content TEXT NOT NULL,
    answer TEXT NOT NULL,
    explanation TEXT,
    memo TEXT,
    user_answer TEXT, -- 사용자가 작성한 정답
    is_correct BOOLEAN NOT NULL, -- 정답 여부 (true = 맞음, false = 틀림)
    completed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_qs_quiz_book_solving FOREIGN KEY (quiz_book_solving_id) REFERENCES quiz_book_solving(id),
    CONSTRAINT fk_qs_quiz FOREIGN KEY (quiz_id) REFERENCES quiz(id),
    CONSTRAINT fk_qs_user FOREIGN KEY (user_id) REFERENCES user(id)
);

CREATE TABLE mcq_option_solving (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quiz_solving_id BIGINT NOT NULL,
    option_number INT NOT NULL,
    option_content TEXT NOT NULL,
    is_correct BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_mos_quiz_solving FOREIGN KEY (quiz_solving_id) REFERENCES quiz_solving(id)
);

CREATE TABLE version (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quiz_book_id BIGINT NOT NULL,
    version BIGINT NOT NULL,
    value TEXT
);

-- 데이터 확인
SELECT * FROM quiz_book;
SELECT * FROM quiz;
SELECT * FROM mcq_option;
select * from user;

