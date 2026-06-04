package com.tn.softsys.blocoperatoire.specification;

import com.tn.softsys.blocoperatoire.domain.Patient;
import com.tn.softsys.blocoperatoire.domain.Sexe;
import com.tn.softsys.blocoperatoire.domain.GroupeSanguin;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;

import java.time.LocalDate;

public class PatientSpecification {

    public static Specification<Patient> filter(
            String nom,
            String prenom,
            Sexe sexe,
            GroupeSanguin groupeSanguin,
            String allergie,
            LocalDate dateNaissance
    ) {

        return (root, query, cb) -> {

            query.distinct(true);

            var predicate = cb.conjunction();

            /* ================= EXCLURE PATIENTS ARCHIVÉS ================= */

            predicate = cb.and(predicate,
                    cb.equal(root.get("archived"), false));

            /* ================= NOM ================= */

            if (nom != null && !nom.isBlank()) {
                predicate = cb.and(predicate,
                        cb.like(
                                cb.lower(root.get("nom")),
                                "%" + nom.toLowerCase() + "%"
                        ));
            }

            /* ================= PRENOM ================= */

            if (prenom != null && !prenom.isBlank()) {
                predicate = cb.and(predicate,
                        cb.like(
                                cb.lower(root.get("prenom")),
                                "%" + prenom.toLowerCase() + "%"
                        ));
            }

            /* ================= SEXE ================= */

            if (sexe != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("sexe"), sexe));
            }

            /* ================= GROUPE SANGUIN ================= */

            if (groupeSanguin != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("groupeSanguin"), groupeSanguin));
            }

            /* ================= ALLERGIE ================= */

            if (allergie != null && !allergie.isBlank()) {

                Join<Patient, String> allergiesJoin =
                        root.join("allergies", JoinType.LEFT);

                predicate = cb.and(predicate,
                        cb.like(
                                cb.lower(allergiesJoin),
                                "%" + allergie.toLowerCase() + "%"
                        ));
            }

            /* ================= DATE NAISSANCE ================= */

            if (dateNaissance != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("dateNaissance"), dateNaissance));
            }

            return predicate;
        };
    }
}