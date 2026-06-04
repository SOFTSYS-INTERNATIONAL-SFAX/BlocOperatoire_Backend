-- Fix any remaining encoding corruption in salles.etage_batiment and related fields.
-- The app_fix_mojibake_fr function from V20260514_02 handles UTF-8-as-Latin-1 patterns (Ãâ¢ etc.).
-- This migration also handles direct replacement of the diamond character (♦) that
-- can appear when a single byte 0xE2 is misread, and re-applies the fix functions
-- to ensure all data is clean after the server.servlet.encoding fix in application.properties.

CREATE OR REPLACE FUNCTION app_fix_diamond_corruption(input_text TEXT)
RETURNS TEXT
LANGUAGE plpgsql
AS $$
DECLARE
    value TEXT := COALESCE(input_text, '');
BEGIN
    -- Replace diamond character (♦ U+2666) back to â when it appears in French words
    value := replace(value, 'B♦timent', 'Bâtiment');
    value := replace(value, 'b♦timent', 'bâtiment');
    value := replace(value, '♦tage', 'étage');
    value := replace(value, '♦', 'â');
    RETURN value;
END;
$$;

UPDATE salles
SET etage_batiment = app_fix_diamond_corruption(etage_batiment)
WHERE etage_batiment IS NOT NULL AND etage_batiment LIKE '%♦%';

UPDATE salles
SET nom = app_fix_diamond_corruption(nom)
WHERE nom IS NOT NULL AND nom LIKE '%♦%';

UPDATE salles
SET id_bloc = app_fix_diamond_corruption(id_bloc)
WHERE id_bloc IS NOT NULL AND id_bloc LIKE '%♦%';

UPDATE salles
SET equipements = app_fix_diamond_corruption(equipements)
WHERE equipements IS NOT NULL AND equipements LIKE '%♦%';

-- Re-apply mojibake fix to catch any rows that were added after V20260514_02 ran
UPDATE salles
SET etage_batiment = app_fix_mojibake_fr(etage_batiment)
WHERE etage_batiment IS NOT NULL
  AND (
    etage_batiment LIKE '%Ã%'
    OR etage_batiment LIKE '%Â%'
    OR etage_batiment LIKE '%â€%'
  );

UPDATE salles
SET nom = app_fix_mojibake_fr(nom)
WHERE nom IS NOT NULL
  AND (nom LIKE '%Ã%' OR nom LIKE '%Â%' OR nom LIKE '%â€%');

-- Also fix the i18n fallback columns
UPDATE salles
SET etage_batiment_en = app_fix_mojibake_fr(etage_batiment_en)
WHERE etage_batiment_en IS NOT NULL AND (etage_batiment_en LIKE '%Ã%' OR etage_batiment_en LIKE '%♦%');

UPDATE salles
SET etage_batiment_ar = app_fix_mojibake_fr(etage_batiment_ar)
WHERE etage_batiment_ar IS NOT NULL AND etage_batiment_ar LIKE '%Ã%';
