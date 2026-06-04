package com.tn.softsys.blocoperatoire.domain.oms;

import com.tn.softsys.blocoperatoire.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "oms_sign_out")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OmsSignOut {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID signOutId;

    @Column(nullable = false)
    private Boolean interventionRecorded;

    @Column(nullable = false)
    private Boolean instrumentsCountCorrect;

    @Column(nullable = false)
    private Boolean specimensLabeled;

    @Column(nullable = false)
    private Boolean recoveryPlanConfirmed;

    @Column(columnDefinition = "TEXT")
    private String equipmentProblems;

    @Column
    private Boolean surgeonValidated;

    @Column
    private Boolean anesthesisteValidated;

    @Column
    private Boolean infirmierValidated;

    @Column(length = 150)
    private String surgeonValidatedByName;

    @Column(length = 150)
    private String anesthesisteValidatedByName;

    @Column(length = 150)
    private String infirmierValidatedByName;

    private LocalDateTime surgeonValidatedAt;

    private LocalDateTime anesthesisteValidatedAt;

    private LocalDateTime infirmierValidatedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "completed_by_user_id", nullable = false)
    private User completedBy;

    @Column(nullable = false)
    private LocalDateTime completedAt;
}
