package com.tn.softsys.blocoperatoire.repository;

import com.tn.softsys.blocoperatoire.domain.scores.aldrete.Aldrete;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AldreteRepository extends JpaRepository<Aldrete, UUID> {}