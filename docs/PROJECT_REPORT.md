# Online Voting System - Project Report

## 1. Abstract
Paper-based and ad-hoc elections are slow to count, hard to audit and easy to dispute. This project is a web-based
voting system where voters register online, are approved by an administrator, and cast exactly one secret vote per
election. Administrators create elections and candidates, and the system counts votes automatically and publishes
results when an election closes. The application is built with Spring Boot, Spring Security, Spring Data JPA, MySQL
and JSP. Security is treated as a core feature: BCrypt password hashing, CSRF protection, role-based access,
account lockout, transactional vote casting, a database-level one-vote rule and strict ballot secrecy.

## 2. Objectives
1. Let eligible voters (18+) register online and be verified by an admin.
2. Let admins manage elections, candidates and voters from one dashboard.
3. Guarantee one vote per voter per election, even under concurrent requests.
4. Keep the ballot secret: nobody can see who voted for whom.
5. Count votes automatically and show results (votes, percentage, winner, turnout) only after the election closes.
6. Keep an audit trail of logins, votes cast (without the choice) and admin actions.
7. Follow secure-coding practice (OWASP): injection, XSS, CSRF, broken access control, brute force.

## 3. Technology
| Layer | Technology |
|-------|-----------|
| Language / framework | Java 17, Spring Boot 3.2 |
| Security | Spring Security 6 (form login, BCrypt, CSRF, method security) |
| Persistence | Spring Data JPA, Hibernate 6, MySQL 8 |
| View | JSP, JSTL (Jakarta), Bootstrap 5 |
| Build / test | Maven, JUnit 5, Mockito, AssertJ |

## 4. Architecture (text diagram)
```
 Browser (JSP pages + Bootstrap)
        |  HTTP (session cookie, CSRF token on every POST)
        v
 +-------------------------------------------------------------+
 |  Spring Security filter chain                               |
 |   CSRF filter -> login filter -> authorization (/admin, /voter)
 +-------------------------------------------------------------+
        v
 +-------------------+     +---------------------------+
 |  Controllers      | --> |  Services (business rules)|
 |  Auth, Voter,     |     |  VoteService, ResultService|
 |  Admin*           |     |  ElectionService, ...     |
 +-------------------+     +---------------------------+
        |  forward to JSP           v
        v                   +---------------------------+
   WEB-INF/views/*.jsp      |  Repositories (Spring Data)|
                            +---------------------------+
                                    v  JPA / Hibernate / JDBC
                            +---------------------------+
                            |  MySQL 8  (voting_db)     |
                            +---------------------------+
```
Layers: **controller** (HTTP, validation results, redirects) → **service** (rules, transactions) → **repository**
(database access) → **model** (entities). DTOs carry form data and result data between layers.

## 5. Database design (ER diagram, text)
```
 roles (1) ------< users (N)
   id PK              id PK
   name UNIQUE        full_name, email UNIQUE, voter_id UNIQUE, date_of_birth
                      password (BCrypt), role_id FK, status
                      failed_attempts, account_locked, lock_time, created_at

 elections (1) ------< candidates (N)
   id PK                 id PK, name, party, position_title, description
   title, description    election_id FK
   start_time, end_time
   status, created_at

 elections (1) ------< votes (N) >------ (1) candidates
                         id PK, election_id FK, candidate_id FK
                         (NO voter column, NO timestamp  -> ballot secrecy)

 users (1) ------< voter_requests (N)
                     id PK, voter_id FK, request_type, description, status,
                     admin_remark, created_at, decided_at, voter_notified

 users (1) ------< voter_election_status (N) >------ (1) elections
                     id PK, voter_id FK, election_id FK, voted_at
                     UNIQUE (voter_id, election_id)   -> one vote per voter per election

 audit_logs:  id PK, actor, action, details, created_at
```
Key point: `votes` records WHAT was chosen, `voter_election_status` records THAT a voter voted. There is no
link between the two tables, so the choice cannot be traced back to a voter.

## 6. Main modules
- **Registration and approval:** validation (Jakarta Validation), age check, unique email/voter ID, BCrypt, admin approval.
- **Authentication:** custom `UserDetailsService`, role redirect, lockout after 5 failures for 15 minutes, session timeout 15 min.
- **Election management:** create/edit/close/delete; status computed from start/end time and refreshed every minute.
- **Candidate management:** add/edit/delete, allowed only while the election is open for changes (not closed, no votes yet).
- **Voting:** ballot → confirmation → cast. One `@Transactional` method saves the has-voted row and the vote together.
- **Counting:** one GROUP BY query, percentage, winner(s)/tie, turnout = votes / approved voters.
- **Voter request system:** a voter submits a request (profile correction, information update, other); an admin approves or rejects it with a remark; the voter sees the new status and an in-app notification.
- **Smart eligibility check:** `VoteService.describe()` decides per election: not started / vote now / already voted / ended / not approved.
- **Search and filter:** admin can search voters and candidates and filter elections and requests by status.
- **Audit log:** logins, failures, lockouts, registrations, votes cast (no choice), admin actions.

## 7. Security measures
| Threat | Protection |
|--------|-----------|
| Stolen database | Passwords stored as salted BCrypt hashes |
| SQL injection | Only JPA/derived queries and bound parameters |
| XSS | All user data printed with `<c:out>` (HTML-escaped) |
| CSRF | Spring Security CSRF token on every form, logout is POST |
| Broken access control | `/admin/**` and `/voter/**` URL rules plus `@PreAuthorize` on controllers |
| Brute force | Lockout after 5 wrong passwords |
| Double voting | App check + unique DB constraint + transaction + disabled submit button |
| Vote tracing | Separate tables, no voter id or timestamp on votes, choice never written to audit log |
| Session theft | HttpOnly cookie, SameSite=Lax, session id never in URL, 15 min timeout, session id changes at login |
| Information leak | Custom 403/404/error pages, stack traces hidden |

## 8. Testing
Unit tests (`mvn test`) cover: successful vote, double vote, closed election, election not started, unapproved voter,
candidate from another election, the eligibility messages, the request workflow (approve, reject needs remark, cannot decide twice), and the auto-status rule (upcoming/active/closed, boundaries).

## 9. Limitations
- Identity is verified manually by an admin (no government ID integration).
- Election times use the server's local time zone.
- Bootstrap is loaded from a CDN.

## 10. Future enhancements
1. **OTP / email verification** at registration and before each vote.
2. **Face or ID verification** (document upload + OCR / liveness check).
3. **Blockchain audit**: publish a hash of every ballot batch to an append-only ledger so results can be verified independently.
4. Receipt codes so voters can verify their vote was counted without revealing the choice.
5. Two-factor authentication for admins, CAPTCHA on login/register.
6. REST API + mobile app, multi-language support, accessibility audit.
7. Result export (PDF/CSV), email notifications, multiple positions per election.

## 11. Conclusion
The system covers the complete election life cycle - registration, approval, voting, counting and auditing - while
keeping the ballot secret and the process secure. It demonstrates layered Spring architecture, Spring Security
configuration, JPA mapping with database constraints, transactions and unit testing.
