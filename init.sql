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

CREATE TABLE vc (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quiz_book_id BIGINT NOT NULL,
    version BIGINT NOT NULL,
    quizzes_value TEXT NOT NULL
);

-- 데이터 확인
SELECT * FROM quiz_book;
SELECT * FROM quiz;
SELECT * FROM mcq_option;
select * from user;
select * from quiz_book_solving;
select * from quiz_solving;
select * from vc;

-- 테스트 유저 추가
INSERT INTO user (login_email, password, nick_name, role)
VALUES
  ('test@naver.com', 'test1234', 'test user', 'USER');

-- 알고리즘 문제지 데이터 추가
INSERT INTO quiz_book (version, category, title, level, description, created_by)
VALUES (1, '알고리즘', '알고리즘 객관식 30제', 'HARD', '30문항으로 구성된 알고리즘 객관식 문제집입니다.', 1);

SET @quiz_book_id = LAST_INSERT_ID();

INSERT INTO quiz (quiz_book_id, question_type, content, answer, explanation)
VALUES
(@quiz_book_id, 'MCQ',
 'n개의 정점, m개의 간선을 가진 방향 그래프에서, 한 정점에서 모든 정점까지의 최단 거리를 구하는데 가장 적합한 알고리즘은?',
 '3',
 '다익스트라 알고리즘은 하나의 시작 정점에서 다른 모든 정점까지의 최단 경로를 찾을 때 가장 널리 사용되는 알고리즘입니다. 시간 복잡도는 우선순위 큐(힙)를 이용할 때 O((V+E)logV)입니다. 다만, 간선의 가중치가 모두 0 이상(음수가 없어야 함)일 때만 안전하게 사용할 수 있습니다. 2번 플로이드-워셜은 모든 정점 쌍 간의 최단 경로(출발점이 여러 개)를 한 번에 구할 때 사용하며, O(n^3)이라 한 출발점만 필요할 때는 비효율적입니다. 크루스칼과 프림은 최소 신장 트리(MST), DFS는 모든 경로를 일일이 탐색하기 때문에 비효율적입니다. 정리하면, 단일 출발점 → 모든 정점까지의 최단 경로는 다익스트라, 모든 쌍은 플로이드-워셜, 특정 목적은 문제 상황에 따라 선택합니다.'
);

SET @quiz_id = LAST_INSERT_ID();

INSERT INTO mcq_option (quiz_id, option_number, option_content, is_correct) VALUES
(@quiz_id, 1, '크루스칼', false),
(@quiz_id, 2, '플로이드-워셜', false),
(@quiz_id, 3, '다익스트라', true),
(@quiz_id, 4, '프림', false),
(@quiz_id, 5, 'DFS', false);