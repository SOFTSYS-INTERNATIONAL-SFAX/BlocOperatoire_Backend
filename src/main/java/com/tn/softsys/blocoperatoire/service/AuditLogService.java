package com.tn.softsys.blocoperatoire.service;

import com.tn.softsys.blocoperatoire.domain.AuditLog;
import com.tn.softsys.blocoperatoire.domain.User;
import com.tn.softsys.blocoperatoire.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public void log(User user,
                    String action,
                    String module,
                    String ipAddress,
                    String details) {

        AuditLog audit = AuditLog.builder()
                .user(user)
                .action(action)
                .module(module)
                .ipAddress(ipAddress)
                .details(details)
                .build();

        auditLogRepository.save(audit);
    }

    public void logFailedAttempt(String usernameAttempt,
                                 String action,
                                 String module,
                                 String ipAddress,
                                 String details) {

        AuditLog audit = AuditLog.builder()
                .user(null)
                .action(action)
                .module(module)
                .ipAddress(ipAddress)
                .details("Attempted username: " + usernameAttempt + " | " + details)
                .build();

        auditLogRepository.save(audit);
    }

    public List<AuditLog> findByUser(UUID userId) {
        return auditLogRepository.findByUserUserId(userId);
    }
}
