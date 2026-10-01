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
