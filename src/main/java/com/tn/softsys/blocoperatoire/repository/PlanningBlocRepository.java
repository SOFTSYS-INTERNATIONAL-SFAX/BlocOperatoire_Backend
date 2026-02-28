package com.tn.softsys.blocoperatoire.repository;

import com.tn.softsys.blocoperatoire.domain.PlanningBloc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public interface PlanningBlocRepository extends JpaRepository<PlanningBloc, UUID> {

    Page<PlanningBloc> findByDateBetween(
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable
    );
}