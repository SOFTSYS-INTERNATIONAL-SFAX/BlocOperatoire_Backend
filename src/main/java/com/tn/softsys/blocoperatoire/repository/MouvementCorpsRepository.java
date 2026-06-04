package com.tn.softsys.blocoperatoire.repository;

import com.tn.softsys.blocoperatoire.domain.MouvementCorps;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface MouvementCorpsRepository
        extends JpaRepository<MouvementCorps, UUID>, JpaSpecificationExecutor<MouvementCorps> {
}
