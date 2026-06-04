package com.tn.softsys.blocoperatoire.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "compte_rendu_documents", indexes = {
        @Index(name = "idx_compte_rendu_document_patient", columnList = "patient_id"),
        @Index(name = "idx_compte_rendu_document_intervention", columnList = "intervention_id"),
        @Index(name = "idx_compte_rendu_document_status", columnList = "status"),
        @Index(name = "idx_compte_rendu_document_updated_at", columnList = "updatedAt")
})
public class CompteRenduDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID documentId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "intervention_id")
    private Intervention intervention;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id")
    private CompteRenduTemplate template;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authored_by_user_id")
    private User authoredBy;

    @Column(nullable = false, length = 220)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CompteRenduDocumentStatus status;

    @Column(length = 180)
    private String authoredByDisplayName;

    @Column(name = "dictation_template_id")
    private UUID dictationTemplateId;

    @Column(length = 255)
    private String audioOriginalFileName;

    @Column(length = 255)
    private String audioStorageFileName;

    @Column(length = 120)
    private String audioMimeType;

    private Long audioSizeBytes;

    @Column(length = 500)
    private String audioStoragePath;

    private Integer dictationDurationSeconds;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
