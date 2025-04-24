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
    category VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
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

INSERT INTO user (login_email, password, nick_name)
VALUES ('test@example.com', 'test1234', '테스트유저');

-- 예시 데이터 삽입
-- 문제집 삽입
INSERT INTO quiz_book (category, title, description, created_by)
VALUES ('Math', 'Math Quiz 1', 'This is a math quiz for beginners', 1);

INSERT INTO quiz_book (category, title, description, created_by)
VALUES ('코틀린', '코틀린1', '코틀린1', 2)

-- 자바 관련 데이터 추가
INSERT INTO quiz_book (category, title, description, created_by)
VALUES ('자바', '자바2', '자바 관련 퀴즈', 2);

INSERT INTO quiz_book (category, title, description, created_by)
VALUES ('자바', '자바3', '자바 관련 퀴즈', 2);

-- 코틀린 관련 데이터 추가
INSERT INTO quiz_book (category, title, description, created_by)
VALUES ('코틀린', '코틀린2', '코틀린 관련 퀴즈', 2);

INSERT INTO quiz_book (category, title, description, created_by)
VALUES ('코틀린', '코틀린3', '코틀린 관련 퀴즈', 2);

-- 퀴즈 삽입
INSERT INTO quiz (quiz_book_id, question_type, content, answer, explanation)
VALUES
(1, 'MCQ', 'What is 2 + 2?', '4', '2 + 2는 4입니다.');

-- 객관식 선택지 삽입
INSERT INTO mcq_option (quiz_id, option_number, option_content, is_correct)
VALUES
    (1, 1, '1', FALSE),
    (1, 2, '2', FALSE),
    (1, 3, '3', FALSE),
    (1, 4, '4', TRUE);

-- 데이터 확인
SELECT * FROM quiz_book;
SELECT * FROM quiz;
SELECT * FROM mcq_option;
select * from user;