-- 기존 데이터베이스와 테이블 생성
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
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,  -- DATETIME으로 변경
    CONSTRAINT uk_user_login_email UNIQUE (login_email)
);

-- 이메일 인증 테이블 추가
CREATE TABLE email_verification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,  -- 이메일 주소
    verification_code VARCHAR(255) NOT NULL,  -- 인증 코드
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,  -- DATETIME으로 변경
    expires_at TIMESTAMP,  -- 만료 시간
    CONSTRAINT uk_email_verification_email UNIQUE (email)  -- 이메일은 유니크하게 관리
);

-- 문제집 테이블 (quiz_book)
CREATE TABLE quiz_book (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category VARCHAR(255) NOT NULL,  -- 카테고리
    title VARCHAR(255) NOT NULL,  -- 제목
    description TEXT,  -- 설명
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,  -- 생성일
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL  -- 수정일
);

-- 문제 테이블 (problem)
CREATE TABLE problem (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quiz_book_id BIGINT NOT NULL,  -- 문제집 ID (외래 키)
    question_type ENUM('MCQ', 'SHORT_ANSWER', 'OX') NOT NULL,  -- 문제 종류 (객관식, 단답식, OX 문제)
    content TEXT NOT NULL,  -- 문제 내용
    answer TEXT NOT NULL,  -- 문제 답
    explanation TEXT,  -- 문제 해설
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,  -- 생성일
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,  -- 수정일
    CONSTRAINT fk_quiz_book FOREIGN KEY (quiz_book_id) REFERENCES quiz_book(id)  -- 문제집과의 관계
);

-- 객관식 문제 선택지 테이블 (mcq_option)
CREATE TABLE mcq_option (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    problem_id BIGINT NOT NULL,  -- 문제 ID (외래 키)
    option_number INT NOT NULL,  -- 선택지 번호 (1, 2, 3, 4 등)
    option_content TEXT NOT NULL,  -- 선택지 내용
    is_correct BOOLEAN NOT NULL DEFAULT FALSE,  -- 정답 여부 추가
    CONSTRAINT fk_problem FOREIGN KEY (problem_id) REFERENCES problem(id)  -- 문제와의 관계
);

-- 예시 데이터 삽입 (문제집, 문제, 객관식 선택지)
-- 문제집
INSERT INTO quiz_book (category, title, description)
VALUES ('Math', 'Math Quiz 1', 'This is a math quiz for beginners');

select * from quiz_book;

-- 문제
INSERT INTO problem (quiz_book_id, question_type, content, answer, explanation)
VALUES
(1, 'MCQ', 'What is 2 + 2?', '4', '2 + 2는 4입니다.');

select * from problem;

-- 객관식 문제 선택지
INSERT INTO mcq_option (problem_id, option_number, option_content, is_correct)
VALUES
    (1, 1, '1', FALSE),
    (1, 2, '2', FALSE),
    (1, 3, '3', FALSE),
    (1, 4, '4', TRUE);

select * from mcq_option;
