package com.tn.softsys.blocoperatoire.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScoreSchemaMigrationService {

    private final JdbcTemplate jdbcTemplate;

    @EventListener(ApplicationReadyEvent.class)
    public void ensureScorePatientColumn() {
        if (!tableExists("scores")) {
            return;
        }

        try {
            if (!columnExists("scores", "patient_id")) {
                jdbcTemplate.execute("ALTER TABLE scores ADD COLUMN patient_id uuid");
            }

            jdbcTemplate.update(
                    "UPDATE scores s " +
                    "SET patient_id = i.patient_id " +
                    "FROM interventions i " +
                    "WHERE s.intervention_id = i.intervention_id " +
                    "AND s.patient_id IS NULL"
            );

            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_score_patient ON scores(patient_id)");
            if (columnExists("scores", "intervention_id")) {
                jdbcTemplate.execute("ALTER TABLE scores ALTER COLUMN intervention_id DROP NOT NULL");
            }


            if (!foreignKeyExists("scores", "fk_scores_patient")) {
                jdbcTemplate.execute(
                        "ALTER TABLE scores " +
                        "ADD CONSTRAINT fk_scores_patient " +
                        "FOREIGN KEY (patient_id) REFERENCES patients(patient_id)"
                );
            }

            Integer remainingNulls = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM scores WHERE patient_id IS NULL",
                    Integer.class
            );

            if (remainingNulls != null && remainingNulls == 0) {
                jdbcTemplate.execute("ALTER TABLE scores ALTER COLUMN patient_id SET NOT NULL");
            } else {
                log.warn("Score migration left {} rows without patient_id. Column kept nullable in DB.", remainingNulls);
            }
        } catch (Exception ex) {
            log.error("Unable to finalize score patient_id migration", ex);
        }
    }

    private boolean tableExists(String tableName) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = ?",
                Integer.class,
                tableName
        );

        return count != null && count > 0;
    }

    private boolean columnExists(String tableName, String columnName) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.columns WHERE table_name = ? AND column_name = ?",
                Integer.class,
                tableName,
                columnName
        );

        return count != null && count > 0;
    }

    private boolean foreignKeyExists(String tableName, String constraintName) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.table_constraints " +
                "WHERE table_name = ? AND constraint_name = ? AND constraint_type = 'FOREIGN KEY'",
                Integer.class,
                tableName,
                constraintName
        );

        return count != null && count > 0;
    }
}
