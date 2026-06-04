package com.tn.softsys.blocoperatoire.repository;

import com.tn.softsys.blocoperatoire.domain.scores.asa.Asa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AsaRepository extends JpaRepository<Asa, UUID> {}