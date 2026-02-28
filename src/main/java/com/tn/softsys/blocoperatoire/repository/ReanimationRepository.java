package com.tn.softsys.blocoperatoire.repository;

import com.tn.softsys.blocoperatoire.domain.Reanimation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ReanimationRepository extends JpaRepository<Reanimation, UUID> {

    Page<Reanimation> findByDateEntreeBetween(
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable
    );

    Page<Reanimation> findBySspiSspiId(
            UUID sspiId,
            Pageable pageable
    );

    // Réanimations encore en cours
    Page<Reanimation> findByDateSortieIsNull(Pageable pageable);

    boolean existsBySspiSspiId(UUID sspiId);
}