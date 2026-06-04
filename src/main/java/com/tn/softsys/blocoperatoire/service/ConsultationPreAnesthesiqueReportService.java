package com.tn.softsys.blocoperatoire.service;

import com.tn.softsys.blocoperatoire.domain.ConsultationPreAnesthesique;
import com.tn.softsys.blocoperatoire.dto.preanesthesie.PreAnesthesieReportDownload;
import com.tn.softsys.blocoperatoire.exception.ResourceNotFoundException;
import com.tn.softsys.blocoperatoire.repository.ConsultationPreAnesthesiqueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConsultationPreAnesthesiqueReportService {

    private final ConsultationPreAnesthesiqueRepository repository;
    private final AuditLogService auditLogService;
    private final AuditContextService auditContextService;

    public PreAnesthesieReportDownload download(UUID consultationId) {
        ConsultationPreAnesthesique consultation = repository.findById(consultationId)
                .orElseThrow(() -> new ResourceNotFoundException("Pre-anesthesia consultation not found"));

        String patientLabel = consultation.getPatient() == null
                ? "patient"
                : ((consultation.getPatient().getPrenom() == null ? "" : consultation.getPatient().getPrenom().trim())
                + " "
                + (consultation.getPatient().getNom() == null ? "" : consultation.getPatient().getNom().trim())).trim();

        String normalizedPatientLabel = patientLabel.isBlank()
                ? "patient"
                : patientLabel.toLowerCase().replaceAll("[^a-z0-9]+", "-");

        auditLogService.log(
                auditContextService.getCurrentUserOrNull(),
                "PRE_ANESTHESIE_REPORT_DOWNLOAD",
                "PRE_ANESTHESIE",
                consultation.getConsultationId(),
                "Export PDF fiche pre-anesthesie consultation=" + consultation.getConsultationId(),
                auditContextService.getClientIp()
        );

        return new PreAnesthesieReportDownload(
                "pre-anesthesie-" + normalizedPatientLabel + ".pdf",
                buildPdf(consultation)
        );
    }

    private byte[] buildPdf(ConsultationPreAnesthesique consultation) {
        List<String> rawLines = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        rawLines.add("Compte rendu pre-anesthesique");
        rawLines.add("");
        rawLines.add("Patient: " + buildPatientLabel(consultation));
        rawLines.add("MRN: " + value(consultation.getPatient() != null ? consultation.getPatient().getMrn() : null));
        rawLines.add("Intervention prevue: " + value(
                consultation.getIntervention() != null ? consultation.getIntervention().getNomIntervention() : null
        ));
        rawLines.add("Date intervention: " + (
                consultation.getIntervention() != null && consultation.getIntervention().getDateIntervention() != null
                        ? consultation.getIntervention().getDateIntervention().format(dateFormatter)
                        : "-"
        ));
        rawLines.add("Heure intervention: " + value(
                consultation.getIntervention() != null ? String.valueOf(consultation.getIntervention().getHeureDebut()) : null
        ));
        rawLines.add("ASA: " + value(consultation.getAsaCode()));
        rawLines.add("Urgence: " + yesNo(consultation.getUrgence()));
        rawLines.add("Type anesthesie: " + value(consultation.getTypeAnesthesie()));
        rawLines.add("Risque calcule: " + value(consultation.getRiskLevel()) + " (" + value(consultation.getRiskScore()) + ")");
        rawLines.add("Facteurs risque: " + value(consultation.getRiskSummary()));
        rawLines.add("");
        rawLines.add("Examen clinique");
        rawLines.add("Poids/Taille: " + value(consultation.getPoidsKg()) + " kg / " + value(consultation.getTailleCm()) + " cm");
        rawLines.add("PA: " + value(consultation.getPaSystolique()) + " / " + value(consultation.getPaDiastolique()));
        rawLines.add("FC/FR: " + value(consultation.getFrequenceCardiaque()) + " / " + value(consultation.getFrequenceRespiratoire()));
        rawLines.add("SpO2: " + value(consultation.getSpo2()));
        rawLines.add("Temperature dixieme: " + value(consultation.getTemperatureDixieme()));
        rawLines.add("Mallampati: " + value(consultation.getMallampatiCode()));
        rawLines.add("Ouverture buccale: " + value(consultation.getOuvertureBucaleMm()));
        rawLines.add("Mobilite cervicale: " + value(consultation.getMobiliteCervicale()));
        rawLines.add("Etat dentaire: " + value(consultation.getEtatDentaire()));
        rawLines.add("Voie aerienne difficile suspectee: " + yesNo(consultation.getVoieAerienneDifficileSuspectee()));
        rawLines.add("");
        rawLines.add("Terrain / antecedents");
        rawLines.add("Allergies: " + value(consultation.getAllergiesResume()));
        rawLines.add("Traitements chroniques: " + value(consultation.getTraitementsChroniques()));
        rawLines.add("Antecedents medicaux: " + value(consultation.getAntecedentsMedicauxResume()));
        rawLines.add("Antecedents anesthesiques: " + value(consultation.getAntecedentsAnesthesiques()));
        rawLines.add("Jeune confirme: " + yesNo(consultation.getJeuneConfirme()) + " / Heures: " + value(consultation.getJeuneHeures()));
        rawLines.add("");
        rawLines.add("Synthese");
        rawLines.add("Evaluation cardio-respiratoire: " + value(consultation.getEvaluationCardioRespiratoire()));
        rawLines.add("Bilan / ECG / imagerie: " + value(consultation.getExamensComplementairesResume()));
        rawLines.add("Risque hemorragique: " + value(consultation.getRisqueHemorragique()));
        rawLines.add("Strategie anesthesique: " + value(consultation.getStrategieAnesthesique()));
        rawLines.add("Considerations: " + value(consultation.getConsiderations()));
        rawLines.add("Notes complementaires: " + value(consultation.getNotesComplementaires()));
        rawLines.add("");
        rawLines.add("Validation");
        rawLines.add("Consentement eclaire obtenu: " + yesNo(consultation.getConsentementEclaireObtenu()));
        rawLines.add("Consultation validee: " + yesNo(consultation.getValidee()));
        rawLines.add("Medecin redacteur: " + value(consultation.getMedecinNom()));
        rawLines.add("Medecin validateur: " + value(consultation.getValidatedByName()));
        rawLines.add("Date validation: " + (
                consultation.getValidatedAt() != null
                        ? consultation.getValidatedAt().format(dateTimeFormatter)
                        : "-"
        ));
        rawLines.add("Commentaire validation: " + value(consultation.getValidationCommentaire()));

        List<String> lines = wrapLines(rawLines, 92);
        return MinimalPdfBuilder.build(lines);
    }

    private List<String> wrapLines(List<String> lines, int maxLength) {
        List<String> result = new ArrayList<>();

        for (String line : lines) {
            String safeLine = normalizePdfText(line);
            if (safeLine.length() <= maxLength) {
                result.add(safeLine);
                continue;
            }

            String remaining = safeLine;
            while (remaining.length() > maxLength) {
                int breakIndex = remaining.lastIndexOf(' ', maxLength);
                if (breakIndex <= 0) {
                    breakIndex = maxLength;
                }
                result.add(remaining.substring(0, breakIndex).trim());
                remaining = remaining.substring(Math.min(breakIndex + 1, remaining.length())).trim();
            }

            if (!remaining.isBlank()) {
                result.add(remaining);
            }
        }

        return result;
    }

    private String buildPatientLabel(ConsultationPreAnesthesique consultation) {
        if (consultation.getPatient() == null) {
            return "-";
        }

        String fullName = ((consultation.getPatient().getPrenom() == null ? "" : consultation.getPatient().getPrenom().trim())
                + " "
                + (consultation.getPatient().getNom() == null ? "" : consultation.getPatient().getNom().trim())).trim();
        return fullName.isBlank() ? "-" : fullName;
    }

    private String yesNo(Boolean value) {
        return Boolean.TRUE.equals(value) ? "Oui" : "Non";
    }

    private String value(Object value) {
        if (value == null) {
            return "-";
        }
        String normalized = String.valueOf(value).trim();
        return normalized.isBlank() ? "-" : normalized;
    }

    private String normalizePdfText(String value) {
        return Normalizer.normalize(value == null ? "" : value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .replace("\\", "\\\\")
                .replace("(", "\\(")
                .replace(")", "\\)");
    }

    private static final class MinimalPdfBuilder {

        private MinimalPdfBuilder() {
        }

        private static byte[] build(List<String> lines) {
            List<List<String>> pages = paginate(lines, 40);
            List<byte[]> objects = new ArrayList<>();

            objects.add(ascii("<< /Type /Catalog /Pages 2 0 R >>"));

            StringBuilder kids = new StringBuilder();
            for (int index = 0; index < pages.size(); index++) {
                int pageObjectNumber = 3 + (index * 2);
                kids.append(pageObjectNumber).append(" 0 R ");
            }
            objects.add(ascii("<< /Type /Pages /Kids [" + kids.toString().trim() + "] /Count " + pages.size() + " >>"));

            for (int index = 0; index < pages.size(); index++) {
                int contentObjectNumber = 4 + (index * 2);
                objects.add(ascii("<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Resources << /Font << /F1 "
                        + (3 + pages.size() * 2) + " 0 R >> >> /Contents " + contentObjectNumber + " 0 R >>"));

                String content = buildPageContent(pages.get(index));
                byte[] contentBytes = ascii(content);
                objects.add(ascii("<< /Length " + contentBytes.length + " >>\nstream\n" + content + "\nendstream"));
            }

            objects.add(ascii("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>"));

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            List<Integer> offsets = new ArrayList<>();
            writeAscii(output, "%PDF-1.4\n");

            for (int index = 0; index < objects.size(); index++) {
                offsets.add(output.size());
                writeAscii(output, (index + 1) + " 0 obj\n");
                output.writeBytes(objects.get(index));
                writeAscii(output, "\nendobj\n");
            }

            int xrefOffset = output.size();
            writeAscii(output, "xref\n0 " + (objects.size() + 1) + "\n");
            writeAscii(output, "0000000000 65535 f \n");
            for (Integer offset : offsets) {
                writeAscii(output, String.format("%010d 00000 n \n", offset));
            }
            writeAscii(output, "trailer\n<< /Size " + (objects.size() + 1) + " /Root 1 0 R >>\n");
            writeAscii(output, "startxref\n" + xrefOffset + "\n%%EOF");

            return output.toByteArray();
        }

        private static List<List<String>> paginate(List<String> lines, int linesPerPage) {
            List<List<String>> pages = new ArrayList<>();
            for (int index = 0; index < lines.size(); index += linesPerPage) {
                int end = Math.min(index + linesPerPage, lines.size());
                pages.add(new ArrayList<>(lines.subList(index, end)));
            }
            if (pages.isEmpty()) {
                pages.add(List.of(""));
            }
            return pages;
        }

        private static String buildPageContent(List<String> lines) {
            StringBuilder content = new StringBuilder();
            content.append("BT\n/F1 11 Tf\n50 790 Td\n");
            boolean first = true;
            for (String line : lines) {
                if (!first) {
                    content.append("0 -18 Td\n");
                }
                content.append("(").append(line).append(") Tj\n");
                first = false;
            }
            content.append("ET");
            return content.toString();
        }

        private static byte[] ascii(String value) {
            return value.getBytes(StandardCharsets.US_ASCII);
        }

        private static void writeAscii(ByteArrayOutputStream output, String value) {
            output.writeBytes(ascii(value));
        }
    }
}
