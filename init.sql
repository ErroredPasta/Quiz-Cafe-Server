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
    CONSTRAINT uk_user_login_email UNIQUE (login_email)
);

-- 이메일 인증 테이블 추가
CREATE TABLE email_verification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,  -- 이메일 주소
    verification_code VARCHAR(255) NOT NULL,  -- 인증 코드
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 생성 시간
    expires_at TIMESTAMP,  -- 만료 시간
    CONSTRAINT uk_email_verification_email UNIQUE (email)  -- 이메일은 유니크하게 관리
);

-- SELECT 쿼리로 데이터 확인
SELECT * FROM user;
SELECT * FROM email_verification;