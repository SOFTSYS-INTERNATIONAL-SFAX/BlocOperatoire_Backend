package com.tn.softsys.blocoperatoire.service;

import com.tn.softsys.blocoperatoire.domain.PatientChangeLog;
import com.tn.softsys.blocoperatoire.domain.User;
import com.tn.softsys.blocoperatoire.dto.patient.PatientChangeLogDTO;
import com.tn.softsys.blocoperatoire.repository.PatientChangeLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PatientChangeLogService {

    private final PatientChangeLogRepository repository;
    private final AuditContextService auditContextService;

    public void logChange(UUID patientId, String field, Object oldVal, Object newVal) {

        if (equalsSafe(oldVal, newVal)) return;

        User user = auditContextService.getCurrentUserOrNull();

        repository.save(
                PatientChangeLog.builder()
                        .patientId(patientId)
                        .fieldName(field)
                        .oldValue(toStringSafe(oldVal))
                        .newValue(toStringSafe(newVal))
                        .changedByUserId(user != null ? user.getUserId() : null)
                        .changedByDisplayName(resolveUser(user))
                        .build()
        );
    }

    public List<PatientChangeLogDTO> list(UUID patientId) {
        return repository.findByPatientIdOrderByChangedAtDesc(patientId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    private PatientChangeLogDTO toDTO(PatientChangeLog e) {
        return PatientChangeLogDTO.builder()
                .id(e.getId())
                .fieldName(e.getFieldName())
                .oldValue(e.getOldValue())
                .newValue(e.getNewValue())
                .changedAt(e.getChangedAt())
                .changedByUserId(e.getChangedByUserId())
                .changedBy(e.getChangedByDisplayName())
                .build();
    }

    private boolean equalsSafe(Object a, Object b) {
        return (a == null && b == null) || (a != null && a.equals(b));
    }

    private String toStringSafe(Object val) {
        return val == null ? null : val.toString();
    }

    private String resolveUser(User user) {
        if (user == null) return null;
        String label = ((user.getPrenom() == null ? "" : user.getPrenom()) + " "
                + (user.getNom() == null ? "" : user.getNom())).trim();
        return label.isBlank() ? user.getEmail() : label;
    }
}