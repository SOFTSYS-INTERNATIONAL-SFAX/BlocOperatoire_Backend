package com.tn.softsys.blocoperatoire.repository;

import com.tn.softsys.blocoperatoire.domain.scores.eva.Eva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EvaRepository extends JpaRepository<Eva, UUID> {}