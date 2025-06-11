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
select * from quiz_book_solving;

INSERT INTO user (login_email, password, nick_name, role)
VALUES
  ('test@naver.com', 'test1234', 'test', 'USER');

INSERT INTO quiz_book (version, category, title, level, description, created_by)
VALUES
  (1, '네트워크', '네트워크 기초 문제집', 'EASY', '네트워크 기본 개념을 익히는 쉬운 문제집입니다.', 1),
  (1, '운영체제', '운영체제 중급 문제집', 'MEDIUM', '운영체제 핵심 이론을 다루는 중급 문제집입니다.', 1),
  (1, '알고리즘', '알고리즘 심화 문제집', 'HARD', '복잡한 알고리즘 문제들을 포함하는 어려운 문제집입니다.', 1),
  (1, '스프링', '스프링 입문 문제집', 'EASY', '스프링 프레임워크 기본을 다루는 쉬운 문제집입니다.', 1),
  (1, '코틀린', '코틀린 기초 문제집', 'EASY', '코틀린 문법과 기초 문제를 포함합니다.', 1);

-- 6번 문제집 (네트워크 기초 문제집)
INSERT INTO quiz (quiz_book_id, version, question_type, content, answer, explanation)
VALUES (1, 1, 'MCQ', 'OSI 7계층 중 물리 계층의 역할은?', '전기적 신호 전송', '물리 계층은 데이터를 전기적 신호로 변환하여 전송하는 역할을 합니다.');
SET @quiz_id_6 = LAST_INSERT_ID();

INSERT INTO mcq_option (quiz_id, option_number, option_content, is_correct) VALUES
(@quiz_id_6, 1, '데이터 암호화', false),
(@quiz_id_6, 2, '라우팅 결정', false),
(@quiz_id_6, 3, '전기적 신호 전송', true),
(@quiz_id_6, 4, '세션 관리', false);

-- 7번 문제집 (운영체제 중급 문제집)
INSERT INTO quiz (quiz_book_id, version, question_type, content, answer, explanation)
VALUES (2, 1, 'MCQ', '운영체제에서 프로세스와 스레드의 차이는?', '스레드는 프로세스 내의 작업 단위이다.', '스레드는 프로세스 내에서 실행되는 작업 단위로, 메모리 공간을 공유합니다.');
SET @quiz_id_7 = LAST_INSERT_ID();

INSERT INTO mcq_option (quiz_id, option_number, option_content, is_correct) VALUES
(@quiz_id_7, 1, '프로세스는 스레드보다 빠르다.', false),
(@quiz_id_7, 2, '스레드는 독립적인 메모리 공간을 가진다.', false),
(@quiz_id_7, 3, '스레드는 프로세스 내의 작업 단위이다.', true),
(@quiz_id_7, 4, '프로세스는 스레드보다 가볍다.', false);

-- 8번 문제집 (알고리즘 심화 문제집)
INSERT INTO quiz (quiz_book_id, version, question_type, content, answer, explanation)
VALUES (3, 1, 'MCQ', '다익스트라 알고리즘의 특징은?', '음수 가중치는 처리할 수 없다.', '다익스트라는 가중치가 음수가 아닌 경우에만 동작합니다.');
SET @quiz_id_8 = LAST_INSERT_ID();

INSERT INTO mcq_option (quiz_id, option_number, option_content, is_correct) VALUES
(@quiz_id_8, 1, '음수 가중치를 처리할 수 있다.', false),
(@quiz_id_8, 2, '모든 경우의 수를 탐색한다.', false),
(@quiz_id_8, 3, '음수 가중치는 처리할 수 없다.', true),
(@quiz_id_8, 4, '최소 신장 트리를 생성한다.', false);

-- 9번 문제집 (스프링 입문 문제집)
INSERT INTO quiz (quiz_book_id, version, question_type, content, answer, explanation)
VALUES (4, 1, 'MCQ', '스프링에서 의존성 주입(Dependency Injection)의 장점은?', '결합도를 낮출 수 있다.', '의존성 주입을 통해 객체 간 결합도를 줄이고 유연성을 높일 수 있습니다.');
SET @quiz_id_9 = LAST_INSERT_ID();

INSERT INTO mcq_option (quiz_id, option_number, option_content, is_correct) VALUES
(@quiz_id_9, 1, '코드를 길게 만든다.', false),
(@quiz_id_9, 2, '결합도를 낮출 수 있다.', true),
(@quiz_id_9, 3, '속도를 낮춘다.', false),
(@quiz_id_9, 4, '객체 생성을 제한한다.', false);

-- 10번 문제집 (코틀린 기초 문제집)
INSERT INTO quiz (quiz_book_id, version, question_type, content, answer, explanation)
VALUES (5, 1, 'MCQ', 'Kotlin에서 null을 안전하게 처리하는 방법은?', 'null-safe 연산자(?.)를 사용한다.', 'Kotlin은 null 안전성을 위해 ?. 등의 null-safe 연산자를 제공합니다.');
SET @quiz_id_10 = LAST_INSERT_ID();

INSERT INTO mcq_option (quiz_id, option_number, option_content, is_correct) VALUES
(@quiz_id_10, 1, '널을 그대로 사용한다.', false),
(@quiz_id_10, 2, 'null-safe 연산자(?.)를 사용한다.', true),
(@quiz_id_10, 3, 'null을 무조건 제거한다.', false),
(@quiz_id_10, 4, 'null을 허용하지 않는다.', false);

