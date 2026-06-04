CREATE OR REPLACE FUNCTION app_fix_mojibake_fr(input_text TEXT)
RETURNS TEXT
LANGUAGE plpgsql
AS $$
DECLARE
    value TEXT := COALESCE(input_text, '');
BEGIN
    value := replace(value, 'Ã©', 'é');
    value := replace(value, 'Ã¨', 'è');
    value := replace(value, 'Ãª', 'ê');
    value := replace(value, 'Ã«', 'ë');
    value := replace(value, 'Ã ', 'à');
    value := replace(value, 'Ã¢', 'â');
    value := replace(value, 'Ã®', 'î');
    value := replace(value, 'Ã¯', 'ï');
    value := replace(value, 'Ã´', 'ô');
    value := replace(value, 'Ã¶', 'ö');
    value := replace(value, 'Ã¹', 'ù');
    value := replace(value, 'Ã»', 'û');
    value := replace(value, 'Ã¼', 'ü');
    value := replace(value, 'Ã§', 'ç');
    value := replace(value, 'Ã‰', 'É');
    value := replace(value, 'Ãˆ', 'È');
    value := replace(value, 'Ã€', 'À');
    value := replace(value, 'Ã‚', 'Â');
    value := replace(value, 'ÃŽ', 'Î');
    value := replace(value, 'Ã”', 'Ô');
    value := replace(value, 'Â°', '°');
    value := replace(value, 'Â·', '·');
    value := replace(value, 'Â«', '«');
    value := replace(value, 'Â»', '»');
    value := replace(value, 'Â', '');
    value := replace(value, 'â€™', '''');
    value := replace(value, 'â€“', '-');
    value := replace(value, 'â€”', '-');
    value := replace(value, 'â€¦', '...');
    value := regexp_replace(value, '\s+', ' ', 'g');
    RETURN btrim(value);
END;
$$;

CREATE OR REPLACE FUNCTION app_translate_room_en(input_text TEXT)
RETURNS TEXT
LANGUAGE plpgsql
AS $$
DECLARE
    value TEXT := app_fix_mojibake_fr(input_text);
BEGIN
    value := replace(value, 'Salle de repos', 'Recovery room');
    value := replace(value, 'salle de repos', 'recovery room');
    value := replace(value, 'Salle polyvalente', 'Multi-purpose room');
    value := replace(value, 'salle polyvalente', 'multi-purpose room');
    value := replace(value, 'Chirurgie générale', 'General surgery');
    value := replace(value, 'Chirurgie cardiaque', 'Cardiac surgery');
    value := replace(value, 'Chirurgie medicale', 'Medical surgery');
    value := replace(value, 'Chirurgie médicale', 'Medical surgery');
    value := replace(value, 'Neurochirurgie', 'Neurosurgery');
    value := replace(value, 'Orthopédie', 'Orthopedics');
    value := replace(value, 'Urgences', 'Emergency');
    value := replace(value, 'Bloc', 'Block');
    value := replace(value, 'Salle', 'Room');
    value := replace(value, 'Bâtiment', 'Building');
    value := replace(value, 'batiment', 'building');
    value := replace(value, 'étage', 'floor');
    value := replace(value, 'Etage', 'Floor');
    value := replace(value, 'ème', '');
    RETURN btrim(value);
END;
$$;

CREATE OR REPLACE FUNCTION app_translate_room_ar(input_text TEXT)
RETURNS TEXT
LANGUAGE plpgsql
AS $$
DECLARE
    value TEXT := app_fix_mojibake_fr(input_text);
BEGIN
    value := replace(value, 'Salle de repos', 'غرفة الاستراحة');
    value := replace(value, 'salle de repos', 'غرفة الاستراحة');
    value := replace(value, 'Salle polyvalente', 'غرفة متعددة الاستعمال');
    value := replace(value, 'salle polyvalente', 'غرفة متعددة الاستعمال');
    value := replace(value, 'Chirurgie générale', 'الجراحة العامة');
    value := replace(value, 'Chirurgie cardiaque', 'جراحة القلب');
    value := replace(value, 'Chirurgie medicale', 'الجراحة الطبية');
    value := replace(value, 'Chirurgie médicale', 'الجراحة الطبية');
    value := replace(value, 'Neurochirurgie', 'جراحة الأعصاب');
    value := replace(value, 'Orthopédie', 'جراحة العظام');
    value := replace(value, 'Urgences', 'الاستعجالي');
    value := replace(value, 'Bloc', 'الجناح');
    value := replace(value, 'Salle', 'قاعة');
    value := replace(value, 'Bâtiment', 'المبنى');
    value := replace(value, 'batiment', 'المبنى');
    value := replace(value, 'étage', 'الطابق');
    value := replace(value, 'Etage', 'الطابق');
    RETURN btrim(value);
END;
$$;

CREATE OR REPLACE FUNCTION app_translate_intervention_en(input_text TEXT)
RETURNS TEXT
LANGUAGE plpgsql
AS $$
DECLARE
    value TEXT := app_fix_mojibake_fr(input_text);
BEGIN
    value := replace(value, 'Abcès et fistules', 'Abscesses and fistulas');
    value := replace(value, 'Mise à plat', 'Drainage');
    value := replace(value, 'Traitement', 'Treatment');
    value := replace(value, 'Parage', 'Debridement');
    value := replace(value, 'plaie', 'wound');
    value := replace(value, 'orthopédique', 'orthopedic');
    value := replace(value, 'fracture fermée', 'closed fracture');
    value := replace(value, 'traitée orthopédiquement', 'treated orthopedically');
    value := replace(value, 'Ostéosynthèse', 'Osteosynthesis');
    value := replace(value, 'Courants galvaniques, faradiques ou excito-moteurs', 'Galvanic, faradic or excitomotor currents');
    value := replace(value, 'ultrasons', 'ultrasound');
    value := replace(value, 'diathermie', 'diathermy');
    value := replace(value, 'ondes courtes', 'short waves');
    value := replace(value, 'application de surface', 'surface application');
    value := replace(value, 'séance', 'session');
    value := replace(value, 'durée', 'duration');
    value := replace(value, 'mise en place', 'placement');
    value := replace(value, 'électrodes fixes de surface', 'fixed surface electrodes');
    value := replace(value, 'éléctrodes fixes de surface', 'fixed surface electrodes');
    value := replace(value, 'peau', 'skin');
    value := replace(value, 'Abdomen aigu', 'Acute abdomen');
    value := replace(value, 'sans préparation', 'without preparation');
    value := replace(value, 'Ablation chirurgicale', 'Surgical removal');
    value := replace(value, 'Ablation d''un corps étranger', 'Removal of a foreign body');
    value := replace(value, 'Ablation d''implant', 'Implant removal');
    value := replace(value, 'sac lacrymal', 'lacrimal sac');
    value := replace(value, 'cancer du clitoris, de la vulve ou du vagin', 'cancer of the clitoris, vulva or vagina');
    value := replace(value, 'Avec curage ganglionnaire bilatéral', 'With bilateral lymph node dissection');
    value := replace(value, 'Avec curage ganglionnaire unilatéral', 'With unilateral lymph node dissection');
    value := replace(value, 'Sans curage ganglionnaire', 'Without lymph node dissection');
    value := replace(value, 'extra-sphinctériens à trajet multiramifié', 'extrasphincteric with complex branching tract');
    value := replace(value, 'à trajet simple', 'with simple tract');
    value := replace(value, 'intra-sphinctériens', 'intrasphincteric');
    value := replace(value, 'opératoire ou par traction continue sur fil', 'surgical or by continuous seton traction');
    value := replace(value, 'fosses nasales', 'nasal cavities');
    value := replace(value, 'non enclavé', 'non-impacted');
    value := replace(value, 'enclavé', 'impacted');
    RETURN btrim(value);
END;
$$;

CREATE OR REPLACE FUNCTION app_translate_intervention_ar(input_text TEXT)
RETURNS TEXT
LANGUAGE plpgsql
AS $$
DECLARE
    value TEXT := app_fix_mojibake_fr(input_text);
BEGIN
    value := replace(value, 'Abcès et fistules', 'الخراجات والنواسير');
    value := replace(value, 'Mise à plat', 'فتح وتصريف');
    value := replace(value, 'Traitement', 'علاج');
    value := replace(value, 'Parage', 'تنضير');
    value := replace(value, 'plaie', 'الجرح');
    value := replace(value, 'orthopédique', 'العظمي');
    value := replace(value, 'fracture fermée', 'الكسر المغلق');
    value := replace(value, 'traitée orthopédiquement', 'المعالج عظمياً');
    value := replace(value, 'Ostéosynthèse', 'التثبيت العظمي');
    value := replace(value, 'Courants galvaniques, faradiques ou excito-moteurs', 'تيارات غلفانية أو فارادية أو منبهة للحركة');
    value := replace(value, 'ultrasons', 'الموجات فوق الصوتية');
    value := replace(value, 'diathermie', 'الدياثيرمي');
    value := replace(value, 'ondes courtes', 'الموجات القصيرة');
    value := replace(value, 'application de surface', 'تطبيق سطحي');
    value := replace(value, 'séance', 'جلسة');
    value := replace(value, 'durée', 'المدة');
    value := replace(value, 'mise en place', 'وضع');
    value := replace(value, 'électrodes fixes de surface', 'أقطاب سطحية ثابتة');
    value := replace(value, 'éléctrodes fixes de surface', 'أقطاب سطحية ثابتة');
    value := replace(value, 'peau', 'الجلد');
    value := replace(value, 'Abdomen aigu', 'البطن الحاد');
    value := replace(value, 'sans préparation', 'بدون تحضير');
    value := replace(value, 'Ablation chirurgicale', 'استئصال جراحي');
    value := replace(value, 'Ablation d''un corps étranger', 'استخراج جسم غريب');
    value := replace(value, 'Ablation d''implant', 'إزالة زرعة');
    value := replace(value, 'sac lacrymal', 'الكيس الدمعي');
    value := replace(value, 'cancer du clitoris, de la vulve ou du vagin', 'سرطان البظر أو الفرج أو المهبل');
    value := replace(value, 'Avec curage ganglionnaire bilatéral', 'مع تجريف عقدي ثنائي الجانب');
    value := replace(value, 'Avec curage ganglionnaire unilatéral', 'مع تجريف عقدي أحادي الجانب');
    value := replace(value, 'Sans curage ganglionnaire', 'بدون تجريف عقدي');
    value := replace(value, 'extra-sphinctériens à trajet multiramifié', 'خارج المصرة بمسار متشعب معقد');
    value := replace(value, 'à trajet simple', 'بمسار بسيط');
    value := replace(value, 'intra-sphinctériens', 'داخل المصرة');
    value := replace(value, 'opératoire ou par traction continue sur fil', 'جراحياً أو بالشد المستمر على خيط');
    value := replace(value, 'fosses nasales', 'التجاويف الأنفية');
    value := replace(value, 'non enclavé', 'غير منغرس');
    value := replace(value, 'enclavé', 'منغرس');
    RETURN btrim(value);
END;
$$;

UPDATE intervention_catalog
SET designation = app_fix_mojibake_fr(designation),
    designation_en = app_translate_intervention_en(COALESCE(NULLIF(BTRIM(designation_en), ''), designation)),
    designation_ar = app_translate_intervention_ar(COALESCE(NULLIF(BTRIM(designation_ar), ''), designation));

UPDATE interventions i
SET nom_intervention = app_fix_mojibake_fr(nom_intervention),
    nom_intervention_en = COALESCE(
        NULLIF(BTRIM(ic.designation_en), ''),
        app_translate_intervention_en(COALESCE(NULLIF(BTRIM(i.nom_intervention_en), ''), i.nom_intervention))
    ),
    nom_intervention_ar = COALESCE(
        NULLIF(BTRIM(ic.designation_ar), ''),
        app_translate_intervention_ar(COALESCE(NULLIF(BTRIM(i.nom_intervention_ar), ''), i.nom_intervention))
    )
FROM intervention_catalog ic
WHERE i.catalog_id = ic.catalog_id;

UPDATE interventions
SET nom_intervention = app_fix_mojibake_fr(nom_intervention),
    nom_intervention_en = app_translate_intervention_en(COALESCE(NULLIF(BTRIM(nom_intervention_en), ''), nom_intervention)),
    nom_intervention_ar = app_translate_intervention_ar(COALESCE(NULLIF(BTRIM(nom_intervention_ar), ''), nom_intervention))
WHERE catalog_id IS NULL;

UPDATE salles
SET nom = app_fix_mojibake_fr(nom),
    id_bloc = app_fix_mojibake_fr(id_bloc),
    etage_batiment = app_fix_mojibake_fr(etage_batiment),
    equipements = app_fix_mojibake_fr(equipements),
    nom_en = app_translate_room_en(COALESCE(NULLIF(BTRIM(nom_en), ''), nom)),
    nom_ar = app_translate_room_ar(COALESCE(NULLIF(BTRIM(nom_ar), ''), nom)),
    id_bloc_en = app_translate_room_en(COALESCE(NULLIF(BTRIM(id_bloc_en), ''), id_bloc)),
    id_bloc_ar = app_translate_room_ar(COALESCE(NULLIF(BTRIM(id_bloc_ar), ''), id_bloc)),
    etage_batiment_en = app_translate_room_en(COALESCE(NULLIF(BTRIM(etage_batiment_en), ''), etage_batiment)),
    etage_batiment_ar = app_translate_room_ar(COALESCE(NULLIF(BTRIM(etage_batiment_ar), ''), etage_batiment));

UPDATE alerts a
SET patient_label = src.patient_label,
    patient_label_en = src.patient_label,
    patient_label_ar = src.patient_label,
    intervention_label = src.intervention_label,
    intervention_label_en = src.intervention_label_en,
    intervention_label_ar = src.intervention_label_ar,
    room_label = src.room_label,
    room_label_en = src.room_label_en,
    room_label_ar = src.room_label_ar
FROM (
    SELECT
        a2.alert_id,
        NULLIF(BTRIM(COALESCE(p.prenom, '') || ' ' || COALESCE(p.nom, '')), '') AS patient_label,
        COALESCE(i.nom_intervention, ic.designation) AS intervention_label,
        COALESCE(i.nom_intervention_en, ic.designation_en, i.nom_intervention, ic.designation) AS intervention_label_en,
        COALESCE(i.nom_intervention_ar, ic.designation_ar, i.nom_intervention, ic.designation) AS intervention_label_ar,
        COALESCE(s.nom, si.nom) AS room_label,
        COALESCE(s.nom_en, si.nom_en, s.nom, si.nom) AS room_label_en,
        COALESCE(s.nom_ar, si.nom_ar, s.nom, si.nom) AS room_label_ar
    FROM alerts a2
    LEFT JOIN interventions i ON i.intervention_id = a2.intervention_id
    LEFT JOIN intervention_catalog ic ON ic.catalog_id = i.catalog_id
    LEFT JOIN patients p ON p.patient_id = i.patient_id
    LEFT JOIN salles s ON s.salle_id = a2.salle_id
    LEFT JOIN salles si ON si.salle_id = i.salle_id
) src
WHERE a.alert_id = src.alert_id;

UPDATE alerts
SET title_en = CASE type
        WHEN 'INTERVENTION_PLANIFIEE' THEN 'Planned procedure'
        WHEN 'INTERVENTION_DEMARREE' THEN 'Procedure started'
        WHEN 'SSPI_DEPASSEMENT' THEN 'PACU overrun'
        WHEN 'SALLE_ACTIVEE' THEN 'Room activated'
        WHEN 'SALLE_DESACTIVEE' THEN 'Room deactivated'
        ELSE COALESCE(NULLIF(BTRIM(title_en), ''), title)
    END,
    title_ar = CASE type
        WHEN 'INTERVENTION_PLANIFIEE' THEN 'عملية مجدولة'
        WHEN 'INTERVENTION_DEMARREE' THEN 'بدأت العملية'
        WHEN 'SSPI_DEPASSEMENT' THEN 'تجاوز مدة الإفاقة'
        WHEN 'SALLE_ACTIVEE' THEN 'تم تفعيل غرفة العمليات'
        WHEN 'SALLE_DESACTIVEE' THEN 'تم تعطيل غرفة العمليات'
        ELSE COALESCE(NULLIF(BTRIM(title_ar), ''), title)
    END;

UPDATE alerts
SET message_en = CASE type
        WHEN 'INTERVENTION_PLANIFIEE' THEN COALESCE(intervention_label_en, 'Procedure') || ' has been scheduled for ' || COALESCE(patient_label_en, 'unknown patient') || '.'
        WHEN 'INTERVENTION_DEMARREE' THEN COALESCE(intervention_label_en, 'Procedure') || ' has started for ' || COALESCE(patient_label_en, 'unknown patient') || '.'
        WHEN 'SSPI_DEPASSEMENT' THEN 'Patient ' || COALESCE(patient_label_en, 'unknown patient') || ' exceeded the authorized PACU delay after ' || COALESCE(intervention_label_en, 'procedure') || '.'
        WHEN 'SALLE_ACTIVEE' THEN COALESCE(room_label_en, 'Room') || ' is now active.'
        WHEN 'SALLE_DESACTIVEE' THEN COALESCE(room_label_en, 'Room') || ' is now inactive.'
        ELSE COALESCE(NULLIF(BTRIM(message_en), ''), message)
    END,
    message_ar = CASE type
        WHEN 'INTERVENTION_PLANIFIEE' THEN 'تمت جدولة ' || COALESCE(intervention_label_ar, 'العملية') || ' للمريض ' || COALESCE(patient_label_ar, 'مريض غير معروف') || '.'
        WHEN 'INTERVENTION_DEMARREE' THEN 'بدأت ' || COALESCE(intervention_label_ar, 'العملية') || ' للمريض ' || COALESCE(patient_label_ar, 'مريض غير معروف') || '.'
        WHEN 'SSPI_DEPASSEMENT' THEN 'تجاوز المريض ' || COALESCE(patient_label_ar, 'مريض غير معروف') || ' المدة المسموح بها في الإفاقة بعد ' || COALESCE(intervention_label_ar, 'العملية') || '.'
        WHEN 'SALLE_ACTIVEE' THEN COALESCE(room_label_ar, 'غرفة العمليات') || ' أصبحت مفعلة الآن.'
        WHEN 'SALLE_DESACTIVEE' THEN COALESCE(room_label_ar, 'غرفة العمليات') || ' أصبحت غير مفعلة الآن.'
        ELSE COALESCE(NULLIF(BTRIM(message_ar), ''), message)
    END;
