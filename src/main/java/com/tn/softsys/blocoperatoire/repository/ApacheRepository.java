package com.tn.softsys.blocoperatoire.repository;

import com.tn.softsys.blocoperatoire.domain.scores.apache.ApacheII;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ApacheRepository extends JpaRepository<ApacheII, UUID> {}