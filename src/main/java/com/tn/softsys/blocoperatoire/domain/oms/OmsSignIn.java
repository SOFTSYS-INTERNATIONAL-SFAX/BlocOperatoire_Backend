package com.tn.softsys.blocoperatoire.domain.oms;

import com.tn.softsys.blocoperatoire.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "oms_sign_in")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OmsSignIn {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID signInId;

    @Column(nullable = false)
    private Boolean patientIdentityConfirmed;

    @Column(nullable = false)
    private Boolean siteMarked;

    @Column(nullable = false)
    private Boolean anesthesiaMachineChecked;

    @Column(nullable = false)
    private Boolean pulseOximeterWorking;

    private Boolean difficultAirwayRisk;
    private Boolean aspirationRisk;
    private Boolean hemorrhageRisk;
    private Boolean bloodProductsAvailable;

    @Column(columnDefinition = "TEXT")
    private String allergies;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "completed_by_user_id", nullable = false)
    private User completedBy;

    @Column(nullable = false)
    private LocalDateTime completedAt;
}
