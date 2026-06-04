package com.tn.softsys.blocoperatoire.domain.oms;

import com.tn.softsys.blocoperatoire.domain.Intervention;
import com.tn.softsys.blocoperatoire.domain.Patient;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "oms_checklists",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_oms_checklist_intervention", columnNames = "intervention_id"),
                @UniqueConstraint(name = "uk_oms_checklist_sign_in", columnNames = "sign_in_id"),
                @UniqueConstraint(name = "uk_oms_checklist_time_out", columnNames = "time_out_id"),
                @UniqueConstraint(name = "uk_oms_checklist_sign_out", columnNames = "sign_out_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChecklistOms {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID checklistId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "intervention_id", nullable = false, unique = true)
    private Intervention intervention;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "sign_in_id", unique = true)
    private OmsSignIn signIn;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "time_out_id", unique = true)
    private OmsTimeOut timeOut;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "sign_out_id", unique = true)
    private OmsSignOut signOut;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
