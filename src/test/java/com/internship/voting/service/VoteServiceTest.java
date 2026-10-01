package com.internship.voting.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.internship.voting.dto.VoteEligibility;
import com.internship.voting.dto.VoterElectionView;
import com.internship.voting.exception.BusinessException;
import com.internship.voting.model.Candidate;
import com.internship.voting.model.Election;
import com.internship.voting.model.Role;
import com.internship.voting.model.User;
import com.internship.voting.model.Vote;
import com.internship.voting.model.VoterElectionStatus;
import com.internship.voting.model.VoterStatus;
import com.internship.voting.repository.CandidateRepository;
import com.internship.voting.repository.ElectionRepository;
import com.internship.voting.repository.UserRepository;
import com.internship.voting.repository.VoteRepository;
import com.internship.voting.repository.VoterElectionStatusRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the voting rules. Repositories are mocked, so no database is needed.
 * Each test sets up only the stubs that its code path really uses (Mockito strict stubs).
 */
@ExtendWith(MockitoExtension.class)
class VoteServiceTest {

    private static final String EMAIL = "ravi@example.com";
    private static final Long ELECTION_ID = 10L;
    private static final Long CANDIDATE_ID = 100L;

    @Mock private UserRepository userRepository;
    @Mock private ElectionRepository electionRepository;
    @Mock private CandidateRepository candidateRepository;
    @Mock private VoteRepository voteRepository;
    @Mock private VoterElectionStatusRepository voterElectionStatusRepository;
    @Mock private AuditService auditService;

    @InjectMocks private VoteService voteService;

    private User voter;
    private Election election;
    private Candidate candidate;

    @BeforeEach
    void setUp() {
        Role voterRole = new Role(Role.VOTER);

        voter = new User();
        voter.setId(1L);
        voter.setEmail(EMAIL);
        voter.setRole(voterRole);
        voter.setStatus(VoterStatus.APPROVED);

        // An election that is open right now: started yesterday, ends tomorrow
        election = new Election();
        election.setId(ELECTION_ID);
        election.setTitle("Test election");
        election.setStartTime(LocalDateTime.now().minusDays(1));
        election.setEndTime(LocalDateTime.now().plusDays(1));

        candidate = new Candidate();
        candidate.setId(CANDIDATE_ID);
        candidate.setName("Anita");
        candidate.setElection(election);
    }

    @Test
    void castVote_savesVoteAndHasVotedRecord_whenEverythingIsValid() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(voter));
        when(electionRepository.findById(ELECTION_ID)).thenReturn(Optional.of(election));
        when(candidateRepository.findById(CANDIDATE_ID)).thenReturn(Optional.of(candidate));
        when(voterElectionStatusRepository.existsByVoterIdAndElectionId(1L, ELECTION_ID)).thenReturn(false);

        voteService.castVote(EMAIL, ELECTION_ID, CANDIDATE_ID);

        verify(voterElectionStatusRepository).saveAndFlush(any(VoterElectionStatus.class));
        verify(voteRepository).save(any(Vote.class));
        verify(auditService).log(any(), any(), any());
    }

    @Test
    void castVote_rejectsSecondVote_inSameElection() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(voter));
        when(electionRepository.findById(ELECTION_ID)).thenReturn(Optional.of(election));
        when(candidateRepository.findById(CANDIDATE_ID)).thenReturn(Optional.of(candidate));
        when(voterElectionStatusRepository.existsByVoterIdAndElectionId(1L, ELECTION_ID)).thenReturn(true);

        assertThatThrownBy(() -> voteService.castVote(EMAIL, ELECTION_ID, CANDIDATE_ID))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already voted");

        verify(voteRepository, never()).save(any(Vote.class));
    }

    @Test
    void castVote_rejectsVote_whenElectionIsClosed() {
        election.setStartTime(LocalDateTime.now().minusDays(10));
        election.setEndTime(LocalDateTime.now().minusDays(1));   // ended yesterday
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(voter));
        when(electionRepository.findById(ELECTION_ID)).thenReturn(Optional.of(election));

        assertThatThrownBy(() -> voteService.castVote(EMAIL, ELECTION_ID, CANDIDATE_ID))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("ended");

        verify(voteRepository, never()).save(any(Vote.class));
    }

    @Test
    void castVote_rejectsVote_whenElectionHasNotStarted() {
        election.setStartTime(LocalDateTime.now().plusDays(1));
        election.setEndTime(LocalDateTime.now().plusDays(2));
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(voter));
        when(electionRepository.findById(ELECTION_ID)).thenReturn(Optional.of(election));

        assertThatThrownBy(() -> voteService.castVote(EMAIL, ELECTION_ID, CANDIDATE_ID))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("not started");

        verify(voteRepository, never()).save(any(Vote.class));
    }

    @Test
    void castVote_rejectsVote_whenVoterIsNotApproved() {
        voter.setStatus(VoterStatus.PENDING);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(voter));

        assertThatThrownBy(() -> voteService.castVote(EMAIL, ELECTION_ID, CANDIDATE_ID))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("not been approved");

        verify(voterElectionStatusRepository, never()).saveAndFlush(any(VoterElectionStatus.class));
        verify(voteRepository, never()).save(any(Vote.class));
    }

    @Test
    void castVote_rejectsVote_whenCandidateBelongsToAnotherElection() {
        Election other = new Election();
        other.setId(99L);
        candidate.setElection(other);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(voter));
        when(electionRepository.findById(ELECTION_ID)).thenReturn(Optional.of(election));
        when(candidateRepository.findById(CANDIDATE_ID)).thenReturn(Optional.of(candidate));

        assertThatThrownBy(() -> voteService.castVote(EMAIL, ELECTION_ID, CANDIDATE_ID))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("does not belong");

        verify(voteRepository, never()).save(any(Vote.class));
    }

    // ---------------- smart eligibility check ----------------

    @Test
    void describe_canVote_whenElectionActiveAndNotYetVoted() {
        when(voterElectionStatusRepository.existsByVoterIdAndElectionId(1L, ELECTION_ID)).thenReturn(false);

        VoterElectionView view = voteService.describe(voter, election);

        assertThat(view.getEligibility()).isEqualTo(VoteEligibility.CAN_VOTE);
        assertThat(view.isCanVote()).isTrue();
    }

    @Test
    void describe_alreadyVoted_whenVoterHasVotedInActiveElection() {
        when(voterElectionStatusRepository.existsByVoterIdAndElectionId(1L, ELECTION_ID)).thenReturn(true);

        VoterElectionView view = voteService.describe(voter, election);

        assertThat(view.getEligibility()).isEqualTo(VoteEligibility.ALREADY_VOTED);
        assertThat(view.getMessage()).isEqualTo("You have already voted.");
    }

    @Test
    void describe_notStarted_forUpcomingElection() {
        election.setStartTime(LocalDateTime.now().plusDays(1));
        election.setEndTime(LocalDateTime.now().plusDays(2));
        when(voterElectionStatusRepository.existsByVoterIdAndElectionId(1L, ELECTION_ID)).thenReturn(false);

        assertThat(voteService.describe(voter, election).getMessage()).isEqualTo("Voting has not started yet.");
    }

    @Test
    void describe_ended_forCompletedElection() {
        election.setStartTime(LocalDateTime.now().minusDays(5));
        election.setEndTime(LocalDateTime.now().minusDays(1));
        when(voterElectionStatusRepository.existsByVoterIdAndElectionId(1L, ELECTION_ID)).thenReturn(false);

        assertThat(voteService.describe(voter, election).getMessage()).isEqualTo("Voting has ended.");
    }

    @Test
    void describe_notApproved_whenVoterIsPending() {
        voter.setStatus(VoterStatus.PENDING);
        when(voterElectionStatusRepository.existsByVoterIdAndElectionId(1L, ELECTION_ID)).thenReturn(false);

        assertThat(voteService.describe(voter, election).getEligibility()).isEqualTo(VoteEligibility.NOT_APPROVED);
    }
}
