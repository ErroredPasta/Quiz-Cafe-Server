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

-- 이메일 인증 테이블 (자식 테이블 없음)
CREATE TABLE email_verification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    verification_code VARCHAR(255) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    expires_at TIMESTAMP,
    CONSTRAINT uk_email_verification_email UNIQUE (email)
);

-- quiz_book 테이블
CREATE TABLE quiz_book (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    version BIGINT NOT NULL,
    category VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    level ENUM('EASY', 'MEDIUM', 'HARD') NOT NULL,
    description TEXT,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_quiz_book_user FOREIGN KEY (created_by) REFERENCES user(id) ON DELETE CASCADE
);

-- quiz 테이블
CREATE TABLE quiz (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quiz_book_id BIGINT NOT NULL,
    question_type ENUM('MCQ', 'SHORT_ANSWER', 'OX') NOT NULL,
    content TEXT NOT NULL,
    answer TEXT NOT NULL,
    explanation TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_quiz_book FOREIGN KEY (quiz_book_id) REFERENCES quiz_book(id) ON DELETE CASCADE
);

-- mcq_option 테이블
CREATE TABLE mcq_option (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quiz_id BIGINT NOT NULL,
    option_number INT NOT NULL,
    option_content TEXT NOT NULL,
    is_correct BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_quiz FOREIGN KEY (quiz_id) REFERENCES quiz(id) ON DELETE CASCADE
);

-- quiz_book_bookmark 테이블
CREATE TABLE quiz_book_bookmark (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    quiz_book_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_qbb_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    CONSTRAINT fk_qbb_quiz_book FOREIGN KEY (quiz_book_id) REFERENCES quiz_book(id) ON DELETE CASCADE,
    CONSTRAINT unique_user_quiz_book UNIQUE (user_id, quiz_book_id)
);

-- quiz_book_solving 테이블
CREATE TABLE quiz_book_solving (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    quiz_book_id BIGINT NOT NULL,
    version BIGINT NOT NULL,
    total_quizzes INT NOT NULL,
    correct_count INT NOT NULL,
    completed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_solving_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    CONSTRAINT fk_solving_quiz_book FOREIGN KEY (quiz_book_id) REFERENCES quiz_book(id) ON DELETE CASCADE
);

-- quiz_solving 테이블
CREATE TABLE quiz_solving (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quiz_book_solving_id BIGINT NOT NULL,
    quiz_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    memo TEXT,
    user_answer TEXT,
    is_correct BOOLEAN NOT NULL,
    completed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_qs_quiz_book_solving FOREIGN KEY (quiz_book_solving_id) REFERENCES quiz_book_solving(id) ON DELETE CASCADE,
    CONSTRAINT fk_qs_quiz FOREIGN KEY (quiz_id) REFERENCES quiz(id) ON DELETE CASCADE,
    CONSTRAINT fk_qs_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);

-- mcq_option_solving 테이블
CREATE TABLE mcq_option_solving (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quiz_solving_id BIGINT NOT NULL,
    option_number INT NOT NULL,
    option_content TEXT NOT NULL,
    is_correct BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_mos_quiz_solving FOREIGN KEY (quiz_solving_id) REFERENCES quiz_solving(id) ON DELETE CASCADE
);

-- CREATE TABLE vc (
--     id BIGINT AUTO_INCREMENT PRIMARY KEY,
--     quiz_book_id BIGINT NOT NULL,
--     version BIGINT NOT NULL,
--     quizzes_value TEXT NOT NULL
-- );

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

-- 문제 번호 1
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

-- 문제 번호 2
INSERT INTO quiz (quiz_book_id, question_type, content, answer, explanation)
VALUES
(@quiz_book_id, 'MCQ',
 'n개의 정점과 m개의 간선을 가진 무방향 그래프에서, 모든 정점을 연결하는 간선의 가중치 합이 최소가 되도록 하는 간선 집합을 찾는 문제는 무엇인가?',
 '2',
 '최소 신장 트리(MST, Minimum Spanning Tree)란 모든 정점을 포함하면서, 사이클이 없고, 간선의 가중치 합이 가장 작은 트리를 의미합니다. 대표적인 알고리즘으로는 크루스칼(Kruskal)과 프림(Prim)이 있습니다. 이 문제는 각 정점을 한 번씩만 방문하면서 전체 네트워크를 최소 비용으로 연결해야 할 때 등장합니다. 1번 ''최단 경로''는 한 쌍의 정점 사이의 최단 거리 문제이며, 3번 위상정렬은 방향 그래프의 순서 결정, 4번 이분 매칭은 두 그룹 사이의 최대 매칭, 5번 SCC는 강한 연결 요소 분할에 쓰입니다. 따라서 전체 연결+가중치 최소 조건엔 반드시 ''최소 신장 트리''를 떠올려야 합니다.'
);

SET @quiz_id = LAST_INSERT_ID();

INSERT INTO mcq_option (quiz_id, option_number, option_content, is_correct) VALUES
(@quiz_id, 1, '최단 경로', false),
(@quiz_id, 2, '최소 신장 트리', true),
(@quiz_id, 3, '위상 정렬', false),
(@quiz_id, 4, '이분 매칭', false),
(@quiz_id, 5, 'SCC', false);

-- 문제 번호 3
INSERT INTO quiz (quiz_book_id, question_type, content, answer, explanation)
VALUES
(@quiz_book_id, 'MCQ',
 '길이가 n인 수열에서, LIS(Longest Increasing Subsequence) 길이를 가장 효율적으로 구하는 알고리즘의 시간 복잡도는?',
 '2',
 'LIS(최장 증가 부분 수열)는 수열에서 "항상 증가하는 부분"의 최대 길이를 구하는 문제입니다. 전통적인 DP만 사용하면 모든 원소마다 앞을 확인하므로 O(n^2)이지만, 이분 탐색을 활용하면 O(n log n)에 해결할 수 있습니다. 이 방법은 실제로 답이 되는 수열을 만드는 게 아니라, 길이만 구하는 데 가장 효율적입니다. 1번 O(n)은 불가능, 3번 O(n^2)는 단순 DP, 4,5번은 말이 안 됩니다. 코딩테스트에서 LIS가 등장하면 O(n log n) 풀이(이분 탐색)까지 익혀두는 게 필수입니다.');

SET @quiz_id = LAST_INSERT_ID();

INSERT INTO mcq_option (quiz_id, option_number, option_content, is_correct) VALUES
(@quiz_id, 1, 'O(n)', false),
(@quiz_id, 2, 'O(n log n)', true),
(@quiz_id, 3, 'O(n^2)', false),
(@quiz_id, 4, 'O(log n)', false),
(@quiz_id, 5, 'O(n^3)', false);

-- 문제 번호 4
INSERT INTO quiz (quiz_book_id, question_type, content, answer, explanation)
VALUES
(@quiz_book_id, 'MCQ',
 '임의의 양방향 그래프에서 한 정점에서 다른 정점으로의 "최단 경로의 개수"를 구할 때 사용하는 탐색법은?',
 '2',
 'BFS(너비 우선 탐색)는 각 정점까지의 최단 거리를 계층적으로 구할 때 사용합니다. 최단 경로의 개수를 세기 위해, 각 정점을 처음 방문할 때만 카운트하는 것이 아니라, 동일한 최단 거리로 여러 번 도달할 수 있음을 체크해 누적합니다. DFS는 깊게 들어가기 때문에 거리 계산이 비효율적이고, 위상정렬이나 플로이드-워셜은 이 목적에 적합하지 않습니다. BFS는 시작점에서 거리 1씩 증가하며 모든 경로를 탐색하므로, 여러 최단 경로를 정확하게 카운트할 수 있습니다. 대표 예시는 "최단 거리의 경우의 수", "최소 횟수로 도달하는 경로의 개수" 등에서 자주 출제됩니다.');

SET @quiz_id = LAST_INSERT_ID();

INSERT INTO mcq_option (quiz_id, option_number, option_content, is_correct) VALUES
(@quiz_id, 1, 'DFS', false),
(@quiz_id, 2, 'BFS', true),
(@quiz_id, 3, '위상정렬', false),
(@quiz_id, 4, '이분탐색', false),
(@quiz_id, 5, '플로이드-워셜', false);

-- 문제 번호 5
INSERT INTO quiz (quiz_book_id, question_type, content, answer, explanation)
VALUES
(@quiz_book_id, 'MCQ',
 '주어진 문자열 S에서 서로 다른 부분 문자열의 개수를 가장 효율적으로 구하는 자료구조는?',
 '3',
 '문자열의 모든 서로 다른 부분 문자열의 개수를 구하려면, 문자열의 모든 접미사를 정렬한 "Suffix Array"를 사용합니다. 이와 함께 각 접미사간의 공통 접두사(LCP, Longest Common Prefix)를 구하면, 중복된 부분 문자열을 세지 않으면서 전체 개수를 빠르게 구할 수 있습니다. 세그먼트 트리, 힙, 큐, 우선순위 큐 등은 이 문제의 해법과 무관합니다. 접미사 배열은 O(n log n)에 구축 가능하고, LCP를 통해 중복을 피하면서 부분 문자열 개수를 O(n)에 집계할 수 있습니다. 이 방식은 문자열 알고리즘에서 "모든 부분 문자열의 집합"이 필요한 문제에서 매우 자주 활용됩니다.');

SET @quiz_id = LAST_INSERT_ID();

INSERT INTO mcq_option (quiz_id, option_number, option_content, is_correct) VALUES
(@quiz_id, 1, '세그먼트 트리', false),
(@quiz_id, 2, '힙', false),
(@quiz_id, 3, 'Suffix Array', true),
(@quiz_id, 4, '큐', false),
(@quiz_id, 5, '우선순위 큐', false);

-- 문제 번호 6
INSERT INTO quiz (quiz_book_id, question_type, content, answer, explanation)
VALUES
(@quiz_book_id, 'MCQ',
 'BFS(너비 우선 탐색)를 사용할 때 주로 필요한 자료구조는?',
 '1',
 'BFS는 탐색 순서를 보장하기 위해 큐(Queue)를 사용합니다. 큐에 넣고 빼는 과정을 반복하며 탐색합니다.');

SET @quiz_id = LAST_INSERT_ID();

INSERT INTO mcq_option (quiz_id, option_number, option_content, is_correct) VALUES
(@quiz_id, 1, '큐', true),
(@quiz_id, 2, '스택', false),
(@quiz_id, 3, '우선순위 큐', false),
(@quiz_id, 4, '트리', false),
(@quiz_id, 5, '힙', false);

-- 문제 번호 7
INSERT INTO quiz (quiz_book_id, question_type, content, answer, explanation)
VALUES
(@quiz_book_id, 'MCQ',
 '다음 중 DFS(깊이 우선 탐색)의 특징으로 옳은 것은?',
 '2',
 'DFS는 깊이 있게 한 경로를 끝까지 탐색하는 방식으로, 모든 경로를 탐색할 수 있습니다.');

SET @quiz_id = LAST_INSERT_ID();

INSERT INTO mcq_option (quiz_id, option_number, option_content, is_correct) VALUES
(@quiz_id, 1, '최단 거리를 보장한다', false),
(@quiz_id, 2, '모든 경로를 탐색할 수 있다', true),
(@quiz_id, 3, '항상 사이클을 찾는다', false),
(@quiz_id, 4, '반드시 이분 그래프에서만 쓸 수 있다', false),
(@quiz_id, 5, '간선 가중치가 필요하다', false);

-- 문제 번호 8
INSERT INTO quiz (quiz_book_id, question_type, content, answer, explanation)
VALUES
(@quiz_book_id, 'MCQ',
 '트리 자료구조의 기본적인 성질로 올바른 것은?',
 '3',
 '트리는 "사이클이 없고, 루트를 제외한 모든 노드는 하나의 부모만 가집니다."');

SET @quiz_id = LAST_INSERT_ID();

INSERT INTO mcq_option (quiz_id, option_number, option_content, is_correct) VALUES
(@quiz_id, 1, '사이클이 존재한다', false),
(@quiz_id, 2, '모든 노드가 반드시 두 개의 자식 노드를 가진다', false),
(@quiz_id, 3, '모든 노드는 하나의 부모를 가진다(루트 제외)', true),
(@quiz_id, 4, '자기 자신으로 가는 간선이 존재한다', false),
(@quiz_id, 5, '항상 방향이 없다', false);

-- 문제 번호 9
INSERT INTO quiz (quiz_book_id, question_type, content, answer, explanation)
VALUES
(@quiz_book_id, 'MCQ',
 '연결 리스트(Linked List)의 특징은?',
 '2',
 '연결 리스트는 임의 위치 접근은 느리지만, 삽입/삭제가 빠릅니다.');

SET @quiz_id = LAST_INSERT_ID();

INSERT INTO mcq_option (quiz_id, option_number, option_content, is_correct) VALUES
(@quiz_id, 1, '임의 위치 원소 접근이 O(1)', false),
(@quiz_id, 2, '삽입/삭제가 빠르다', true),
(@quiz_id, 3, '항상 정렬되어 있다', false),
(@quiz_id, 4, '배열보다 공간 효율이 높다', false),
(@quiz_id, 5, '스택을 구현할 수 없다', false);

-- 문제 번호 10
INSERT INTO quiz (quiz_book_id, question_type, content, answer, explanation)
VALUES
(@quiz_book_id, 'MCQ',
 '스택(Stack)을 가장 잘 설명하는 것은?',
 '2',
 '스택은 LIFO(Last-In, First-Out), 마지막에 들어간 데이터가 가장 먼저 나옵니다.');

SET @quiz_id = LAST_INSERT_ID();

INSERT INTO mcq_option (quiz_id, option_number, option_content, is_correct) VALUES
(@quiz_id, 1, 'FIFO', false),
(@quiz_id, 2, 'LIFO', true),
(@quiz_id, 3, '임의 접근', false),
(@quiz_id, 4, '트리', false),
(@quiz_id, 5, '해시', false);

-- 문제 번호 11
INSERT INTO quiz (quiz_book_id, question_type, content, answer, explanation)
VALUES
(@quiz_book_id, 'MCQ',
 '다음 중 정렬 알고리즘에 해당하지 않는 것은?',
 '4',
 '크루스칼은 최소 신장 트리(MST) 알고리즘입니다. 나머지는 모두 정렬 알고리즘입니다.');

SET @quiz_id = LAST_INSERT_ID();

INSERT INTO mcq_option (quiz_id, option_number, option_content, is_correct) VALUES
(@quiz_id, 1, '버블 정렬', false),
(@quiz_id, 2, '퀵 정렬', false),
(@quiz_id, 3, '병합 정렬', false),
(@quiz_id, 4, '크루스칼', true),
(@quiz_id, 5, '삽입 정렬', false);

-- 문제 번호 12
INSERT INTO quiz (quiz_book_id, question_type, content, answer, explanation)
VALUES
(@quiz_book_id, 'MCQ',
 '해시 테이블(Hash Table)의 평균 시간 복잡도는?',
 '1',
 '해시 테이블의 삽입/탐색/삭제는 평균적으로 O(1)에 동작합니다.');

SET @quiz_id = LAST_INSERT_ID();

INSERT INTO mcq_option (quiz_id, option_number, option_content, is_correct) VALUES
(@quiz_id, 1, 'O(1)', true),
(@quiz_id, 2, 'O(n)', false),
(@quiz_id, 3, 'O(log n)', false),
(@quiz_id, 4, 'O(n log n)', false),
(@quiz_id, 5, 'O(n^2)', false);

-- 문제 번호 13
INSERT INTO quiz (quiz_book_id, question_type, content, answer, explanation)
VALUES
(@quiz_book_id, 'MCQ',
 '2차원 배열에서 상하좌우 네 방향을 모두 탐색하고 싶을 때 사용하는 기법은?',
 '2',
 '델타 배열을 사용하면 상하좌우(혹은 8방향) 인접 칸을 쉽게 탐색할 수 있습니다.');

SET @quiz_id = LAST_INSERT_ID();

INSERT INTO mcq_option (quiz_id, option_number, option_content, is_correct) VALUES
(@quiz_id, 1, '슬라이딩 윈도우', false),
(@quiz_id, 2, '델타 배열', true),
(@quiz_id, 3, '그리디', false),
(@quiz_id, 4, '이분 탐색', false),
(@quiz_id, 5, 'DP', false);

-- 문제 번호 14
INSERT INTO quiz (quiz_book_id, question_type, content, answer, explanation)
VALUES
(@quiz_book_id, 'MCQ',
 '그래프에서 "사이클이 없는 방향 그래프"를 부르는 용어는?',
 '2',
 'DAG(Directed Acyclic Graph)는 방향성이 있고, 사이클이 없는 그래프입니다.');

SET @quiz_id = LAST_INSERT_ID();

INSERT INTO mcq_option (quiz_id, option_number, option_content, is_correct) VALUES
(@quiz_id, 1, '트리', false),
(@quiz_id, 2, 'DAG', true),
(@quiz_id, 3, 'MST', false),
(@quiz_id, 4, '완전 그래프', false),
(@quiz_id, 5, '연결 그래프', false);

-- 문제 번호 15
INSERT INTO quiz (quiz_book_id, question_type, content, answer, explanation)
VALUES
(@quiz_book_id, 'MCQ',
 'n개의 노드로 이루어진 트리의 간선 개수는?',
 '2',
 '트리에서 항상 간선 수 = 노드 수 - 1입니다.');

SET @quiz_id = LAST_INSERT_ID();

INSERT INTO mcq_option (quiz_id, option_number, option_content, is_correct) VALUES
(@quiz_id, 1, 'n', false),
(@quiz_id, 2, 'n-1', true),
(@quiz_id, 3, 'n+1', false),
(@quiz_id, 4, '2n', false),
(@quiz_id, 5, 'n^2', false);

-- 문제 번호 16
INSERT INTO quiz (quiz_book_id, question_type, content, answer, explanation)
VALUES
(@quiz_book_id, 'MCQ',
 '퀵 정렬(QuickSort)의 평균 시간 복잡도는?',
 '2',
 '퀵 정렬은 평균적으로 O(n log n), 최악에는 O(n^2)입니다.');

SET @quiz_id = LAST_INSERT_ID();

INSERT INTO mcq_option (quiz_id, option_number, option_content, is_correct) VALUES
(@quiz_id, 1, 'O(n)', false),
(@quiz_id, 2, 'O(n log n)', true),
(@quiz_id, 3, 'O(n^2)', false),
(@quiz_id, 4, 'O(log n)', false),
(@quiz_id, 5, 'O(1)', false);

-- 문제 번호 17
INSERT INTO quiz (quiz_book_id, question_type, content, answer, explanation)
VALUES
(@quiz_book_id, 'MCQ',
 '이진 탐색 트리(BST)의 중위 순회 결과는?',
 '3',
 'BST를 중위 순회하면 오름차순으로 값이 정렬됩니다.');

SET @quiz_id = LAST_INSERT_ID();

INSERT INTO mcq_option (quiz_id, option_number, option_content, is_correct) VALUES
(@quiz_id, 1, '역순', false),
(@quiz_id, 2, '무작위', false),
(@quiz_id, 3, '오름차순', true),
(@quiz_id, 4, '내림차순', false),
(@quiz_id, 5, '항상 같은 값', false);

-- 문제 번호 18
INSERT INTO quiz (quiz_book_id, question_type, content, answer, explanation)
VALUES
(@quiz_book_id, 'MCQ',
 '동적 계획법(DP)에서 "메모이제이션"이란 무엇인가?',
 '2',
 'DP에서 메모이제이션은 이미 계산한 결과를 저장하여, 같은 계산을 반복하지 않는 기법입니다.');

SET @quiz_id = LAST_INSERT_ID();

INSERT INTO mcq_option (quiz_id, option_number, option_content, is_correct) VALUES
(@quiz_id, 1, '코드를 짧게 만드는 방법', false),
(@quiz_id, 2, '중복 연산을 피하기 위해 결과를 저장', true),
(@quiz_id, 3, 'DFS와 BFS의 조합', false),
(@quiz_id, 4, '항상 반복문으로만 구현', false),
(@quiz_id, 5, '입력을 정렬하는 과정', false);

-- 문제 번호 19
INSERT INTO quiz (quiz_book_id, question_type, content, answer, explanation)
VALUES
(@quiz_book_id, 'MCQ',
 '이분 탐색(Binary Search)이 동작하려면 어떤 조건이 필요한가?',
 '1',
 '이분 탐색은 오직 정렬된 데이터에만 사용할 수 있습니다.');

SET @quiz_id = LAST_INSERT_ID();

INSERT INTO mcq_option (quiz_id, option_number, option_content, is_correct) VALUES
(@quiz_id, 1, '입력이 정렬되어 있어야 한다', true),
(@quiz_id, 2, '모두 음수여야 한다', false),
(@quiz_id, 3, '데이터가 중복되면 안 된다', false),
(@quiz_id, 4, '사이클이 없어야 한다', false),
(@quiz_id, 5, '트리 구조여야 한다', false);

-- 문제 번호 20
INSERT INTO quiz (quiz_book_id, question_type, content, answer, explanation)
VALUES
(@quiz_book_id, 'MCQ',
 '힙(Heap) 자료구조를 이용해 구현할 수 있는 대표적인 자료구조는?',
 '3',
 '힙은 우선순위 큐를 구현할 때 가장 많이 사용됩니다.');

SET @quiz_id = LAST_INSERT_ID();

INSERT INTO mcq_option (quiz_id, option_number, option_content, is_correct) VALUES
(@quiz_id, 1, '큐', false),
(@quiz_id, 2, '스택', false),
(@quiz_id, 3, '우선순위 큐', true),
(@quiz_id, 4, '트라이', false),
(@quiz_id, 5, '그래프', false);

-- 문제 번호 21
INSERT INTO quiz (quiz_book_id, question_type, content, answer, explanation)
VALUES
(@quiz_book_id, 'MCQ',
 '연결리스트에서 "뒤에서 k번째 노드"를 찾을 때 가장 빠른 방법은?',
 '3',
 '두 포인터(슬로우/패스트 포인터)로 k만큼 차이를 둬 한 번에 찾는 것이 효율적입니다.');

SET @quiz_id = LAST_INSERT_ID();

INSERT INTO mcq_option (quiz_id, option_number, option_content, is_correct) VALUES
(@quiz_id, 1, '앞에서부터 하나씩 센다', false),
(@quiz_id, 2, '스택을 사용한다', false),
(@quiz_id, 3, '두 포인터(빠른, 느린)로 찾는다', true),
(@quiz_id, 4, '배열로 변환한다', false),
(@quiz_id, 5, '큐에 저장한다', false);

-- 문제 번호 22
INSERT INTO quiz (quiz_book_id, question_type, content, answer, explanation)
VALUES
(@quiz_book_id, 'MCQ',
 '다음 중 그리디(Greedy) 알고리즘의 핵심 특징은?',
 '3',
 '그리디는 매 단계 가장 좋아 보이는 것을 선택하여 전체 문제의 해답을 구하는 방식입니다.');

SET @quiz_id = LAST_INSERT_ID();

INSERT INTO mcq_option (quiz_id, option_number, option_content, is_correct) VALUES
(@quiz_id, 1, '항상 전체 탐색', false),
(@quiz_id, 2, '최적 부분 구조', false),
(@quiz_id, 3, '한 단계에서 가장 좋은 선택', true),
(@quiz_id, 4, '동적 계획법을 포함', false),
(@quiz_id, 5, '반드시 재귀 사용', false);

-- 문제 번호 23
INSERT INTO quiz (quiz_book_id, question_type, content, answer, explanation)
VALUES
(@quiz_book_id, 'MCQ',
 '피보나치 수열을 재귀로 단순 구현하면 시간 복잡도는?',
 '4',
 '메모이제이션 없이 재귀로 구현하면, 같은 부분을 여러 번 계산해서 O(2^n)이 걸립니다.');

SET @quiz_id = LAST_INSERT_ID();

INSERT INTO mcq_option (quiz_id, option_number, option_content, is_correct) VALUES
(@quiz_id, 1, 'O(1)', false),
(@quiz_id, 2, 'O(n)', false),
(@quiz_id, 3, 'O(log n)', false),
(@quiz_id, 4, 'O(2^n)', true),
(@quiz_id, 5, 'O(n^2)', false);

-- 문제 번호 24
INSERT INTO quiz (quiz_book_id, question_type, content, answer, explanation)
VALUES
(@quiz_book_id, 'MCQ',
 '해시 함수의 충돌(Collision)이 발생할 때 보통 사용하는 방법은?',
 '2',
 '충돌이 발생하면, open addressing(재해싱), separate chaining(연결리스트 등) 기법을 사용합니다.');

SET @quiz_id = LAST_INSERT_ID();

INSERT INTO mcq_option (quiz_id, option_number, option_content, is_correct) VALUES
(@quiz_id, 1, '값 삭제', false),
(@quiz_id, 2, '재해싱(rehashing), 체이닝 등', true),
(@quiz_id, 3, '탐색 중단', false),
(@quiz_id, 4, '배열 복사', false),
(@quiz_id, 5, '정렬', false);

-- 문제 번호 25
INSERT INTO quiz (quiz_book_id, question_type, content, answer, explanation)
VALUES
(@quiz_book_id, 'MCQ',
 '인접 행렬(Adjacency Matrix)로 그래프를 표현할 때 적합한 경우는?',
 '2',
 '간선이 많은 "밀집 그래프"에서는 인접 행렬이 공간 효율 및 접근 속도에서 유리합니다.');

SET @quiz_id = LAST_INSERT_ID();

INSERT INTO mcq_option (quiz_id, option_number, option_content, is_correct) VALUES
(@quiz_id, 1, '간선이 매우 드물 때', false),
(@quiz_id, 2, '간선이 많은 밀집 그래프', true),
(@quiz_id, 3, '모든 노드가 트리 구조일 때', false),
(@quiz_id, 4, '모든 가중치가 동일할 때', false),
(@quiz_id, 5, '데이터가 정렬되어 있을 때', false);

-- 문제 번호 26
INSERT INTO quiz (quiz_book_id, question_type, content, answer, explanation)
VALUES
(@quiz_book_id, 'MCQ',
 '다음 중 "위상 정렬(Topological Sort)"이 필요한 문제는?',
 '2',
 '위상 정렬은 선후관계가 명확한 작업의 순서를 정하는 데 사용합니다.');

SET @quiz_id = LAST_INSERT_ID();

INSERT INTO mcq_option (quiz_id, option_number, option_content, is_correct) VALUES
(@quiz_id, 1, '순환(사이클) 검증', false),
(@quiz_id, 2, '작업 순서 결정(선후관계)', true),
(@quiz_id, 3, '트리 순회', false),
(@quiz_id, 4, 'BFS', false),
(@quiz_id, 5, 'DFS', false);

-- 문제 번호 27
INSERT INTO quiz (quiz_book_id, question_type, content, answer, explanation)
VALUES
(@quiz_book_id, 'MCQ',
 '깊이 우선 탐색(DFS)로 미로 찾기를 구현할 때 필요한 자료구조는?',
 '2',
 'DFS는 스택(재귀 호출 포함)을 사용하여 경로를 탐색합니다.');

SET @quiz_id = LAST_INSERT_ID();

INSERT INTO mcq_option (quiz_id, option_number, option_content, is_correct) VALUES
(@quiz_id, 1, '큐', false),
(@quiz_id, 2, '스택', true),
(@quiz_id, 3, '배열', false),
(@quiz_id, 4, '힙', false),
(@quiz_id, 5, '우선순위 큐', false);

-- 문제 번호 28
INSERT INTO quiz (quiz_book_id, question_type, content, answer, explanation)
VALUES
(@quiz_book_id, 'MCQ',
 '트리에서 "서브트리(subtree)"의 정의는?',
 '3',
 '트리에서 임의의 노드를 루트로 했을 때, 그 하위 전체를 "서브트리"라 부릅니다.');

SET @quiz_id = LAST_INSERT_ID();

INSERT INTO mcq_option (quiz_id, option_number, option_content, is_correct) VALUES
(@quiz_id, 1, '루트 노드만 포함', false),
(@quiz_id, 2, '리프 노드만 포함', false),
(@quiz_id, 3, '어떤 노드를 루트로 하는 모든 하위 트리', true),
(@quiz_id, 4, '한 레벨의 노드 집합', false),
(@quiz_id, 5, '모든 부모 노드 집합', false);

-- 문제 번호 29
INSERT INTO quiz (quiz_book_id, question_type, content, answer, explanation)
VALUES
(@quiz_book_id, 'MCQ',
 '2차원 배열에서 "BFS"를 이용한 탐색을 구현할 때 꼭 필요한 것은?',
 '1',
 '2차원 BFS는 무한루프/중복 방문을 방지하기 위해 방문 체크 배열이 필수입니다.');

SET @quiz_id = LAST_INSERT_ID();

INSERT INTO mcq_option (quiz_id, option_number, option_content, is_correct) VALUES
(@quiz_id, 1, '방문 체크 배열', true),
(@quiz_id, 2, '정렬', false),
(@quiz_id, 3, '재귀 함수', false),
(@quiz_id, 4, '우선순위 큐', false),
(@quiz_id, 5, '힙', false);

-- 문제 번호 30
INSERT INTO quiz (quiz_book_id, question_type, content, answer, explanation)
VALUES
(@quiz_book_id, 'MCQ',
 '다음 중 "최소 신장 트리(MST)"의 대표적인 알고리즘이 아닌 것은?',
 '3',
 '다익스트라는 최단 경로 알고리즘입니다. MST는 크루스칼, 프림이 대표적입니다.');

SET @quiz_id = LAST_INSERT_ID();

INSERT INTO mcq_option (quiz_id, option_number, option_content, is_correct) VALUES
(@quiz_id, 1, '크루스칼', false),
(@quiz_id, 2, '프림', false),
(@quiz_id, 3, '다익스트라', true),
(@quiz_id, 4, '모두 MST 알고리즘', false),
(@quiz_id, 5, '둘 다 아님', false);

-- 테스트 문제집
INSERT INTO quiz_book (version, category, title, level, description, created_by)
VALUES (1, '네트워크', '네트워크 객관식', 'HARD', '네트워크 객관식 테스트 문제집', 1);

SET @quiz_book_id = LAST_INSERT_ID();

INSERT INTO quiz (quiz_book_id, question_type, content, answer, explanation)
VALUES
(@quiz_book_id, 'MCQ',
 '네트워크 객관식 문제 1',
 '3',
 '네트워크 객관식 문제 1'
);

SET @quiz_id = LAST_INSERT_ID();

INSERT INTO mcq_option (quiz_id, option_number, option_content, is_correct) VALUES
(@quiz_id, 1, '오답', false),
(@quiz_id, 2, '오답', false),
(@quiz_id, 3, '정답', true),
(@quiz_id, 4, '오답', false),
(@quiz_id, 5, '오답', false);

INSERT INTO quiz (quiz_book_id, question_type, content, answer, explanation)
VALUES
(@quiz_book_id, 'MCQ',
 '네트워크 객관식 문제 2',
 '2',
 '네트워크 객관식 문제 2'
);

SET @quiz_id = LAST_INSERT_ID();

INSERT INTO mcq_option (quiz_id, option_number, option_content, is_correct) VALUES
(@quiz_id, 1, '오답', false),
(@quiz_id, 2, '정답', true),
(@quiz_id, 3, '오답', false),
(@quiz_id, 4, '오답', false),
(@quiz_id, 5, '오답', false);

INSERT INTO quiz (quiz_book_id, question_type, content, answer, explanation)
VALUES
(@quiz_book_id, 'MCQ',
 '네트워크 객관식 문제 3',
 '3',
 '네트워크 객관식 문제 3'
);

SET @quiz_id = LAST_INSERT_ID();

INSERT INTO mcq_option (quiz_id, option_number, option_content, is_correct) VALUES
(@quiz_id, 1, '오답', false),
(@quiz_id, 2, '오답', false),
(@quiz_id, 3, '정답', true),
(@quiz_id, 4, '오답', false),
(@quiz_id, 5, '오답', false);


INSERT INTO quiz_book (version, category, title, level, description, created_by)
VALUES (1, '네트워크', '네트워크 단답형', 'HARD', '네트워크 단답형 테스트 문제집', 1);

SET @quiz_book_id = LAST_INSERT_ID();

INSERT INTO quiz (quiz_book_id, question_type, content, answer, explanation)
VALUES
(@quiz_book_id, 'SHORT_ANSWER',
 '네트워크 단답형 문제 1',
 '정답',
 '네트워크 단답형 문제 1'
);

INSERT INTO quiz (quiz_book_id, question_type, content, answer, explanation)
VALUES
(@quiz_book_id, 'SHORT_ANSWER',
 '네트워크 단답형 문제 2',
 '정답',
 '네트워크 단답형 문제 2'
);

INSERT INTO quiz (quiz_book_id, question_type, content, answer, explanation)
VALUES
(@quiz_book_id, 'SHORT_ANSWER',
 '네트워크 단답형 문제 3',
 '정답',
 '네트워크 단답형 문제 3'
);

INSERT INTO quiz_book (version, category, title, level, description, created_by)
VALUES (1, '네트워크', '네트워크 OX', 'HARD', '네트워크 OX 테스트 문제집', 1);

SET @quiz_book_id = LAST_INSERT_ID();

INSERT INTO quiz (quiz_book_id, question_type, content, answer, explanation)
VALUES
(@quiz_book_id, 'OX',
 '네트워크 OX 문제 1',
 'O',
 '네트워크 OX 문제 1'
);

INSERT INTO quiz (quiz_book_id, question_type, content, answer, explanation)
VALUES
(@quiz_book_id, 'OX',
 '네트워크 OX 문제 2',
 'X',
 '네트워크 OX 문제 2'
);

INSERT INTO quiz (quiz_book_id, question_type, content, answer, explanation)
VALUES
(@quiz_book_id, 'OX',
 '네트워크 OX 문제 3',
 'O',
 '네트워크 OX 문제 3'
);

INSERT INTO quiz_book (version, category, title, level, description, created_by)
VALUES (1, '네트워크', '네트워크 혼합형', 'HARD', '네트워크 혼합형 테스트 문제집', 1);

SET @quiz_book_id = LAST_INSERT_ID();

INSERT INTO quiz (quiz_book_id, question_type, content, answer, explanation)
VALUES
(@quiz_book_id, 'OX',
 '네트워크 OX 문제 1',
 'O',
 '네트워크 OX 문제 1'
);

INSERT INTO quiz (quiz_book_id, question_type, content, answer, explanation)
VALUES
(@quiz_book_id, 'SHORT_ANSWER',
 '네트워크 단답형 문제 2',
 '정답',
 '네트워크 단답형 문제 2'
);

INSERT INTO quiz (quiz_book_id, question_type, content, answer, explanation)
VALUES
(@quiz_book_id, 'MCQ',
 '네트워크 객관식 문제 3',
 '3',
 '네트워크 객관식 문제 3'
);

SET @quiz_id = LAST_INSERT_ID();

INSERT INTO mcq_option (quiz_id, option_number, option_content, is_correct) VALUES
(@quiz_id, 1, '오답', false),
(@quiz_id, 2, '오답', false),
(@quiz_id, 3, '정답', true),
(@quiz_id, 4, '오답', false),
(@quiz_id, 5, '오답', false);