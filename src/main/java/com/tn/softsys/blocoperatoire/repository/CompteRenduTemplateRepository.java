package com.tn.softsys.blocoperatoire.repository;

import com.tn.softsys.blocoperatoire.domain.CompteRenduTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CompteRenduTemplateRepository extends JpaRepository<CompteRenduTemplate, UUID> {
    List<CompteRenduTemplate> findAllByOrderByUpdatedAtDesc();
}
