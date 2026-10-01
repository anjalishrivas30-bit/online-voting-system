package com.internship.voting.service;

import com.internship.voting.model.AuditLog;
import com.internship.voting.repository.AuditLogRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Writes the audit trail. IMPORTANT: when a vote is logged, only "voter X voted in election Y" is stored -
 * never the candidate that was chosen - so the audit log cannot be used to break ballot secrecy.
 */
@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public void log(String actor, String action, String details) {
        auditLogRepository.save(new AuditLog(truncate(actor, 150), action, truncate(details, 500)));
    }

    @Transactional(readOnly = true)
    public List<AuditLog> latest() {
        return auditLogRepository.findTop100ByOrderByCreatedAtDesc();
    }

    private String truncate(String text, int max) {
        if (text == null) {
            return "";
        }
        return text.length() <= max ? text : text.substring(0, max);
    }
}
