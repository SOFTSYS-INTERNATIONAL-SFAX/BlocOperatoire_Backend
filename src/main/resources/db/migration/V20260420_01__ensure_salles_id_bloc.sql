DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = current_schema()
          AND table_name = 'salles'
    ) THEN

        IF NOT EXISTS (
            SELECT 1
            FROM information_schema.columns
            WHERE table_schema = current_schema()
              AND table_name = 'salles'
              AND column_name = 'id_bloc'
        ) THEN
            EXECUTE 'ALTER TABLE salles ADD COLUMN id_bloc VARCHAR(100)';
        END IF;

        EXECUTE $sql$
            UPDATE salles
            SET id_bloc = COALESCE(
                NULLIF(TRIM(id_bloc), ''),
                NULLIF(TRIM(nom), ''),
                'BLOC-INDISPONIBLE'
            )
            WHERE id_bloc IS NULL OR TRIM(id_bloc) = ''
        $sql$;

        EXECUTE 'ALTER TABLE salles ALTER COLUMN id_bloc SET NOT NULL';
    END IF;
END
$$;
