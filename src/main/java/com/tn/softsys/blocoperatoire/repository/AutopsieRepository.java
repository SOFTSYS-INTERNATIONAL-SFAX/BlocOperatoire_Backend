package com.tn.softsys.blocoperatoire.repository;

import com.tn.softsys.blocoperatoire.domain.Autopsie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface AutopsieRepository extends JpaRepository<Autopsie, UUID>, JpaSpecificationExecutor<Autopsie> {

    boolean existsByDecesDecesId(UUID decesId);
}
