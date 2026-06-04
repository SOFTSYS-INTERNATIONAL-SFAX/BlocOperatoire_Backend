UPDATE salles
SET id_bloc = COALESCE(
    NULLIF(
        TRIM(
            SUBSTRING(nom FROM '(?i).*-\\s*((bloc|block)\\s*[^-]+)$')
        ),
        ''
    ),
    NULLIF(
        TRIM(
            SUBSTRING(nom FROM '(?i)^((bloc|block)\\s*[^-]+)')
        ),
        ''
    ),
    id_bloc
)
WHERE id_bloc IS NOT NULL
  AND nom IS NOT NULL
  AND TRIM(id_bloc) = TRIM(nom);
