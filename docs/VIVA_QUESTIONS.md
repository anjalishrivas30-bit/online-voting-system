# 10 Likely Viva Questions and Answers

**1. How do you make sure a voter cannot vote twice?**
Three layers. (1) `VoteService` checks `existsByVoterIdAndElectionId` first. (2) The table `voter_election_status` has a
UNIQUE constraint on `(voter_id, election_id)`, so even two simultaneous requests cannot both insert; the second gets a
`DataIntegrityViolationException`, which I convert to "already voted". (3) `castVote` is `@Transactional`, so the has-voted row
and the vote row are saved together or rolled back together. The confirm button is also disabled after the first click.

**2. How is ballot secrecy achieved?**
The `votes` table has only `election_id` and `candidate_id` - no voter id and no timestamp. Who voted is stored in a different
table (`voter_election_status`) that has no candidate column. The audit log records "vote cast in election X" but never the choice.
So nobody can link a voter to a candidate.

**3. How does Spring Security authenticate a user here?**
The login form posts to `/login`. Spring Security calls my `CustomUserDetailsService`, which loads the user by email from MySQL and
returns a `UserDetails` with the BCrypt hash and the role. `DaoAuthenticationProvider` hashes the submitted password and compares
it with the stored hash. On success my `LoginSuccessHandler` redirects by role; on failure `LoginFailureHandler` counts the attempt.

**4. Why BCrypt and not SHA-256 or plain text?**
BCrypt is a slow, salted, adaptive hash. Each password gets its own random salt, so equal passwords give different hashes and rainbow
tables do not work; the work factor makes brute force expensive. Plain text or fast hashes are unsafe if the database leaks.

**5. What is CSRF and how did you prevent it?**
CSRF tricks a logged-in user's browser into sending an unwanted request to our site. Spring Security generates a random token per
session; every POST form includes it as a hidden field (`${_csrf.token}`) and the server rejects requests without a valid token.
Logout is also a POST with the token.

**6. How is role-based access implemented?**
Two layers. `SecurityConfig` has URL rules: `/admin/**` needs `ROLE_ADMIN`, `/voter/**` needs `ROLE_VOTER`. The controllers also have
`@PreAuthorize("hasRole('ADMIN')")` (enabled by `@EnableMethodSecurity`) as defense in depth. A voter opening an admin URL gets the custom 403 page.

**7. How does the account lockout work?**
`LoginFailureHandler` calls `LoginAttemptService.recordFailure`, which increments `failed_attempts`. At 5 the user is marked
`account_locked` with a `lock_time`. `CustomUserDetailsService` passes `accountNonLocked=false`, so Spring refuses the login.
After 15 minutes the account unlocks automatically. A successful login resets the counter.

**8. How does "auto-status by time" work?**
`Election.computeStatus(now)` returns UPCOMING before the start, ACTIVE between start and end, CLOSED at or after the end.
Business rules (voting, results) always use this function with the current clock, so they are always exact. A scheduled job
(`@Scheduled`, every minute) also updates the stored status column so the database stays in sync.

**9. How do you prevent SQL injection and XSS?**
SQL injection: I only use Spring Data derived queries and a JPQL query with a named parameter; Hibernate uses prepared statements,
so user input is never concatenated into SQL. XSS: every value shown in JSP is printed with `<c:out>`, which HTML-escapes it, and
the inputs are validated with Jakarta Validation on the server.

**10. Why is voting `@Transactional` and what does it guarantee?**
A vote needs several writes: the has-voted row, the vote row and the audit entry. `@Transactional` makes them atomic (all or nothing).
If anything fails - for example the unique constraint - the whole transaction rolls back, so we never get a counted vote without a
has-voted record or the opposite. `saveAndFlush` forces the insert inside the method so constraint violations surface immediately.

**11. How does the voter request feature work?**
`VoterRequest` is an entity with a many-to-one link to `User`. A voter submits it (status PENDING). The admin opens the request,
adds a remark and approves or rejects it through `RequestService.decide()`. Rules: only a pending request can be decided, and a rejection
needs a remark. The `voter_notified` flag is set to false so the voter sees an in-app notification on the dashboard and a badge in the sidebar.

**12. How does the eligibility check work?**
`VoteService.describe()` computes the state on the server from the election time and the voter record: upcoming -> "Voting has not started yet.",
completed -> "Voting has ended.", active and already in `voter_election_status` -> "You have already voted.", otherwise "Vote Now".
`castVote()` repeats the same rules, so changing the URL or the HTML cannot bypass them.

### Bonus questions
- **Why JSP and WAR packaging?** Embedded Tomcat can only render JSPs from a WAR/exploded webapp folder, so the project uses `packaging=war`.
- **Why must FORWARD dispatches be permitted?** Spring Security 6 also authorizes forwards; JSP views are rendered by forwarding to `/WEB-INF/views/*.jsp`.
- **What would you improve?** OTP/email verification, face/ID verification and a blockchain-backed audit trail (see the report).
