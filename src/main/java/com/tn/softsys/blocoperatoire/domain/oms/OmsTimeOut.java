package com.tn.softsys.blocoperatoire.domain.oms;

import com.tn.softsys.blocoperatoire.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "oms_time_out")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OmsTimeOut {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID timeOutId;

    @Column(nullable = false)
    private Boolean teamIntroduced;

    @Column(nullable = false)
    private Boolean patientNameConfirmed;

    @Column(nullable = false)
    private Boolean interventionConfirmed;

    @Column(nullable = false)
    private Boolean siteConfirmed;

    private Boolean antibioticProphylaxisGiven;
    private Boolean imagingDisplayed;

    @Column(columnDefinition = "TEXT")
    private String criticalEventsSurgeon;

    @Column(columnDefinition = "TEXT")
    private String criticalEventsAnesthesia;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "completed_by_user_id", nullable = false)
    private User completedBy;

    @Column(nullable = false)
    private LocalDateTime completedAt;
}
