

-- 자식 테이블부터 순서대로 삭제
DROP TABLE IF EXISTS interview_material;
DROP TABLE IF EXISTS interview_score;
DROP TABLE IF EXISTS interview_question;
DROP TABLE IF EXISTS interview;
DROP TABLE IF EXISTS pre_question;
DROP TABLE IF EXISTS setting_skill;
DROP TABLE IF EXISTS interview_setting;
DROP TABLE IF EXISTS `user`;
DROP TABLE IF EXISTS skill;

-- 외래 키 제약조건 체크 재활성화
SET FOREIGN_KEY_CHECKS = 1;

-- 테이블 생성 (부모 테이블부터)

-- 사용자 테이블
CREATE TABLE `user` (
                         user_id CHAR(36) NOT NULL,
                         email VARCHAR(255) NOT NULL,
                         password VARCHAR(255) NOT NULL,
                         name VARCHAR(100) NOT NULL,
                         created_at DATETIME NOT NULL,
                         oauth VARCHAR(20) NULL,
                         PRIMARY KEY (user_id)
);

-- 면접 설정 테이블
CREATE TABLE interview_setting (
                                    setting_id CHAR(36) NOT NULL,
                                    user_id CHAR(36) NOT NULL,
                                    question_count INT NOT NULL,
                                    interviewer_style VARCHAR(20) NOT NULL,
                                    interviewer_gender VARCHAR(20) NOT NULL,
                                    interviewer_appearance VARCHAR(20) NOT NULL,
                                    created_at DATETIME NOT NULL,
                                    resume_uri VARCHAR(255) NULL,
                                    position VARCHAR(20) NULL,
                                    PRIMARY KEY (setting_id),
                                    FOREIGN KEY (user_id) REFERENCES `user` (user_id) ON DELETE CASCADE
);

-- 면접 테이블
CREATE TABLE interview (
                            interview_id CHAR(36) NOT NULL,
                            setting_id CHAR(36) NOT NULL,
                            duration BIGINT NULL,
                            created_at DATETIME NULL,
                            ai_overall_review TEXT NULL,
                            interview_name VARCHAR(255) NULL,
                            PRIMARY KEY (interview_id),
                            FOREIGN KEY (setting_id) REFERENCES interview_setting (setting_id) ON DELETE CASCADE,
                            UNIQUE KEY unique_setting (setting_id)
);

-- 사전 질문 테이블
CREATE TABLE pre_question (
                               pre_question_id CHAR(36) NOT NULL,
                               setting_id CHAR(36) NOT NULL,
                               question TEXT NOT NULL,
                               answer TEXT NOT NULL,
                               PRIMARY KEY (pre_question_id),
                               FOREIGN KEY (setting_id) REFERENCES interview_setting (setting_id) ON DELETE CASCADE
);

-- 면접 자료 테이블
CREATE TABLE interview_material (
                                     material_id CHAR(36) NOT NULL,
                                     interview_id CHAR(36) NOT NULL,
                                     material_type VARCHAR(20) NOT NULL,
                                     file_path VARCHAR(255) NOT NULL,
                                     created_at DATETIME NOT NULL,
                                     PRIMARY KEY (material_id),
                                     FOREIGN KEY (interview_id) REFERENCES interview (interview_id) ON DELETE CASCADE
);

-- 면접 점수 테이블
CREATE TABLE interview_score (
                                  score_id CHAR(36) NOT NULL,
                                  interview_id CHAR(36) NOT NULL,
                                  score_type VARCHAR(30) NULL,
                                  score INT NULL,
                                  evaludation TEXT NULL,
                                  PRIMARY KEY (score_id),
                                  FOREIGN KEY (interview_id) REFERENCES interview (interview_id) ON DELETE CASCADE
);

-- 면접 질문 테이블
CREATE TABLE interview_question (
                                     question_id CHAR(36) NOT NULL,
                                     interview_id CHAR(36) NOT NULL,
                                     question TEXT NOT NULL,
                                     answer TEXT NULL,
                                     sequence INTEGER NOT NULL,
                                     created_at DATETIME NULL,
                                     feedback TEXT NOT NULL,
                                     PRIMARY KEY (question_id),
                                     FOREIGN KEY (interview_id) REFERENCES interview (interview_id) ON DELETE CASCADE
);

-- 스킬 테이블
CREATE TABLE skill (
                        skill_id CHAR(36) NOT NULL,
                        skill VARCHAR(100) NOT NULL,
                        PRIMARY KEY (skill_id),
                        UNIQUE KEY unique_skill_name (skill)
);

-- 설정-스킬 중간 테이블
CREATE TABLE setting_skill (
                                setting_id CHAR(36) NOT NULL,
                                skill_id CHAR(36) NOT NULL,
                                PRIMARY KEY (setting_id, skill_id),
                                FOREIGN KEY (setting_id) REFERENCES interview_setting (setting_id) ON DELETE CASCADE,
                                FOREIGN KEY (skill_id) REFERENCES skill (skill_id) ON DELETE CASCADE
);