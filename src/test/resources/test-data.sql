-- 테스트용 기본 데이터

-- 기본 사용자 데이터
INSERT INTO `user` (user_id, email, password, name, created_at, oauth) VALUES
('test-user-1', 'test1@example.com', '$2a$10$dummyHashedPassword1', '테스트유저1', '2024-01-01 10:00:00', NULL),
('test-user-2', 'test2@example.com', '$2a$10$dummyHashedPassword2', '테스트유저2', '2024-01-02 10:00:00', 'GOOGLE'),
('test-user-3', 'oauth@google.com', '', '구글유저', '2024-01-03 10:00:00', 'GOOGLE');

-- 기본 스킬 데이터
INSERT INTO skill(skill_id, skill) VALUES
(1, 'Java'),
(2, 'Spring'),
(3, 'Python'),
(4, 'JavaScript'),
(5, 'React');
