-- ================================================
-- 기존 데이터 전체 삭제 (재실행 가능하도록)
-- ================================================
SET FOREIGN_KEY_CHECKS = 0;

DELETE FROM interview_material;
DELETE FROM interview_score;
DELETE FROM interview_question;
DELETE FROM interview;
DELETE FROM pre_question;
DELETE FROM setting_skill;
DELETE FROM interview_setting;
DELETE FROM `user`;
DELETE FROM skill;

SET FOREIGN_KEY_CHECKS = 1;

-- ================================================
-- 테스트용 더미 데이터 (UUID는 매 실행마다 랜덤 생성)
-- ================================================

-- 스킬 (UUID 사용)
SET @sk1  = UUID(); SET @sk2  = UUID(); SET @sk3  = UUID(); SET @sk4  = UUID(); SET @sk5  = UUID();
SET @sk6  = UUID(); SET @sk7  = UUID(); SET @sk8  = UUID(); SET @sk9  = UUID(); SET @sk10 = UUID();

INSERT IGNORE INTO skill (skill_id, skill) VALUES (@sk1, 'Spring');
INSERT IGNORE INTO skill (skill_id, skill) VALUES (@sk2, 'Java');
INSERT IGNORE INTO skill (skill_id, skill) VALUES (@sk3, 'MySQL');
INSERT IGNORE INTO skill (skill_id, skill) VALUES (@sk4, 'Docker');
INSERT IGNORE INTO skill (skill_id, skill) VALUES (@sk5, 'React');
INSERT IGNORE INTO skill (skill_id, skill) VALUES (@sk6, 'TypeScript');
INSERT IGNORE INTO skill (skill_id, skill) VALUES (@sk7, 'Kubernetes');
INSERT IGNORE INTO skill (skill_id, skill) VALUES (@sk8, 'Redis');
INSERT IGNORE INTO skill (skill_id, skill) VALUES (@sk9, 'Python');
INSERT IGNORE INTO skill (skill_id, skill) VALUES (@sk10, 'AWS');

-- ================================================
-- 사용자 3명
-- ================================================
SET @u1 = UUID();
SET @u2 = UUID();
SET @u3 = UUID();

INSERT INTO `user` (user_id, email, password, name, created_at, oauth) VALUES
(@u1, 'testuser1@boterview.com', 'qwer1234', '김면접', '2026-01-01 09:00:00', NULL),
(@u2, 'testuser2@boterview.com', 'qwer1234', '이개발', '2026-01-05 10:00:00', NULL),
(@u3, 'testuser3@boterview.com', 'qwer1234', '박코딩', '2026-01-10 11:00:00', NULL);

-- ================================================
-- 사용자1 - 면접 설정 4개
-- ================================================
SET @s1 = UUID();
SET @s2 = UUID();
SET @s3 = UUID();
SET @s4 = UUID();

INSERT INTO interview_setting (setting_id, user_id, question_count, interviewer_style, interviewer_gender, interviewer_appearance, created_at, resume_uri, position) VALUES
(@s1, @u1, 5, 'FORMAL',   'MALE',   'REAL',   '2026-01-10 10:00:00', 's3://bucket/resume/s1/resume.pdf', 'BACKEND_DEVELOPER'),
(@s2, @u1, 3, 'CASUAL',   'FEMALE', 'ANIME',  '2026-01-15 14:00:00', NULL,                               'FRONT_DEVELOPER'),
(@s3, @u1, 4, 'PRESSURE', 'MALE',   'REAL',   '2026-01-20 09:00:00', 's3://bucket/resume/s3/resume.pdf', 'DEV_OPS_DEVELOPER'),
(@s4, @u1, 5, 'FORMAL',   'FEMALE', 'ANIMAL', '2026-01-25 16:00:00', 's3://bucket/resume/s4/resume.pdf', 'DBA');

-- 사용자2 - 면접 설정 3개
SET @s5 = UUID();
SET @s6 = UUID();
SET @s7 = UUID();

INSERT INTO interview_setting (setting_id, user_id, question_count, interviewer_style, interviewer_gender, interviewer_appearance, created_at, resume_uri, position) VALUES
(@s5, @u2, 5, 'FORMAL',   'MALE',   'REAL',  '2026-01-12 10:00:00', 's3://bucket/resume/s5/resume.pdf', 'BACKEND_DEVELOPER'),
(@s6, @u2, 4, 'CASUAL',   'FEMALE', 'ANIME', '2026-01-18 11:00:00', NULL,                               'AI_DEVELOPER'),
(@s7, @u2, 3, 'PRESSURE', 'MALE',   'REAL',  '2026-01-22 15:00:00', 's3://bucket/resume/s7/resume.pdf', 'BACKEND_DEVELOPER');

-- 사용자3 - 면접 설정 2개
SET @s8 = UUID();
SET @s9 = UUID();

INSERT INTO interview_setting (setting_id, user_id, question_count, interviewer_style, interviewer_gender, interviewer_appearance, created_at, resume_uri, position) VALUES
(@s8, @u3, 5, 'FORMAL', 'FEMALE', 'REAL',   '2026-01-14 13:00:00', 's3://bucket/resume/s8/resume.pdf', 'FRONT_DEVELOPER'),
(@s9, @u3, 4, 'CASUAL', 'MALE',   'ANIMAL', '2026-01-20 17:00:00', NULL,                               'BACKEND_DEVELOPER');

-- ================================================
-- 설정-스킬 매핑
-- ================================================
INSERT INTO setting_skill (setting_id, skill_id) VALUES
(@s1, @sk1), (@s1, @sk2), (@s1, @sk3),
(@s2, @sk5), (@s2, @sk6),
(@s3, @sk4), (@s3, @sk7), (@s3, @sk10),
(@s4, @sk3), (@s4, @sk8),
(@s5, @sk1), (@s5, @sk2), (@s5, @sk8),
(@s6, @sk9), (@s6, @sk10),
(@s7, @sk1), (@s7, @sk2), (@s7, @sk3), (@s7, @sk4),
(@s8, @sk5), (@s8, @sk6),
(@s9, @sk1), (@s9, @sk2);

-- ================================================
-- 사전 질문
-- ================================================
INSERT INTO pre_question (pre_question_id, setting_id, question, answer) VALUES
(UUID(), @s1, 'Spring IoC 컨테이너란?', 'Bean의 생명주기를 관리하는 컨테이너입니다.'),
(UUID(), @s1, 'DI와 IoC의 차이는?', 'IoC는 제어의 역전 원칙이고, DI는 그 구현 방법입니다.'),
(UUID(), @s1, 'Spring Bean의 스코프 종류는?', 'singleton, prototype, request, session, application 등이 있습니다.'),
(UUID(), @s3, 'Docker와 VM의 차이는?', 'Docker는 OS 수준 가상화, VM은 하드웨어 수준 가상화입니다.'),
(UUID(), @s3, 'CI/CD 파이프라인이란?', '코드 변경 사항을 자동으로 빌드, 테스트, 배포하는 프로세스입니다.'),
(UUID(), @s5, 'JPA N+1 문제란?', '연관 엔티티 조회 시 추가 쿼리가 N번 발생하는 문제입니다.'),
(UUID(), @s5, 'Redis의 주요 용도는?', '캐싱, 세션 관리, 메시지 큐, 실시간 랭킹 등에 사용됩니다.'),
(UUID(), @s8, 'React의 생명주기란?', '컴포넌트가 생성, 업데이트, 소멸되는 과정입니다.'),
(UUID(), @s9, 'RESTful API란?', 'HTTP 메서드를 활용하여 자원을 CRUD하는 아키텍처 스타일입니다.');

-- ================================================
-- 사용자1 면접 8건
-- ================================================
SET @i1  = UUID(); SET @i2  = UUID(); SET @i3  = UUID(); SET @i4  = UUID();
SET @i5  = UUID(); SET @i6  = UUID(); SET @i7  = UUID(); SET @i8  = UUID();

INSERT INTO interview (interview_id, setting_id, duration, created_at, ai_overall_review, interview_name) VALUES
(@i1, @s1, 780000,  '2026-01-11 10:30:00', '전체적으로 논리 구조는 좋으나 기술 용어의 정확한 사용이 부족합니다.', 'Backend 1차 면접'),
(@i2, @s1, 920000,  '2026-01-13 14:00:00', 'Spring 핵심 개념에 대한 이해도가 높고 답변이 체계적입니다.', 'Backend 2차 면접'),
(@i3, @s2, 540000,  '2026-01-16 11:00:00', 'React 기초 지식은 탄탄하나 상태 관리 패턴에 대한 깊이가 부족합니다.', 'Frontend React 면접'),
(@i4, @s2, 600000,  '2026-01-18 16:00:00', 'TypeScript 타입 시스템에 대한 이해가 우수합니다.', 'Frontend TS 심화 면접'),
(@i5, @s3, 850000,  '2026-01-21 09:30:00', 'DevOps 전반에 대한 이해는 있으나 실무 경험이 부족해 보입니다.', 'DevOps 기초 면접'),
(@i6, @s3, 720000,  '2026-01-23 10:00:00', 'Kubernetes 운영 경험에 대한 답변이 구체적이고 좋았습니다.', 'DevOps K8s 심화'),
(@i7, @s4, 680000,  '2026-01-26 14:30:00', 'SQL 최적화에 대한 이해도는 높으나 실행 계획 분석이 미흡합니다.', 'DBA SQL 최적화 면접'),
(@i8, @s4, 900000,  '2026-01-28 11:00:00', '인덱스 설계와 파티셔닝에 대한 답변이 탁월했습니다.', 'DBA 인덱스 심화 면접');

-- 사용자2 면접 5건
SET @i9  = UUID(); SET @i10 = UUID(); SET @i11 = UUID(); SET @i12 = UUID(); SET @i13 = UUID();

INSERT INTO interview (interview_id, setting_id, duration, created_at, ai_overall_review, interview_name) VALUES
(@i9,  @s5, 810000, '2026-01-13 11:00:00', 'JPA와 QueryDSL 활용 능력이 뛰어납니다.', 'Backend JPA 면접'),
(@i10, @s5, 750000, '2026-01-15 15:00:00', 'Redis 캐싱 전략에 대한 이해가 실무 수준입니다.', 'Backend Redis 면접'),
(@i11, @s6, 660000, '2026-01-19 10:00:00', 'ML 파이프라인 설계에 대한 기본기가 갖춰져 있습니다.', 'AI/ML 기초 면접'),
(@i12, @s6, 720000, '2026-01-21 14:00:00', '모델 서빙과 최적화에 대한 답변이 인상적이었습니다.', 'AI 모델 서빙 면접'),
(@i13, @s7, 580000, '2026-01-24 09:00:00', 'MSA 아키텍처 경험이 풍부하고 트러블슈팅 능력이 좋습니다.', 'Backend MSA 면접');

-- 사용자3 면접 4건
SET @i14 = UUID(); SET @i15 = UUID(); SET @i16 = UUID(); SET @i17 = UUID();

INSERT INTO interview (interview_id, setting_id, duration, created_at, ai_overall_review, interview_name) VALUES
(@i14, @s8, 700000, '2026-01-15 14:00:00', 'CSS 레이아웃과 반응형 디자인에 대한 이해가 좋습니다.', 'Frontend CSS 면접'),
(@i15, @s8, 640000, '2026-01-17 11:00:00', 'Next.js SSR/SSG 개념을 정확히 이해하고 있습니다.', 'Frontend Next.js 면접'),
(@i16, @s9, 830000, '2026-01-22 10:00:00', 'Spring Security 설정 및 JWT 인증에 대한 이해가 깊습니다.', 'Backend 인증 면접'),
(@i17, @s9, 760000, '2026-01-25 15:00:00', '동시성 제어와 락 전략에 대한 실무 경험이 돋보입니다.', 'Backend 동시성 면접');

-- ================================================
-- 면접 질문 (면접당 3~5개, question_id에 UUID 사용)
-- ================================================

-- 면접1 (@i1) - 5문제
INSERT INTO interview_question (question_id, interview_id, question, answer, created_at, elapsed_time) VALUES
(UUID(), @i1, '트랜잭션 격리수준을 설명해보세요.', 'READ COMMITTED는 커밋된 데이터만 읽을 수 있는 격리 수준입니다. Dirty Read를 방지합니다.', '2026-01-11 10:31:10', 65000),
(UUID(), @i1, '인덱스의 작동 원리를 설명해보세요.', 'B-Tree 기반으로 데이터를 정렬된 상태로 유지하며 이진 탐색을 통해 빠른 조회가 가능합니다.', '2026-01-11 10:33:00', 82000),
(UUID(), @i1, 'Spring AOP의 동작 원리를 설명해보세요.', '프록시 패턴을 사용하여 메서드 호출 전후에 부가 기능을 삽입합니다.', '2026-01-11 10:36:00', 95000),
(UUID(), @i1, 'JPA의 영속성 컨텍스트란?', '엔티티를 영구 저장하기 전 1차 캐시 역할을 하는 논리적 영역입니다.', '2026-01-11 10:39:00', 78000),
(UUID(), @i1, 'SOLID 원칙에 대해 설명해주세요.', '단일 책임, 개방-폐쇄, 리스코프 치환, 인터페이스 분리, 의존 역전 원칙입니다.', '2026-01-11 10:42:00', 88000);

-- 면접2 (@i2) - 5문제
INSERT INTO interview_question (question_id, interview_id, question, answer, created_at, elapsed_time) VALUES
(UUID(), @i2, 'Spring MVC의 요청 처리 흐름을 설명해주세요.', 'DispatcherServlet이 요청을 받아 HandlerMapping으로 컨트롤러를 찾고 HandlerAdapter를 통해 실행합니다.', '2026-01-13 14:02:00', 72000),
(UUID(), @i2, '@Transactional의 전파 속성을 아는 대로 설명해주세요.', 'REQUIRED, REQUIRES_NEW, NESTED, SUPPORTS 등이 있으며 REQUIRED가 기본값입니다.', '2026-01-13 14:05:00', 90000),
(UUID(), @i2, 'Spring Security의 인증 처리 과정을 설명해주세요.', 'AuthenticationFilter가 요청을 가로채 AuthenticationManager를 통해 인증을 처리합니다.', '2026-01-13 14:08:00', 85000),
(UUID(), @i2, 'Bean 생명주기 콜백에 대해 설명해주세요.', '@PostConstruct, @PreDestroy 또는 InitializingBean, DisposableBean 인터페이스를 사용합니다.', '2026-01-13 14:11:00', 68000),
(UUID(), @i2, 'ConnectionPool의 역할과 설정 시 주의점은?', 'DB 연결을 미리 생성해 재사용합니다. maxPoolSize, minIdle 등 적절한 설정이 필요합니다.', '2026-01-13 14:14:00', 75000);

-- 면접3 (@i3) - 3문제
INSERT INTO interview_question (question_id, interview_id, question, answer, created_at, elapsed_time) VALUES
(UUID(), @i3, 'React의 Virtual DOM이란 무엇인가요?', '실제 DOM의 가벼운 복사본으로 diff 알고리즘으로 최소한의 DOM 업데이트만 수행합니다.', '2026-01-16 11:02:00', 70000),
(UUID(), @i3, 'useEffect와 useLayoutEffect의 차이는?', 'useEffect는 비동기적으로 실행되고 useLayoutEffect는 DOM 변경 후 동기적으로 실행됩니다.', '2026-01-16 11:04:30', 55000),
(UUID(), @i3, 'React에서 상태 관리 방법을 비교해주세요.', 'useState는 로컬 상태, useContext는 전역 상태, Redux는 복잡한 상태 관리에 적합합니다.', '2026-01-16 11:07:00', 80000);

-- 면접4 (@i4) - 4문제
INSERT INTO interview_question (question_id, interview_id, question, answer, created_at, elapsed_time) VALUES
(UUID(), @i4, 'TypeScript의 제네릭을 설명해주세요.', '타입을 매개변수화하여 재사용 가능한 컴포넌트를 만드는 기능입니다.', '2026-01-18 16:02:00', 60000),
(UUID(), @i4, 'interface와 type의 차이는?', 'interface는 선언 병합이 가능하고 type은 유니온, 인터섹션 등 더 유연한 타입 조합이 가능합니다.', '2026-01-18 16:05:00', 72000),
(UUID(), @i4, 'React 컴포넌트 최적화 방법은?', 'React.memo, useMemo, useCallback을 사용하여 불필요한 리렌더링을 방지합니다.', '2026-01-18 16:08:00', 65000),
(UUID(), @i4, 'CSR과 SSR의 차이를 설명해주세요.', 'CSR은 브라우저에서 렌더링, SSR은 서버에서 HTML을 생성하여 전달합니다. SEO와 초기 로딩에 차이가 있습니다.', '2026-01-18 16:11:00', 78000);

-- 면접5 (@i5) - 4문제
INSERT INTO interview_question (question_id, interview_id, question, answer, created_at, elapsed_time) VALUES
(UUID(), @i5, 'Docker 컨테이너와 이미지의 차이는?', '이미지는 읽기 전용 템플릿이고 컨테이너는 이미지의 실행 인스턴스입니다.', '2026-01-21 09:32:00', 55000),
(UUID(), @i5, 'Kubernetes의 Pod란?', 'K8s에서 배포 가능한 최소 단위로 하나 이상의 컨테이너를 포함합니다.', '2026-01-21 09:35:00', 68000),
(UUID(), @i5, 'CI/CD 파이프라인을 어떻게 구성하시나요?', 'GitHub Actions로 PR시 테스트, main 병합시 Docker 빌드 후 ECR 푸시, ECS 배포합니다.', '2026-01-21 09:39:00', 92000),
(UUID(), @i5, 'Blue-Green 배포와 Rolling 배포의 차이는?', 'Blue-Green은 두 환경을 전환, Rolling은 점진적으로 인스턴스를 교체하는 방식입니다.', '2026-01-21 09:43:00', 75000);

-- 면접6 (@i6) - 3문제
INSERT INTO interview_question (question_id, interview_id, question, answer, created_at, elapsed_time) VALUES
(UUID(), @i6, 'K8s의 Service 종류를 설명해주세요.', 'ClusterIP, NodePort, LoadBalancer, ExternalName이 있으며 각각 접근 범위가 다릅니다.', '2026-01-23 10:03:00', 80000),
(UUID(), @i6, 'Helm Chart란 무엇인가요?', 'K8s 리소스를 패키징하여 배포를 관리하는 패키지 매니저입니다.', '2026-01-23 10:06:00', 60000),
(UUID(), @i6, 'K8s에서 HPA는 어떻게 동작하나요?', 'CPU, 메모리 사용률 등 메트릭을 기반으로 Pod 수를 자동 조절합니다.', '2026-01-23 10:09:00', 70000);

-- 면접7 (@i7) - 4문제
INSERT INTO interview_question (question_id, interview_id, question, answer, created_at, elapsed_time) VALUES
(UUID(), @i7, 'SQL 실행 계획을 읽는 방법은?', 'EXPLAIN 명령으로 확인하며 type, key, rows, Extra 컬럼을 주로 확인합니다.', '2026-01-26 14:32:00', 75000),
(UUID(), @i7, '커버링 인덱스란?', '쿼리에 필요한 모든 컬럼이 인덱스에 포함되어 테이블 접근 없이 결과를 반환하는 것입니다.', '2026-01-26 14:35:00', 62000),
(UUID(), @i7, 'DB 락의 종류를 설명해주세요.', '공유 락(S-Lock)과 배타 락(X-Lock)이 있으며 레코드 락, 갭 락, 넥스트키 락 등이 있습니다.', '2026-01-26 14:38:00', 88000),
(UUID(), @i7, '정규화와 반정규화의 트레이드오프는?', '정규화는 중복 제거와 무결성, 반정규화는 읽기 성능 향상이 목적입니다.', '2026-01-26 14:41:00', 70000);

-- 면접8 (@i8) - 5문제
INSERT INTO interview_question (question_id, interview_id, question, answer, created_at, elapsed_time) VALUES
(UUID(), @i8, '파티셔닝의 종류와 사용 시나리오는?', 'Range, List, Hash, Key 파티셔닝이 있으며 대용량 테이블의 관리와 성능 향상에 사용합니다.', '2026-01-28 11:02:00', 85000),
(UUID(), @i8, '복합 인덱스 설계 시 고려사항은?', '카디널리티가 높은 컬럼을 앞에 배치하고 WHERE, ORDER BY, GROUP BY 패턴을 고려합니다.', '2026-01-28 11:05:00', 78000),
(UUID(), @i8, 'MySQL의 InnoDB와 MyISAM 차이는?', 'InnoDB는 트랜잭션, 외래키, 행 수준 잠금을 지원하고 MyISAM은 테이블 수준 잠금입니다.', '2026-01-28 11:08:00', 65000),
(UUID(), @i8, 'Slow Query를 개선하는 접근 방법은?', 'EXPLAIN 분석, 인덱스 추가/변경, 쿼리 리팩토링, 캐싱 도입 순으로 접근합니다.', '2026-01-28 11:11:00', 92000),
(UUID(), @i8, 'MVCC란 무엇인가요?', '다중 버전 동시성 제어로 읽기와 쓰기가 서로를 차단하지 않도록 하는 메커니즘입니다.', '2026-01-28 11:14:00', 70000);

-- 면접9 (@i9) - 4문제
INSERT INTO interview_question (question_id, interview_id, question, answer, created_at, elapsed_time) VALUES
(UUID(), @i9, 'JPA N+1 문제를 해결하는 방법은?', 'Fetch Join, @EntityGraph, Batch Size 설정 등으로 해결할 수 있습니다.', '2026-01-13 11:02:00', 72000),
(UUID(), @i9, 'QueryDSL의 장점은?', '컴파일 타임에 쿼리 오류를 검증할 수 있고 동적 쿼리 작성이 용이합니다.', '2026-01-13 11:05:00', 58000),
(UUID(), @i9, '영속성 컨텍스트의 장점을 설명해주세요.', '1차 캐시, 동일성 보장, 변경 감지, 지연 로딩 등의 이점이 있습니다.', '2026-01-13 11:08:00', 80000),
(UUID(), @i9, 'OSIV 패턴이란?', 'Open Session In View로 뷰 렌더링까지 영속성 컨텍스트를 유지하는 패턴입니다.', '2026-01-13 11:11:00', 65000);

-- 면접10 (@i10) - 4문제
INSERT INTO interview_question (question_id, interview_id, question, answer, created_at, elapsed_time) VALUES
(UUID(), @i10, 'Redis의 데이터 타입을 설명해주세요.', 'String, List, Set, Sorted Set, Hash 등이 있습니다.', '2026-01-15 15:02:00', 55000),
(UUID(), @i10, 'Cache Aside 패턴을 설명해주세요.', '캐시에서 먼저 조회하고 없으면 DB에서 조회 후 캐시에 저장하는 패턴입니다.', '2026-01-15 15:05:00', 68000),
(UUID(), @i10, 'Redis의 Pub/Sub 기능은?', '메시지 발행/구독 패턴으로 실시간 알림 등에 활용할 수 있습니다.', '2026-01-15 15:08:00', 60000),
(UUID(), @i10, 'Redis 클러스터의 동작 방식은?', '해시 슬롯 기반으로 데이터를 분산 저장하고 마스터-슬레이브 복제를 지원합니다.', '2026-01-15 15:11:00', 82000);

-- 면접11 (@i11) - 3문제
INSERT INTO interview_question (question_id, interview_id, question, answer, created_at, elapsed_time) VALUES
(UUID(), @i11, 'ML 파이프라인의 구성 요소는?', '데이터 수집, 전처리, 모델 학습, 평가, 배포 단계로 구성됩니다.', '2026-01-19 10:02:00', 75000),
(UUID(), @i11, 'Overfitting을 방지하는 방법은?', '정규화, 드롭아웃, 교차 검증, 데이터 증강 등을 사용합니다.', '2026-01-19 10:05:00', 68000),
(UUID(), @i11, 'Feature Engineering이란?', '모델 성능을 향상시키기 위해 원본 데이터에서 유의미한 특성을 추출/생성하는 과정입니다.', '2026-01-19 10:08:00', 72000);

-- 면접12 (@i12) - 4문제
INSERT INTO interview_question (question_id, interview_id, question, answer, created_at, elapsed_time) VALUES
(UUID(), @i12, 'TensorFlow Serving이란?', '학습된 모델을 프로덕션 환경에서 서빙하기 위한 고성능 시스템입니다.', '2026-01-21 14:02:00', 65000),
(UUID(), @i12, '모델 경량화 기법을 설명해주세요.', '프루닝, 양자화, 지식 증류 등을 통해 모델 크기와 추론 시간을 줄입니다.', '2026-01-21 14:05:00', 78000),
(UUID(), @i12, 'A/B 테스트를 모델에 적용하는 방법은?', '트래픽을 분할하여 기존 모델과 새 모델의 성능을 실시간으로 비교합니다.', '2026-01-21 14:08:00', 70000),
(UUID(), @i12, 'MLOps란 무엇인가요?', 'ML 모델의 개발, 배포, 운영을 자동화하는 DevOps의 확장 개념입니다.', '2026-01-21 14:11:00', 62000);

-- 면접13 (@i13) - 3문제
INSERT INTO interview_question (question_id, interview_id, question, answer, created_at, elapsed_time) VALUES
(UUID(), @i13, 'MSA에서 서비스 간 통신 방법은?', 'REST, gRPC, 메시지 큐(Kafka, RabbitMQ) 등을 사용합니다.', '2026-01-24 09:02:00', 68000),
(UUID(), @i13, 'Circuit Breaker 패턴이란?', '장애가 발생한 서비스 호출을 차단하여 전체 시스템 장애를 방지하는 패턴입니다.', '2026-01-24 09:05:00', 72000),
(UUID(), @i13, 'SAGA 패턴을 설명해주세요.', '분산 트랜잭션을 로컬 트랜잭션의 시퀀스로 관리하며 보상 트랜잭션으로 롤백합니다.', '2026-01-24 09:08:00', 85000);

-- 면접14 (@i14) - 4문제
INSERT INTO interview_question (question_id, interview_id, question, answer, created_at, elapsed_time) VALUES
(UUID(), @i14, 'Flexbox와 Grid의 차이는?', 'Flexbox는 1차원 레이아웃, Grid는 2차원 레이아웃을 위한 CSS 모듈입니다.', '2026-01-15 14:02:00', 58000),
(UUID(), @i14, '반응형 디자인의 핵심 기법은?', 'Media Query, 유동적 그리드, 유연한 이미지, viewport 메타태그를 사용합니다.', '2026-01-15 14:05:00', 65000),
(UUID(), @i14, 'CSS-in-JS의 장단점은?', '컴포넌트 단위 스타일링이 가능하나 런타임 오버헤드와 번들 크기 증가가 단점입니다.', '2026-01-15 14:08:00', 72000),
(UUID(), @i14, 'BEM 방법론이란?', 'Block-Element-Modifier의 약자로 CSS 클래스 네이밍 규칙입니다.', '2026-01-15 14:11:00', 50000);

-- 면접15 (@i15) - 3문제
INSERT INTO interview_question (question_id, interview_id, question, answer, created_at, elapsed_time) VALUES
(UUID(), @i15, 'Next.js의 SSR과 SSG 차이는?', 'SSR은 매 요청마다 서버에서 렌더링, SSG는 빌드 시 정적 페이지를 생성합니다.', '2026-01-17 11:02:00', 70000),
(UUID(), @i15, 'Next.js의 API Routes란?', 'pages/api 디렉토리에서 서버리스 API 엔드포인트를 생성할 수 있는 기능입니다.', '2026-01-17 11:05:00', 55000),
(UUID(), @i15, 'ISR이란 무엇인가요?', 'Incremental Static Regeneration으로 빌드 후에도 정적 페이지를 주기적으로 재생성합니다.', '2026-01-17 11:08:00', 68000);

-- 면접16 (@i16) - 4문제
INSERT INTO interview_question (question_id, interview_id, question, answer, created_at, elapsed_time) VALUES
(UUID(), @i16, 'JWT의 구조를 설명해주세요.', 'Header, Payload, Signature 세 부분으로 구성되며 Base64로 인코딩됩니다.', '2026-01-22 10:02:00', 62000),
(UUID(), @i16, 'OAuth 2.0의 Authorization Code Flow는?', '클라이언트가 인증 서버에서 코드를 받고 이를 토큰으로 교환하는 방식입니다.', '2026-01-22 10:05:00', 78000),
(UUID(), @i16, 'CORS란 무엇이고 어떻게 처리하나요?', '다른 출처의 리소스 접근 제한 정책으로 서버에서 허용 헤더를 설정하여 처리합니다.', '2026-01-22 10:08:00', 70000),
(UUID(), @i16, 'XSS와 CSRF의 차이와 방어 방법은?', 'XSS는 스크립트 주입, CSRF는 인증된 사용자의 의도치 않은 요청입니다.', '2026-01-22 10:12:00', 90000);

-- 면접17 (@i17) - 4문제
INSERT INTO interview_question (question_id, interview_id, question, answer, created_at, elapsed_time) VALUES
(UUID(), @i17, 'Synchronized와 ReentrantLock의 차이는?', 'ReentrantLock은 tryLock, fairness, condition 등 더 세밀한 제어가 가능합니다.', '2026-01-25 15:02:00', 72000),
(UUID(), @i17, '낙관적 락과 비관적 락의 차이는?', '낙관적 락은 충돌이 적을 때, 비관적 락은 충돌이 많을 때 적합합니다.', '2026-01-25 15:05:00', 65000),
(UUID(), @i17, 'ThreadPool 설정 시 고려사항은?', 'CPU/IO bound 작업 비율, 최대 동시 요청 수, 큐 크기 등을 고려합니다.', '2026-01-25 15:08:00', 80000),
(UUID(), @i17, 'ConcurrentHashMap의 동작 원리는?', '세그먼트 단위 잠금으로 동시 읽기/쓰기 성능을 보장합니다.', '2026-01-25 15:11:00', 68000);

-- ================================================
-- 면접 점수 (면접당 3~5개)
-- ================================================

-- 면접1 점수
INSERT INTO interview_score (score_id, interview_id, score_type, score, evaludation) VALUES
(UUID(), @i1, 'PRONUNCIATION_ACCURACY', 78, '기술 용어 발음은 정확하나 말 빠르기 조절이 필요합니다.'),
(UUID(), @i1, 'RESPONSE_ACCURACY', 70, '핵심 개념은 파악하고 있으나 세부 설명이 부족합니다.'),
(UUID(), @i1, 'SPEAKING_PACE', 72, '답변 초반에는 빠르지만 후반부는 적절한 속도입니다.'),
(UUID(), @i1, 'VOCABULARY_QUALITY', 75, '기술 용어 사용은 적절하나 비유적 표현이 부족합니다.'),
(UUID(), @i1, 'OVERALL', 74, '전반적으로 준수한 면접이었으나 깊이 있는 설명이 부족합니다.');

-- 면접2 점수
INSERT INTO interview_score (score_id, interview_id, score_type, score, evaludation) VALUES
(UUID(), @i2, 'PRONUNCIATION_ACCURACY', 85, '명확한 발음과 적절한 억양으로 전달력이 좋습니다.'),
(UUID(), @i2, 'RESPONSE_ACCURACY', 88, 'Spring 핵심 개념을 정확히 이해하고 체계적으로 답변했습니다.'),
(UUID(), @i2, 'SPEAKING_PACE', 82, '일정한 속도로 차분하게 답변했습니다.'),
(UUID(), @i2, 'VOCABULARY_QUALITY', 86, '전문 용어를 적절히 사용하며 쉬운 비유로 설명합니다.'),
(UUID(), @i2, 'OVERALL', 85, 'Spring 전반에 대한 깊은 이해와 체계적 답변이 인상적입니다.');

-- 면접3 점수
INSERT INTO interview_score (score_id, interview_id, score_type, score, evaludation) VALUES
(UUID(), @i3, 'RESPONSE_ACCURACY', 72, 'React 기초는 탄탄하나 상태 관리 심화 부분이 미흡합니다.'),
(UUID(), @i3, 'SPEAKING_PACE', 68, '답변이 다소 빠르고 정리가 덜 된 느낌입니다.'),
(UUID(), @i3, 'OVERALL', 70, '기초 지식은 갖추고 있으나 심화 학습이 필요합니다.');

-- 면접4 점수
INSERT INTO interview_score (score_id, interview_id, score_type, score, evaludation) VALUES
(UUID(), @i4, 'PRONUNCIATION_ACCURACY', 80, '영어 기술 용어 발음이 자연스럽습니다.'),
(UUID(), @i4, 'RESPONSE_ACCURACY', 83, 'TypeScript 타입 시스템에 대한 이해가 깊습니다.'),
(UUID(), @i4, 'VOCABULARY_QUALITY', 81, '적절한 기술 용어 사용과 예시가 좋았습니다.'),
(UUID(), @i4, 'OVERALL', 82, 'Frontend 기술 전반에 대한 우수한 이해도를 보여줍니다.');

-- 면접5 점수
INSERT INTO interview_score (score_id, interview_id, score_type, score, evaludation) VALUES
(UUID(), @i5, 'RESPONSE_ACCURACY', 65, 'DevOps 개념은 알고 있으나 실무 경험 기반 답변이 부족합니다.'),
(UUID(), @i5, 'SPEAKING_PACE', 70, '긴장한 듯 답변 속도가 일정하지 않습니다.'),
(UUID(), @i5, 'VOCABULARY_QUALITY', 68, '인프라 관련 용어 사용이 다소 부정확합니다.'),
(UUID(), @i5, 'OVERALL', 67, 'DevOps 기초 개념 정리와 실무 경험 보강이 필요합니다.');

-- 면접6 점수
INSERT INTO interview_score (score_id, interview_id, score_type, score, evaludation) VALUES
(UUID(), @i6, 'PRONUNCIATION_ACCURACY', 82, '기술 용어 발음이 정확하고 명확합니다.'),
(UUID(), @i6, 'RESPONSE_ACCURACY', 86, 'K8s 운영 경험을 기반으로 한 구체적 답변이 좋았습니다.'),
(UUID(), @i6, 'OVERALL', 84, 'Kubernetes 관련 깊이 있는 지식을 보여주었습니다.');

-- 면접7 점수
INSERT INTO interview_score (score_id, interview_id, score_type, score, evaludation) VALUES
(UUID(), @i7, 'RESPONSE_ACCURACY', 76, 'SQL 최적화 지식은 있으나 실행 계획 분석 경험이 부족합니다.'),
(UUID(), @i7, 'VOCABULARY_QUALITY', 80, 'DB 관련 전문 용어를 정확히 사용합니다.'),
(UUID(), @i7, 'OVERALL', 78, 'SQL 최적화 기본기는 갖추고 있으며 실무 경험이 더해지면 좋겠습니다.');

-- 면접8 점수
INSERT INTO interview_score (score_id, interview_id, score_type, score, evaludation) VALUES
(UUID(), @i8, 'PRONUNCIATION_ACCURACY', 88, '전문 용어를 자신있게 발음하며 전달력이 뛰어납니다.'),
(UUID(), @i8, 'RESPONSE_ACCURACY', 92, '인덱스 설계와 파티셔닝에 대한 답변이 탁월했습니다.'),
(UUID(), @i8, 'SPEAKING_PACE', 85, '적절한 속도와 강약 조절이 좋았습니다.'),
(UUID(), @i8, 'VOCABULARY_QUALITY', 90, 'DBA 전문 용어를 정확히 사용하며 설명이 명확합니다.'),
(UUID(), @i8, 'OVERALL', 91, '뛰어난 DB 지식과 실무 경험이 돋보이는 면접이었습니다.');

-- 면접9 점수
INSERT INTO interview_score (score_id, interview_id, score_type, score, evaludation) VALUES
(UUID(), @i9, 'RESPONSE_ACCURACY', 87, 'JPA 활용 능력이 뛰어나고 N+1 해결 방법을 정확히 설명했습니다.'),
(UUID(), @i9, 'VOCABULARY_QUALITY', 84, 'ORM 관련 용어를 정확히 사용합니다.'),
(UUID(), @i9, 'OVERALL', 86, 'JPA와 QueryDSL 활용에 대한 높은 숙련도를 보여줍니다.');

-- 면접10 점수
INSERT INTO interview_score (score_id, interview_id, score_type, score, evaludation) VALUES
(UUID(), @i10, 'PRONUNCIATION_ACCURACY', 80, '영어 기술 용어 발음이 양호합니다.'),
(UUID(), @i10, 'RESPONSE_ACCURACY', 85, 'Redis 캐싱 전략과 클러스터 구성에 대한 이해가 깊습니다.'),
(UUID(), @i10, 'SPEAKING_PACE', 78, '전반적으로 적절하나 복잡한 내용에서 약간 빨라집니다.'),
(UUID(), @i10, 'OVERALL', 83, 'Redis 실무 활용에 대한 우수한 이해도를 보여줍니다.');

-- 면접11 점수
INSERT INTO interview_score (score_id, interview_id, score_type, score, evaludation) VALUES
(UUID(), @i11, 'RESPONSE_ACCURACY', 74, 'ML 기본 개념은 알지만 실무 경험이 부족합니다.'),
(UUID(), @i11, 'VOCABULARY_QUALITY', 76, 'ML 용어 사용이 적절합니다.'),
(UUID(), @i11, 'OVERALL', 75, 'ML 기초 지식은 갖추고 있으며 프로젝트 경험이 더 필요합니다.');

-- 면접12 점수
INSERT INTO interview_score (score_id, interview_id, score_type, score, evaludation) VALUES
(UUID(), @i12, 'PRONUNCIATION_ACCURACY', 82, '기술 발표 경험이 느껴지는 명확한 전달입니다.'),
(UUID(), @i12, 'RESPONSE_ACCURACY', 88, '모델 서빙과 MLOps에 대한 실무 지식이 인상적입니다.'),
(UUID(), @i12, 'SPEAKING_PACE', 80, '차분하고 일정한 속도로 답변했습니다.'),
(UUID(), @i12, 'OVERALL', 85, '모델 배포와 운영에 대한 높은 이해도를 보여줍니다.');

-- 면접13 점수
INSERT INTO interview_score (score_id, interview_id, score_type, score, evaludation) VALUES
(UUID(), @i13, 'RESPONSE_ACCURACY', 90, 'MSA 아키텍처 경험이 풍부하고 패턴 활용이 적절합니다.'),
(UUID(), @i13, 'VOCABULARY_QUALITY', 87, '분산 시스템 용어를 정확히 사용합니다.'),
(UUID(), @i13, 'OVERALL', 89, 'MSA 설계와 트러블슈팅 경험이 돋보이는 우수한 면접입니다.');

-- 면접14 점수
INSERT INTO interview_score (score_id, interview_id, score_type, score, evaludation) VALUES
(UUID(), @i14, 'RESPONSE_ACCURACY', 79, 'CSS 레이아웃에 대한 이해가 좋으나 최신 기법 학습이 필요합니다.'),
(UUID(), @i14, 'SPEAKING_PACE', 75, '답변 속도는 적절하나 자신감이 부족합니다.'),
(UUID(), @i14, 'OVERALL', 77, 'CSS 기본기는 좋으며 실무 프로젝트 경험이 더해지면 좋겠습니다.');

-- 면접15 점수
INSERT INTO interview_score (score_id, interview_id, score_type, score, evaludation) VALUES
(UUID(), @i15, 'PRONUNCIATION_ACCURACY', 83, '기술 용어를 명확히 발음합니다.'),
(UUID(), @i15, 'RESPONSE_ACCURACY', 86, 'Next.js의 렌더링 전략에 대한 이해가 정확합니다.'),
(UUID(), @i15, 'OVERALL', 84, 'Next.js 프레임워크에 대한 높은 이해도를 보여줍니다.');

-- 면접16 점수
INSERT INTO interview_score (score_id, interview_id, score_type, score, evaludation) VALUES
(UUID(), @i16, 'RESPONSE_ACCURACY', 88, '보안 개념과 인증 메커니즘에 대한 이해가 깊습니다.'),
(UUID(), @i16, 'SPEAKING_PACE', 82, '체계적이고 차분한 답변이 좋았습니다.'),
(UUID(), @i16, 'VOCABULARY_QUALITY', 85, '보안 관련 용어를 정확히 사용합니다.'),
(UUID(), @i16, 'OVERALL', 86, 'Spring Security와 인증 전반에 대한 우수한 이해도입니다.');

-- 면접17 점수
INSERT INTO interview_score (score_id, interview_id, score_type, score, evaludation) VALUES
(UUID(), @i17, 'PRONUNCIATION_ACCURACY', 84, '기술 용어 발음이 정확합니다.'),
(UUID(), @i17, 'RESPONSE_ACCURACY', 87, '동시성 제어 메커니즘에 대한 실무 경험이 돋보입니다.'),
(UUID(), @i17, 'SPEAKING_PACE', 80, '적절한 속도로 답변했습니다.'),
(UUID(), @i17, 'VOCABULARY_QUALITY', 83, 'Java 동시성 관련 용어 사용이 정확합니다.'),
(UUID(), @i17, 'OVERALL', 85, '동시성 제어에 대한 깊은 이해와 실무 감각이 뛰어납니다.');

-- ================================================
-- 면접 자료 (면접당 1~2개)
-- ================================================
INSERT INTO interview_material (material_id, interview_id, material_type, file_path, created_at) VALUES
(UUID(), @i1,  'VIDEO', CONCAT('s3://bucket/interviews/', @i1, '/video.mp4'),           '2026-01-11 10:50:00'),
(UUID(), @i1,  'TEXT',  CONCAT('s3://bucket/interviews/', @i1, '/transcript.txt'),       '2026-01-11 10:52:00'),
(UUID(), @i2,  'VIDEO', CONCAT('s3://bucket/interviews/', @i2, '/video.mp4'),           '2026-01-13 14:30:00'),
(UUID(), @i2,  'TEXT',  CONCAT('s3://bucket/interviews/', @i2, '/transcript.txt'),       '2026-01-13 14:32:00'),
(UUID(), @i3,  'VIDEO', CONCAT('s3://bucket/interviews/', @i3, '/video.mp4'),           '2026-01-16 11:20:00'),
(UUID(), @i4,  'VIDEO', CONCAT('s3://bucket/interviews/', @i4, '/video.mp4'),           '2026-01-18 16:25:00'),
(UUID(), @i4,  'TEXT',  CONCAT('s3://bucket/interviews/', @i4, '/transcript.txt'),       '2026-01-18 16:27:00'),
(UUID(), @i5,  'VIDEO', CONCAT('s3://bucket/interviews/', @i5, '/video.mp4'),           '2026-01-21 10:00:00'),
(UUID(), @i6,  'VIDEO', CONCAT('s3://bucket/interviews/', @i6, '/video.mp4'),           '2026-01-23 10:25:00'),
(UUID(), @i6,  'TEXT',  CONCAT('s3://bucket/interviews/', @i6, '/transcript.txt'),       '2026-01-23 10:27:00'),
(UUID(), @i7,  'VIDEO', CONCAT('s3://bucket/interviews/', @i7, '/video.mp4'),           '2026-01-26 15:00:00'),
(UUID(), @i8,  'VIDEO', CONCAT('s3://bucket/interviews/', @i8, '/video.mp4'),           '2026-01-28 11:30:00'),
(UUID(), @i8,  'TEXT',  CONCAT('s3://bucket/interviews/', @i8, '/transcript.txt'),       '2026-01-28 11:32:00'),
(UUID(), @i9,  'VIDEO', CONCAT('s3://bucket/interviews/', @i9, '/video.mp4'),           '2026-01-13 11:25:00'),
(UUID(), @i9,  'TEXT',  CONCAT('s3://bucket/interviews/', @i9, '/transcript.txt'),       '2026-01-13 11:27:00'),
(UUID(), @i10, 'VIDEO', CONCAT('s3://bucket/interviews/', @i10, '/video.mp4'),          '2026-01-15 15:25:00'),
(UUID(), @i11, 'VIDEO', CONCAT('s3://bucket/interviews/', @i11, '/video.mp4'),          '2026-01-19 10:20:00'),
(UUID(), @i12, 'VIDEO', CONCAT('s3://bucket/interviews/', @i12, '/video.mp4'),          '2026-01-21 14:25:00'),
(UUID(), @i12, 'TEXT',  CONCAT('s3://bucket/interviews/', @i12, '/transcript.txt'),      '2026-01-21 14:27:00'),
(UUID(), @i13, 'VIDEO', CONCAT('s3://bucket/interviews/', @i13, '/video.mp4'),          '2026-01-24 09:20:00'),
(UUID(), @i14, 'VIDEO', CONCAT('s3://bucket/interviews/', @i14, '/video.mp4'),          '2026-01-15 14:25:00'),
(UUID(), @i14, 'TEXT',  CONCAT('s3://bucket/interviews/', @i14, '/transcript.txt'),      '2026-01-15 14:27:00'),
(UUID(), @i15, 'VIDEO', CONCAT('s3://bucket/interviews/', @i15, '/video.mp4'),          '2026-01-17 11:20:00'),
(UUID(), @i16, 'VIDEO', CONCAT('s3://bucket/interviews/', @i16, '/video.mp4'),          '2026-01-22 10:25:00'),
(UUID(), @i16, 'TEXT',  CONCAT('s3://bucket/interviews/', @i16, '/transcript.txt'),      '2026-01-22 10:27:00'),
(UUID(), @i17, 'VIDEO', CONCAT('s3://bucket/interviews/', @i17, '/video.mp4'),          '2026-01-25 15:25:00'),
(UUID(), @i17, 'TEXT',  CONCAT('s3://bucket/interviews/', @i17, '/transcript.txt'),      '2026-01-25 15:27:00');
