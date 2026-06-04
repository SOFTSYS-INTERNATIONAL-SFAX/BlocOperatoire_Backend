ALTER TABLE intervention_catalog
    ADD COLUMN IF NOT EXISTS designation_en VARCHAR(512),
    ADD COLUMN IF NOT EXISTS designation_ar VARCHAR(512);

ALTER TABLE interventions
    ADD COLUMN IF NOT EXISTS nom_intervention_en VARCHAR(512),
    ADD COLUMN IF NOT EXISTS nom_intervention_ar VARCHAR(512);

ALTER TABLE salles
    ADD COLUMN IF NOT EXISTS nom_en VARCHAR(255),
    ADD COLUMN IF NOT EXISTS nom_ar VARCHAR(255),
    ADD COLUMN IF NOT EXISTS id_bloc_en VARCHAR(100),
    ADD COLUMN IF NOT EXISTS id_bloc_ar VARCHAR(100),
    ADD COLUMN IF NOT EXISTS etage_batiment_en VARCHAR(255),
    ADD COLUMN IF NOT EXISTS etage_batiment_ar VARCHAR(255);

ALTER TABLE alerts
    ADD COLUMN IF NOT EXISTS title_en VARCHAR(200),
    ADD COLUMN IF NOT EXISTS title_ar VARCHAR(200),
    ADD COLUMN IF NOT EXISTS message_en VARCHAR(2000),
    ADD COLUMN IF NOT EXISTS message_ar VARCHAR(2000),
    ADD COLUMN IF NOT EXISTS patient_label VARCHAR(200),
    ADD COLUMN IF NOT EXISTS patient_label_en VARCHAR(200),
    ADD COLUMN IF NOT EXISTS patient_label_ar VARCHAR(200),
    ADD COLUMN IF NOT EXISTS intervention_label VARCHAR(512),
    ADD COLUMN IF NOT EXISTS intervention_label_en VARCHAR(512),
    ADD COLUMN IF NOT EXISTS intervention_label_ar VARCHAR(512),
    ADD COLUMN IF NOT EXISTS room_label VARCHAR(255),
    ADD COLUMN IF NOT EXISTS room_label_en VARCHAR(255),
    ADD COLUMN IF NOT EXISTS room_label_ar VARCHAR(255);

UPDATE intervention_catalog
SET designation_en = designation
WHERE designation_en IS NULL OR BTRIM(designation_en) = '';

UPDATE intervention_catalog
SET designation_ar = designation
WHERE designation_ar IS NULL OR BTRIM(designation_ar) = '';

UPDATE interventions
SET nom_intervention_en = nom_intervention
WHERE nom_intervention_en IS NULL OR BTRIM(nom_intervention_en) = '';

UPDATE interventions
SET nom_intervention_ar = nom_intervention
WHERE nom_intervention_ar IS NULL OR BTRIM(nom_intervention_ar) = '';

UPDATE salles
SET nom_en = nom
WHERE nom_en IS NULL OR BTRIM(nom_en) = '';

UPDATE salles
SET nom_ar = nom
WHERE nom_ar IS NULL OR BTRIM(nom_ar) = '';

UPDATE salles
SET id_bloc_en = id_bloc
WHERE id_bloc_en IS NULL OR BTRIM(id_bloc_en) = '';

UPDATE salles
SET id_bloc_ar = id_bloc
WHERE id_bloc_ar IS NULL OR BTRIM(id_bloc_ar) = '';

UPDATE salles
SET etage_batiment_en = etage_batiment
WHERE etage_batiment_en IS NULL OR BTRIM(etage_batiment_en) = '';

UPDATE salles
SET etage_batiment_ar = etage_batiment
WHERE etage_batiment_ar IS NULL OR BTRIM(etage_batiment_ar) = '';

UPDATE alerts
SET title_en = title
WHERE title_en IS NULL OR BTRIM(title_en) = '';

UPDATE alerts
SET title_ar = title
WHERE title_ar IS NULL OR BTRIM(title_ar) = '';

UPDATE alerts
SET message_en = message
WHERE message_en IS NULL OR BTRIM(message_en) = '';

UPDATE alerts
SET message_ar = message
WHERE message_ar IS NULL OR BTRIM(message_ar) = '';
