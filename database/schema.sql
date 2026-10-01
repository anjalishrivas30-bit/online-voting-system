-- ============================================================
--  Online Voting System - MySQL 8 schema + seed data
--  OPTIONAL: the application creates these tables by itself (Hibernate ddl-auto=update)
--  and loads the same seed data from src/main/resources/data.sql.
--  Use this script if you want to create the database manually / show the schema in your viva:
--      mysql -u root -p < database/schema.sql
-- ============================================================
CREATE DATABASE IF NOT EXISTS voting_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE voting_db;

CREATE TABLE IF NOT EXISTS roles (
    id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    name  VARCHAR(30) NOT NULL UNIQUE                       -- ROLE_ADMIN / ROLE_VOTER
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS users (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name        VARCHAR(100) NOT NULL,
    email            VARCHAR(150) NOT NULL UNIQUE,            -- login name
    voter_id         VARCHAR(30)  NOT NULL UNIQUE,
    date_of_birth    DATE         NOT NULL,
    password         VARCHAR(100) NOT NULL,                   -- BCrypt hash
    role_id          BIGINT       NOT NULL,
    status           VARCHAR(20)  NOT NULL DEFAULT 'PENDING', -- PENDING / APPROVED / REJECTED
    failed_attempts  INT          NOT NULL DEFAULT 0,
    account_locked   TINYINT(1)   NOT NULL DEFAULT 0,
    lock_time        DATETIME(6)  NULL,
    created_at       DATETIME(6)  NOT NULL,
    CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES roles (id),
    INDEX idx_users_status (status)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS elections (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    title        VARCHAR(200)  NOT NULL,
    description  VARCHAR(1000) NULL,
    start_time   DATETIME(6)   NOT NULL,
    end_time     DATETIME(6)   NOT NULL,
    status       VARCHAR(20)   NOT NULL DEFAULT 'UPCOMING',   -- UPCOMING / ACTIVE / CLOSED
    created_at   DATETIME(6)   NOT NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS candidates (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(100)  NOT NULL,
    party           VARCHAR(100)  NOT NULL,
    position_title  VARCHAR(100)  NOT NULL,
    description     VARCHAR(1000) NULL,
    election_id     BIGINT        NOT NULL,
    CONSTRAINT fk_candidates_election FOREIGN KEY (election_id) REFERENCES elections (id),
    INDEX idx_candidates_election (election_id)
) ENGINE=InnoDB;

-- BALLOT SECRECY: no voter column and no timestamp in this table.
CREATE TABLE IF NOT EXISTS votes (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    election_id   BIGINT NOT NULL,
    candidate_id  BIGINT NOT NULL,
    CONSTRAINT fk_votes_election  FOREIGN KEY (election_id)  REFERENCES elections (id),
    CONSTRAINT fk_votes_candidate FOREIGN KEY (candidate_id) REFERENCES candidates (id),
    INDEX idx_votes_election (election_id),
    INDEX idx_votes_candidate (candidate_id)
) ENGINE=InnoDB;

-- The "has voted" record. UNIQUE(voter_id, election_id) = one vote per voter per election, enforced by MySQL.
CREATE TABLE IF NOT EXISTS voter_election_status (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    voter_id     BIGINT      NOT NULL,
    election_id  BIGINT      NOT NULL,
    voted_at     DATETIME(6) NOT NULL,
    CONSTRAINT fk_ves_voter    FOREIGN KEY (voter_id)    REFERENCES users (id),
    CONSTRAINT fk_ves_election FOREIGN KEY (election_id) REFERENCES elections (id),
    CONSTRAINT uk_voter_election UNIQUE (voter_id, election_id),
    INDEX idx_ves_election (election_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS audit_logs (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    actor       VARCHAR(150) NOT NULL,
    action      VARCHAR(60)  NOT NULL,
    details     VARCHAR(500) NULL,
    created_at  DATETIME(6)  NOT NULL,
    INDEX idx_audit_created (created_at)
) ENGINE=InnoDB;

-- Voter request system: a voter submits a request, an admin approves or rejects it with a remark.
CREATE TABLE IF NOT EXISTS voter_requests (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    voter_id        BIGINT        NOT NULL,
    request_type    VARCHAR(40)   NOT NULL,                    -- PROFILE_CORRECTION / VOTER_INFO_UPDATE / OTHER
    description     VARCHAR(1000) NOT NULL,
    status          VARCHAR(20)   NOT NULL DEFAULT 'PENDING',  -- PENDING / APPROVED / REJECTED
    admin_remark    VARCHAR(500)  NULL,
    created_at      DATETIME(6)   NOT NULL,
    decided_at      DATETIME(6)   NULL,
    voter_notified  TINYINT(1)    NOT NULL DEFAULT 1,          -- 0 = decision not yet seen by the voter (in-app notification)
    CONSTRAINT fk_requests_voter FOREIGN KEY (voter_id) REFERENCES users (id),
    INDEX idx_requests_status (status),
    INDEX idx_requests_voter (voter_id)
) ENGINE=InnoDB;

-- ===================== SEED DATA =====================
-- ============================================================
--  Seed data (runs automatically after Hibernate creates the tables).
--  INSERT IGNORE makes it safe to run on every startup.
--  Logins:  admin@voting.com / Admin@123
--           ravi@example.com, priya@example.com (approved), amit@example.com (pending) / Voter@123
-- ============================================================
INSERT IGNORE INTO roles (id, name) VALUES (1, 'ROLE_ADMIN'), (2, 'ROLE_VOTER');

INSERT IGNORE INTO users (id, full_name, email, voter_id, date_of_birth, password, role_id, status, failed_attempts, account_locked, created_at) VALUES
(1, 'System Administrator', 'admin@voting.com', 'ADMIN0001', '1990-01-01', '$2b$10$Sj8Is.XVrNbcOLmvnJggyeZ1xhcYNYlsNis.Tl.8SuUf.p6Go9Ssm', 1, 'APPROVED', 0, 0, NOW()),
(2, 'Ravi Kumar',  'ravi@example.com',  'VOT10001', '2000-05-14', '$2b$10$b4FW1QNGJLlf1yyt1wMx9uLzoL0CY7p82tALTnky61/S.ggOL0ToO', 2, 'APPROVED', 0, 0, NOW()),
(3, 'Priya Sharma','priya@example.com', 'VOT10002', '1999-11-02', '$2b$10$b4FW1QNGJLlf1yyt1wMx9uLzoL0CY7p82tALTnky61/S.ggOL0ToO', 2, 'APPROVED', 0, 0, NOW()),
(4, 'Amit Verma',  'amit@example.com',  'VOT10003', '2001-03-21', '$2b$10$b4FW1QNGJLlf1yyt1wMx9uLzoL0CY7p82tALTnky61/S.ggOL0ToO', 2, 'PENDING',  0, 0, NOW());

INSERT IGNORE INTO elections (id, title, description, start_time, end_time, status, created_at) VALUES
(1, 'Student Council President Election 2026',
    'Vote for the student who will lead the council this academic year. One vote per registered voter.',
    DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 30 DAY), 'ACTIVE', NOW());

INSERT IGNORE INTO candidates (id, name, party, position_title, description, election_id) VALUES
(1, 'Anita Desai',  'Progress Alliance', 'President', 'Final-year engineering student focused on campus labs and placements.', 1),
(2, 'Rahul Mehta',  'Unity Front',       'President', 'Sports captain promoting student welfare and inter-college events.', 1),
(3, 'Sara Khan',    'Green Campus',      'President', 'Environmental club lead working on sustainability and open-source culture.', 1);

INSERT IGNORE INTO voter_requests (id, voter_id, request_type, description, status, admin_remark, created_at, decided_at, voter_notified) VALUES
(1, 2, 'PROFILE_CORRECTION', 'My name is spelled incorrectly on my voter profile. Please correct it.', 'PENDING', NULL, NOW(), NULL, 1),
(2, 3, 'VOTER_INFO_UPDATE', 'Please update my contact information in the voter records.', 'APPROVED', 'Verified and updated.', DATE_SUB(NOW(), INTERVAL 1 DAY), NOW(), 0);
