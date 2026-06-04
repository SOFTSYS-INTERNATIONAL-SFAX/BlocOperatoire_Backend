package com.tn.softsys.blocoperatoire.repository;

import com.tn.softsys.blocoperatoire.domain.oms.ChecklistOms;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChecklistOmsRepository extends JpaRepository<ChecklistOms, UUID> {

    Optional<ChecklistOms> findByIntervention_InterventionId(UUID interventionId);

    Optional<ChecklistOms> findBySignIn_SignInId(UUID signInId);

    Optional<ChecklistOms> findByTimeOut_TimeOutId(UUID timeOutId);

    Optional<ChecklistOms> findBySignOut_SignOutId(UUID signOutId);

    Page<ChecklistOms> findBySignInIsNotNull(Pageable pageable);

    Page<ChecklistOms> findByTimeOutIsNotNull(Pageable pageable);

    Page<ChecklistOms> findBySignOutIsNotNull(Pageable pageable);

    Page<ChecklistOms> findByIntervention_InterventionIdAndSignInIsNotNull(UUID interventionId, Pageable pageable);

    Page<ChecklistOms> findByIntervention_InterventionIdAndTimeOutIsNotNull(UUID interventionId, Pageable pageable);

    Page<ChecklistOms> findByIntervention_InterventionIdAndSignOutIsNotNull(UUID interventionId, Pageable pageable);

    @EntityGraph(attributePaths = {"intervention", "signIn", "timeOut", "signOut"})
    List<ChecklistOms> findByIntervention_DateIntervention(LocalDate dateIntervention);
}
