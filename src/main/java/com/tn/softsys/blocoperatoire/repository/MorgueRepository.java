package com.tn.softsys.blocoperatoire.repository;

import com.tn.softsys.blocoperatoire.domain.Morgue;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MorgueRepository extends JpaRepository<Morgue, UUID> {

    Page<Morgue> findByNomContainingIgnoreCase(String nom, Pageable pageable);
}