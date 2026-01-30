CREATE TABLE IF NOT EXISTS `user` (
    user_id      CHAR(36)     NOT NULL,
    email        VARCHAR(255) NOT NULL,
    password     VARCHAR(255) NOT NULL,
    name         VARCHAR(100) NOT NULL,
    created_at   DATETIME     NOT NULL,
    oauth        VARCHAR(20)  NULL,
    PRIMARY KEY (user_id)
);

CREATE TABLE IF NOT EXISTS interview_setting (
    setting_id             CHAR(36)     NOT NULL,
    user_id                CHAR(36)     NOT NULL,
    question_count         INT          NOT NULL,
    interviewer_tone       VARCHAR(20)  NOT NULL,
    interviewer_gender     VARCHAR(20)  NOT NULL,
    interviewer_appearance VARCHAR(20)  NOT NULL,
    created_at             DATETIME     NOT NULL,
    resume_s3_path         VARCHAR(255) NULL,
    job_type               VARCHAR(20)  NULL,
    PRIMARY KEY (setting_id),
    FOREIGN KEY (user_id) REFERENCES `user` (user_id)
);

CREATE TABLE IF NOT EXISTS interview (
    interview_id      CHAR(36)     NOT NULL,
    setting_id        CHAR(36)     NOT NULL,
    duration          BIGINT       NULL,
    created_at        DATETIME     NULL,
    ai_overall_review TEXT         NULL,
    interview_name    VARCHAR(255) NULL,
    PRIMARY KEY (interview_id),
    FOREIGN KEY (setting_id) REFERENCES interview_setting (setting_id)
);

CREATE TABLE IF NOT EXISTS pre_question (
    pre_question_id  CHAR(36) NOT NULL,
    setting_id       CHAR(36) NOT NULL,
    question         TEXT     NOT NULL,
    answer           TEXT     NOT NULL,
    PRIMARY KEY (pre_question_id),
    FOREIGN KEY (setting_id) REFERENCES interview_setting (setting_id)
);

CREATE TABLE IF NOT EXISTS interview_material (
    material_id   CHAR(36)     NOT NULL,
    interview_id  CHAR(36)     NOT NULL,
    material_type VARCHAR(20)  NOT NULL,
    s3_uri        VARCHAR(255) NOT NULL,
    created_at    DATETIME     NOT NULL,
    PRIMARY KEY (material_id),
    FOREIGN KEY (interview_id) REFERENCES interview (interview_id)
);

CREATE TABLE IF NOT EXISTS interview_score (
    score_id      CHAR(36)    NOT NULL,
    interview_id  CHAR(36)    NOT NULL,
    score_type    VARCHAR(20) NULL,
    score         INT         NULL,
    ai_review     TEXT        NULL,
    PRIMARY KEY (score_id),
    FOREIGN KEY (interview_id) REFERENCES interview (interview_id)
);

CREATE TABLE IF NOT EXISTS interview_question (
    question_id   BIGINT   NOT NULL AUTO_INCREMENT,
    interview_id  CHAR(36) NOT NULL,
    ai_question   TEXT     NOT NULL,
    user_answer   TEXT     NULL,
    created_at    DATETIME NULL,
    answer_time   BIGINT   NULL,
    PRIMARY KEY (question_id),
    FOREIGN KEY (interview_id) REFERENCES interview (interview_id)
);

CREATE TABLE IF NOT EXISTS skill (
    skill_id    BIGINT       NOT NULL AUTO_INCREMENT,
    skill_name  VARCHAR(100) NOT NULL,
    PRIMARY KEY (skill_id)
);

CREATE TABLE IF NOT EXISTS setting_skill (
    setting_id CHAR(36) NOT NULL,
    skill_id   BIGINT   NOT NULL,
    PRIMARY KEY (setting_id, skill_id),
    FOREIGN KEY (setting_id) REFERENCES interview_setting (setting_id),
    FOREIGN KEY (skill_id) REFERENCES skill (skill_id)
);
