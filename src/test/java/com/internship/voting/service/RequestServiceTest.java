package com.internship.voting.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.internship.voting.exception.BusinessException;
import com.internship.voting.model.RequestStatus;
import com.internship.voting.model.RequestType;
import com.internship.voting.model.Role;
import com.internship.voting.model.User;
import com.internship.voting.model.VoterRequest;
import com.internship.voting.repository.UserRepository;
import com.internship.voting.repository.VoterRequestRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Unit tests for the voter request workflow (PENDING -> APPROVED / REJECTED). */
@ExtendWith(MockitoExtension.class)
class RequestServiceTest {

    @Mock private VoterRequestRepository requestRepository;
    @Mock private UserRepository userRepository;
    @Mock private AuditService auditService;

    @InjectMocks private RequestService requestService;

    private User voter;
    private VoterRequest pending;

    @BeforeEach
    void setUp() {
        voter = new User();
        voter.setId(1L);
        voter.setEmail("ravi@example.com");
        voter.setRole(new Role(Role.VOTER));
        pending = new VoterRequest(voter, RequestType.PROFILE_CORRECTION, "Please fix my name spelling");
    }

    @Test
    void submit_createsPendingRequest() {
        when(userRepository.findByEmail("ravi@example.com")).thenReturn(Optional.of(voter));
        when(requestRepository.save(any(VoterRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        VoterRequest saved = requestService.submit("ravi@example.com", RequestType.OTHER, "  Need help with voting  ");

        assertThat(saved.getStatus()).isEqualTo(RequestStatus.PENDING);
        assertThat(saved.getDescription()).isEqualTo("Need help with voting");
        assertThat(saved.getVoter()).isSameAs(voter);
    }

    @Test
    void decide_approve_setsStatusRemarkAndNotifiesVoter() {
        when(requestRepository.findById(5L)).thenReturn(Optional.of(pending));

        requestService.decide(5L, RequestStatus.APPROVED, "Verified", "admin@voting.com");

        assertThat(pending.getStatus()).isEqualTo(RequestStatus.APPROVED);
        assertThat(pending.getAdminRemark()).isEqualTo("Verified");
        assertThat(pending.getDecidedAt()).isNotNull();
        assertThat(pending.isVoterNotified()).isFalse();   // voter has an unseen notification
        verify(requestRepository).save(pending);
    }

    @Test
    void decide_rejectWithoutRemark_isRefused() {
        when(requestRepository.findById(5L)).thenReturn(Optional.of(pending));

        assertThatThrownBy(() -> requestService.decide(5L, RequestStatus.REJECTED, "  ", "admin@voting.com"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("remark");

        assertThat(pending.getStatus()).isEqualTo(RequestStatus.PENDING);
        verify(requestRepository, never()).save(any(VoterRequest.class));
    }

    @Test
    void decide_cannotDecideTwice() {
        pending.setStatus(RequestStatus.APPROVED);
        when(requestRepository.findById(5L)).thenReturn(Optional.of(pending));

        assertThatThrownBy(() -> requestService.decide(5L, RequestStatus.REJECTED, "Too late", "admin@voting.com"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already been");

        verify(requestRepository, never()).save(any(VoterRequest.class));
    }
}
