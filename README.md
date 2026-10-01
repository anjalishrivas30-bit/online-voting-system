# Online Voting System

A secure online election system built as a Java developer internship project.

**Stack:** Java 17 · Spring Boot 3.2 · Spring MVC · Spring Security 6 · Spring Data JPA/Hibernate · MySQL 8 · JSP + JSTL · Bootstrap 5

## Features
**Admin** (sidebar: Dashboard, Manage Voters, Manage Candidates, Manage Elections, Voter Requests, Results, Audit Log, Logout)
- Dashboard cards: total voters, candidates, elections, active elections, pending requests
- Approve / reject voter registrations; search voters by name, email or voter ID
- Create, edit, end, delete elections (status upcoming / active / completed is automatic); filter by status
- Add, edit, delete candidates and assign them to an election; search candidates
- Voter requests: view details, approve or reject with a remark; filter by pending / approved / rejected
- Results: live count for running elections, final result with winner and turnout
- Audit log of logins, votes cast (never the choice) and admin actions

**Voter** (sidebar: Dashboard, Elections, My Requests, Submit Request, My Profile, Logout)
- Dashboard: welcome, profile, active and upcoming elections, request status, voting status, quick actions
- Smart eligibility check per election: "Voting has not started yet." / "Vote Now" / "You have already voted." / "Voting has ended."
- Vote flow: elections → candidates → select → confirm dialog → submit → success message
- **One voter = one vote per election**, enforced in the service AND by a UNIQUE (voter_id, election_id) key in MySQL
- Submit requests (profile correction, information update, other) and track the status and admin remark
- In-app notifications: request submitted / approved / rejected, vote submitted, new pending request for admin
- Results of an election only after it is completed

**Security:** BCrypt passwords, CSRF on every form, URL rules (`/admin/**` = ADMIN, `/voter/**` = VOTER) plus `@PreAuthorize`, account lockout after 5 wrong passwords, ballot secrecy, transactional voting, validation, XSS-safe output, custom 403/404 pages.

---

## STEP-BY-STEP: how to run (Windows / Mac / Linux)

### Step 1 - Install the tools (once)
| Tool | Version | Check command |
|------|---------|---------------|
| JDK | 17 or newer | `java -version` |
| Maven | 3.8 or newer | `mvn -version` |
| MySQL Server | 8.x | `mysql --version` |

### Step 2 - Start MySQL
Make sure the MySQL service is running (Windows: `net start MySQL80` in an Administrator terminal, or start it in "Services").
Test it: `mysql -u root -p` and type your password. If this login fails, the app cannot connect either.

### Step 3 - Unzip the project
Unzip `online-voting-system.zip`. You get a folder with `pom.xml` inside. Open a terminal **inside that folder**.

### Step 4 - Put your MySQL password in the project
Open `src/main/resources/application.properties` and change this line:
```
spring.datasource.password=root
```
to your real MySQL root password (and `spring.datasource.username` if you do not use `root`).
The database name stays **voting_db** and is created automatically.

### Step 5 - Run
```
mvn clean spring-boot:run
```
The first run downloads libraries (needs internet, takes a few minutes). Wait until you see:
```
Started OnlineVotingApplication in ... seconds
```

### Step 6 - Open the application
Open **http://localhost:8080** in your browser.

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@voting.com | Admin@123 |
| Voter (approved) | ravi@example.com | Voter@123 |
| Voter (approved) | priya@example.com | Voter@123 |
| Voter (pending approval) | amit@example.com | Voter@123 |

Sign in as admin → you land on the **Admin Dashboard**. Sign in as a voter → **Voter Dashboard**.

### Step 7 - Run the unit tests (optional, no database needed)
```
mvn test
```

### Build a runnable file (optional)
```
mvn clean package
java -jar target/online-voting-system-1.0.0.war
```

---

## Try the full flow (10 minutes)
1. **Voter request:** sign in as `ravi@example.com` → *Submit Request* → choose a type, write a description → submit. The request shows as *Pending* in *My Requests*.
2. **Admin approves:** sign out, sign in as admin. The dashboard shows "pending requests" and the sidebar shows a red badge. Open *Voter Requests* → *Review* → write a remark → *Approve*.
3. **Voter sees the update:** sign in as Ravi again → the dashboard shows a notification and the request is *Approved* with the remark.
4. **Voting:** as Ravi → *Elections* → *Vote Now* → select a candidate → *Review my choice* → confirm → "Vote successfully submitted".
5. **Duplicate vote blocked:** the election now shows "You have already voted." Opening the vote URL again gives "You have already voted in this election."
6. **Another voter:** sign in as `priya@example.com` and vote in the same election: allowed (each voter has one vote).
7. **Pending voter:** sign in as `amit@example.com` → "waiting for approval", cannot vote. Admin approves him in *Manage Voters*.
8. **Results:** admin → *Results* → *View live count*. Use *Manage Elections* → *End now*, then open the results: the winner is shown. Voters can now open the results too.
9. **Security check:** while signed in as a voter, open `http://localhost:8080/admin/dashboard` → custom 403 page.

## Important URLs
| URL | Who | Purpose |
|-----|-----|---------|
| `/login`, `/register` | everyone | sign in / register as voter |
| `/admin/dashboard` | admin | summary cards |
| `/admin/voters`, `/admin/candidates`, `/admin/elections` | admin | manage (with search/filter) |
| `/admin/requests`, `/admin/requests/{id}` | admin | voter requests |
| `/admin/results`, `/admin/elections/{id}/results` | admin | results (live or final) |
| `/admin/audit` | admin | audit log |
| `/voter/home` | voter | voter dashboard |
| `/voter/elections` | voter | elections with eligibility |
| `/voter/elections/{id}/vote` | voter | ballot |
| `/voter/requests`, `/voter/requests/new` | voter | my requests / submit request |
| `/voter/profile` | voter | my profile |

## Database (MySQL `voting_db`)
Tables: `users`, `roles`, `elections`, `candidates`, `votes`, `voter_election_status` (UNIQUE voter_id + election_id), `voter_requests`, `audit_logs`.
Hibernate creates/updates them automatically; `src/main/resources/data.sql` inserts the demo data.
`database/schema.sql` is the same structure as plain SQL (optional, for your report/viva).
If you ran an older version of this project, nothing is lost: only the new `voter_requests` table is added.

## Project structure
```
src/main/java/com/internship/voting
  config/       SecurityConfig, DataInitializer (guarantees the admin account), GlobalModelAttributes (sidebar data)
  security/     CustomUserDetailsService, LoginAttemptService, login success/failure handlers
  model/        User, Role, Election, Candidate, Vote, VoterElectionStatus, VoterRequest, AuditLog + enums
  repository/   Spring Data JPA repositories
  service/      VoteService, ResultService, ElectionService, CandidateService, UserService, RequestService, AuditService
  controller/   Auth, Home, AdminDashboard/Voter/Election/Candidate/Request/Result, Voter, VoterRequest, CustomError
  dto/          forms and result objects
  exception/    BusinessException, ResourceNotFoundException, GlobalExceptionHandler
src/main/webapp/WEB-INF/views   JSP pages (common/header.jsp = sidebar layout)
src/main/resources              application.properties, data.sql, static/css/style.css
src/test/java                   VoteServiceTest, RequestServiceTest, ElectionStatusTest
docs/                           PROJECT_REPORT.md, VIVA_QUESTIONS.md
```

## Troubleshooting
| Problem | Fix |
|---------|-----|
| `Access denied for user 'root'` | Wrong MySQL password in `application.properties` |
| `Communications link failure` / `Connection refused` | MySQL is not running (Step 2) or uses another port |
| `Port 8080 was already in use` | Set `server.port=8081` in `application.properties` and open `http://localhost:8081` |
| `Unsupported class file major version` / compiler errors about `release 17` | Install JDK 17+ and make sure `java -version` shows it (also check `JAVA_HOME`) |
| `mvn` is not recognized | Install Maven and add its `bin` folder to PATH, then open a new terminal |
| Cannot log in as admin | Restart the app: `DataInitializer` re-creates and unlocks `admin@voting.com` / `Admin@123` at every start |
| Account locked | Wait 15 minutes (or restart for the admin) |
| Pages look unstyled / no icons | Bootstrap loads from a CDN: the browser needs internet |
| You want a completely fresh database | In MySQL run `DROP DATABASE voting_db;` and start the app again |
| Build fails with a long error | Run `mvn clean spring-boot:run > log.txt 2>&1` and look for `Caused by:` in `log.txt` |
