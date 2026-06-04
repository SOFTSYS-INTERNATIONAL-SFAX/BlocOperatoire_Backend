package com.tn.softsys.blocoperatoire.service;

import com.tn.softsys.blocoperatoire.domain.Intervention;
import com.tn.softsys.blocoperatoire.domain.User;
import com.tn.softsys.blocoperatoire.domain.oms.ChecklistOms;
import com.tn.softsys.blocoperatoire.domain.oms.OmsSignIn;
import com.tn.softsys.blocoperatoire.domain.oms.OmsSignOut;
import com.tn.softsys.blocoperatoire.domain.oms.OmsTimeOut;
import com.tn.softsys.blocoperatoire.dto.oms.OmsChecklistReportDownload;
import com.tn.softsys.blocoperatoire.exception.ResourceNotFoundException;
import com.tn.softsys.blocoperatoire.repository.ChecklistOmsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OmsChecklistReportService {

    private final ChecklistOmsRepository checklistOmsRepository;
    private final AuditLogService auditLogService;
    private final AuditContextService auditContextService;

    public OmsChecklistReportDownload download(UUID interventionId) {
        ChecklistOms checklist = checklistOmsRepository.findByIntervention_InterventionId(interventionId)
                .orElseThrow(() -> new ResourceNotFoundException("OMS checklist not found"));

        String fileName = buildFileName(checklist.getIntervention());

        auditLogService.log(
                auditContextService.getCurrentUserOrNull(),
                "OMS_CHECKLIST_REPORT_DOWNLOAD",
                "OMS_CHECKLIST",
                checklist.getChecklistId(),
                "Export PDF checklist OMS intervention=" + interventionId,
                auditContextService.getClientIp()
        );

        return new OmsChecklistReportDownload(fileName, buildPdf(checklist));
    }

    private String buildFileName(Intervention intervention) {
        String baseName = intervention == null
                ? "checklist-oms"
                : value(intervention.getNomIntervention()).toLowerCase().replaceAll("[^a-z0-9]+", "-");

        if (baseName.isBlank() || "-".equals(baseName)) {
            baseName = "checklist-oms";
        }

        return "oms-" + baseName + ".pdf";
    }

    private byte[] buildPdf(ChecklistOms checklist) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        List<ReportField> summaryFields = new ArrayList<>();
        summaryFields.add(new ReportField("Patient", buildPatientLabel(checklist)));
        summaryFields.add(new ReportField("Intervention", value(checklist.getIntervention() != null ? checklist.getIntervention().getNomIntervention() : null)));
        summaryFields.add(new ReportField("Code acte", value(checklist.getIntervention() != null ? checklist.getIntervention().getCodeActe() : null)));
        summaryFields.add(new ReportField("Date intervention", checklist.getIntervention() != null && checklist.getIntervention().getDateIntervention() != null
                ? checklist.getIntervention().getDateIntervention().format(dateFormatter) : "-"));
        summaryFields.add(new ReportField("Heure intervention", value(checklist.getIntervention() != null ? checklist.getIntervention().getHeureDebut() : null)));
        summaryFields.add(new ReportField("Chirurgien", buildAssignedUserLabel(checklist.getIntervention() != null ? checklist.getIntervention().getChirurgien() : null)));
        summaryFields.add(new ReportField("Anesthesiste", buildAssignedUserLabel(checklist.getIntervention() != null ? checklist.getIntervention().getAnesthesiste() : null)));
        summaryFields.add(new ReportField("Statut checklist", buildChecklistStatus(checklist)));

        List<ReportSection> sections = List.of(
                buildSignInSection(checklist.getSignIn(), dateTimeFormatter),
                buildTimeOutSection(checklist.getTimeOut(), dateTimeFormatter),
                buildSignOutSection(checklist.getSignOut(), dateTimeFormatter)
        );

        return StyledPdfBuilder.build(
                "Checklist OMS",
                "Rapport de tracabilite peri-operatoire",
                "Genere le " + LocalDateTime.now().format(dateTimeFormatter),
                summaryFields,
                sections
        );
    }

    private ReportSection buildSignInSection(OmsSignIn signIn, DateTimeFormatter dateTimeFormatter) {
        List<ReportField> rows = new ArrayList<>();

        if (signIn == null) {
            rows.add(new ReportField("Statut", "Non valide"));
            return new ReportSection("01. Sign In", rows);
        }

        rows.add(new ReportField("Statut", "Valide"));
        rows.add(new ReportField("Identite patient confirmee", yesNo(signIn.getPatientIdentityConfirmed())));
        rows.add(new ReportField("Site operatoire marque", yesNo(signIn.getSiteMarked())));
        rows.add(new ReportField("Machine anesthesie verifiee", yesNo(signIn.getAnesthesiaMachineChecked())));
        rows.add(new ReportField("Oxymetre de pouls operationnel", yesNo(signIn.getPulseOximeterWorking())));
        rows.add(new ReportField("Risque voie aerienne difficile", yesNo(signIn.getDifficultAirwayRisk())));
        rows.add(new ReportField("Risque aspiration", yesNo(signIn.getAspirationRisk())));
        rows.add(new ReportField("Risque hemorragique", yesNo(signIn.getHemorrhageRisk())));
        rows.add(new ReportField("Produits sanguins disponibles", yesNo(signIn.getBloodProductsAvailable())));
        rows.add(new ReportField("Allergies", value(signIn.getAllergies())));
        rows.add(new ReportField("Valide par", buildAssignedUserLabel(signIn.getCompletedBy())));
        rows.add(new ReportField("Horodatage", formatDateTime(signIn.getCompletedAt(), dateTimeFormatter)));

        return new ReportSection("01. Sign In", rows);
    }

    private ReportSection buildTimeOutSection(OmsTimeOut timeOut, DateTimeFormatter dateTimeFormatter) {
        List<ReportField> rows = new ArrayList<>();

        if (timeOut == null) {
            rows.add(new ReportField("Statut", "Non valide"));
            return new ReportSection("02. Time Out", rows);
        }

        rows.add(new ReportField("Statut", "Valide"));
        rows.add(new ReportField("Equipe presentee", yesNo(timeOut.getTeamIntroduced())));
        rows.add(new ReportField("Nom patient confirme", yesNo(timeOut.getPatientNameConfirmed())));
        rows.add(new ReportField("Intervention confirmee", yesNo(timeOut.getInterventionConfirmed())));
        rows.add(new ReportField("Site confirme", yesNo(timeOut.getSiteConfirmed())));
        rows.add(new ReportField("Antibioprophylaxie administree", yesNo(timeOut.getAntibioticProphylaxisGiven())));
        rows.add(new ReportField("Imagerie affichee", yesNo(timeOut.getImagingDisplayed())));
        rows.add(new ReportField("Evenements chirurgien", value(timeOut.getCriticalEventsSurgeon())));
        rows.add(new ReportField("Evenements anesthesie", value(timeOut.getCriticalEventsAnesthesia())));
        rows.add(new ReportField("Valide par", buildAssignedUserLabel(timeOut.getCompletedBy())));
        rows.add(new ReportField("Horodatage", formatDateTime(timeOut.getCompletedAt(), dateTimeFormatter)));

        return new ReportSection("02. Time Out", rows);
    }

    private ReportSection buildSignOutSection(OmsSignOut signOut, DateTimeFormatter dateTimeFormatter) {
        List<ReportField> rows = new ArrayList<>();

        if (signOut == null) {
            rows.add(new ReportField("Statut", "Non valide"));
            return new ReportSection("03. Sign Out", rows);
        }

        rows.add(new ReportField("Statut", "Valide"));
        rows.add(new ReportField("Intervention enregistree", yesNo(signOut.getInterventionRecorded())));
        rows.add(new ReportField("Compte des instruments correct", yesNo(signOut.getInstrumentsCountCorrect())));
        rows.add(new ReportField("Prelevements etiquetes", yesNo(signOut.getSpecimensLabeled())));
        rows.add(new ReportField("Plan de recuperation confirme", yesNo(signOut.getRecoveryPlanConfirmed())));
        rows.add(new ReportField("Problemes d equipement", value(signOut.getEquipmentProblems())));
        rows.add(new ReportField("Validation chirurgien",
                buildValidationLabel(signOut.getSurgeonValidated(), signOut.getSurgeonValidatedByName(), signOut.getSurgeonValidatedAt(), dateTimeFormatter)));
        rows.add(new ReportField("Validation anesthesiste",
                buildValidationLabel(signOut.getAnesthesisteValidated(), signOut.getAnesthesisteValidatedByName(), signOut.getAnesthesisteValidatedAt(), dateTimeFormatter)));
        rows.add(new ReportField("Validation finale", buildAssignedUserLabel(signOut.getCompletedBy())));
        rows.add(new ReportField("Horodatage final", formatDateTime(signOut.getCompletedAt(), dateTimeFormatter)));

        return new ReportSection("03. Sign Out", rows);
    }

    private String buildValidationLabel(Boolean validated, String validatedBy, LocalDateTime validatedAt, DateTimeFormatter formatter) {
        if (!Boolean.TRUE.equals(validated)) {
            return "Non valide";
        }

        return value(validatedBy) + "  |  " + formatDateTime(validatedAt, formatter);
    }

    private String buildChecklistStatus(ChecklistOms checklist) {
        boolean hasSignIn = checklist.getSignIn() != null;
        boolean hasTimeOut = checklist.getTimeOut() != null;
        boolean hasSignOut = checklist.getSignOut() != null;

        if (hasSignIn && hasTimeOut && hasSignOut) {
            return "Complete";
        }
        if (hasSignIn || hasTimeOut || hasSignOut) {
            return "En attente";
        }
        return "A faire";
    }

    private String buildPatientLabel(ChecklistOms checklist) {
        if (checklist == null || checklist.getPatient() == null) {
            return "-";
        }

        String fullName = ((checklist.getPatient().getPrenom() == null ? "" : checklist.getPatient().getPrenom().trim())
                + " "
                + (checklist.getPatient().getNom() == null ? "" : checklist.getPatient().getNom().trim())).trim();
        return fullName.isBlank() ? "-" : fullName;
    }

    private String buildAssignedUserLabel(User user) {
        if (user == null) {
            return "-";
        }
        String fullName = ((user.getPrenom() == null ? "" : user.getPrenom().trim())
                + " "
                + (user.getNom() == null ? "" : user.getNom().trim())).trim();
        return fullName.isBlank() ? value(user.getEmail()) : "Dr. " + fullName;
    }

    private String yesNo(Boolean value) {
        if (value == null) {
            return "-";
        }
        return Boolean.TRUE.equals(value) ? "Oui" : "Non";
    }

    private String value(Object value) {
        if (value == null) {
            return "-";
        }
        String normalized = String.valueOf(value).trim();
        return normalized.isBlank() ? "-" : normalized;
    }

    private String formatDateTime(LocalDateTime value, DateTimeFormatter formatter) {
        return value == null ? "-" : value.format(formatter);
    }

    private static String normalizePdfText(String value) {
        return Normalizer.normalize(value == null ? "" : value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .replace("\\", "\\\\")
                .replace("(", "\\(")
                .replace(")", "\\)");
    }

    private record ReportField(String label, String value) {
    }

    private record ReportSection(String title, List<ReportField> fields) {
    }

    private static final class StyledPdfBuilder {

        private static final float PAGE_WIDTH = 595f;
        private static final float PAGE_HEIGHT = 842f;
        private static final float MARGIN = 42f;
        private static final float CONTENT_WIDTH = PAGE_WIDTH - (MARGIN * 2);
        private static final float FOOTER_MARGIN = 56f;
        private static final int FONT_REGULAR = 1;
        private static final int FONT_BOLD = 2;

        private final List<String> pageContents = new ArrayList<>();
        private StringBuilder currentPage;
        private float cursorY;
        private final String title;
        private final String subtitle;
        private final String generatedAt;

        private StyledPdfBuilder(String title, String subtitle, String generatedAt) {
            this.title = title;
            this.subtitle = subtitle;
            this.generatedAt = generatedAt;
        }

        private byte[] render(List<ReportField> summaryFields, List<ReportSection> sections) {
            startPage(true);
            drawSummary(summaryFields);

            for (ReportSection section : sections) {
                drawSection(section);
            }

            closePage();
            return buildPdfBinary(pageContents);
        }

        private void drawSummary(List<ReportField> fields) {
            drawSectionTitle("Synthese intervention");

            int index = 0;
            while (index < fields.size()) {
                ReportField left = fields.get(index);
                ReportField right = index + 1 < fields.size() ? fields.get(index + 1) : null;

                float cardWidth = (CONTENT_WIDTH - 12f) / 2f;
                float leftHeight = estimateMetaCardHeight(left, cardWidth);
                float rightHeight = right == null ? leftHeight : estimateMetaCardHeight(right, cardWidth);
                float rowHeight = Math.max(leftHeight, rightHeight);

                ensureSpace(rowHeight + 12f);

                drawMetaCard(MARGIN, cursorY, cardWidth, rowHeight, left);
                if (right != null) {
                    drawMetaCard(MARGIN + cardWidth + 12f, cursorY, cardWidth, rowHeight, right);
                }

                cursorY -= rowHeight + 12f;
                index += 2;
            }
        }

        private void drawSection(ReportSection section) {
            ensureSpace(52f);
            drawSectionTitle(section.title());

            for (ReportField field : section.fields()) {
                float rowHeight = estimateFieldHeight(field);
                ensureSpace(rowHeight + 10f);
                drawFieldRow(field, rowHeight);
                cursorY -= rowHeight + 8f;
            }
        }

        private void drawSectionTitle(String label) {
            ensureSpace(40f);
            drawFilledRect(MARGIN, cursorY - 26f, CONTENT_WIDTH, 28f, 0.92f, 0.96f, 0.99f);
            drawFilledRect(MARGIN, cursorY - 26f, 6f, 28f, 0.07f, 0.43f, 0.50f);
            drawText(MARGIN + 16f, cursorY - 8f, 13f, FONT_BOLD, 0.11f, 0.17f, 0.29f, label);
            cursorY -= 38f;
        }

        private float estimateMetaCardHeight(ReportField field, float width) {
            List<String> valueLines = wrap(field.value(), width - 24f, 11f);
            return 42f + (valueLines.size() * 13f);
        }

        private void drawMetaCard(float x, float topY, float width, float height, ReportField field) {
            drawFilledRect(x, topY - height, width, height, 0.97f, 0.98f, 0.99f);
            drawStrokeRect(x, topY - height, width, height, 0.84f, 0.88f, 0.94f, 0.8f);
            drawText(x + 12f, topY - 16f, 8.5f, FONT_BOLD, 0.07f, 0.43f, 0.50f, field.label().toUpperCase());

            float valueY = topY - 32f;
            List<String> valueLines = wrap(field.value(), width - 24f, 11f);
            for (String line : valueLines) {
                drawText(x + 12f, valueY, 11f, FONT_BOLD, 0.11f, 0.17f, 0.29f, line);
                valueY -= 13f;
            }
        }

        private float estimateFieldHeight(ReportField field) {
            int lineCount = wrap(field.value(), CONTENT_WIDTH - 34f, 10.5f).size();
            return 28f + (lineCount * 12f);
        }

        private void drawFieldRow(ReportField field, float height) {
            drawFilledRect(MARGIN, cursorY - height, CONTENT_WIDTH, height, 1f, 1f, 1f);
            drawStrokeRect(MARGIN, cursorY - height, CONTENT_WIDTH, height, 0.89f, 0.92f, 0.96f, 0.8f);
            drawText(MARGIN + 12f, cursorY - 15f, 9f, FONT_BOLD, 0.34f, 0.43f, 0.55f, field.label());

            float textY = cursorY - 30f;
            for (String line : wrap(field.value(), CONTENT_WIDTH - 24f, 10.5f)) {
                drawText(MARGIN + 12f, textY, 10.5f, FONT_REGULAR, 0.11f, 0.17f, 0.29f, line);
                textY -= 12f;
            }
        }

        private void ensureSpace(float requiredHeight) {
            if (cursorY - requiredHeight < FOOTER_MARGIN) {
                startPage(false);
            }
        }

        private void startPage(boolean firstPage) {
            if (currentPage != null) {
                closePage();
            }

            currentPage = new StringBuilder();
            cursorY = 810f;

            drawFilledRect(0f, PAGE_HEIGHT - 90f, PAGE_WIDTH, 90f, 0.07f, 0.29f, 0.36f);
            drawText(MARGIN, PAGE_HEIGHT - 42f, firstPage ? 24f : 18f, FONT_BOLD, 1f, 1f, 1f, title);
            drawText(MARGIN, PAGE_HEIGHT - 62f, 10.5f, FONT_REGULAR, 0.88f, 0.96f, 0.98f, subtitle);
            drawText(PAGE_WIDTH - 185f, PAGE_HEIGHT - 42f, 8.5f, FONT_REGULAR, 0.88f, 0.96f, 0.98f, generatedAt);
            cursorY = PAGE_HEIGHT - 112f;
        }

        private void closePage() {
            drawText(MARGIN, 28f, 8.5f, FONT_REGULAR, 0.47f, 0.55f, 0.67f, "OperaBloc - Checklist OMS");
            pageContents.add(currentPage.toString());
        }

        private List<String> wrap(String text, float width, float fontSize) {
            String normalized = text == null || text.isBlank() ? "-" : text.trim();
            int maxChars = Math.max(16, Math.min(90, (int) Math.floor(width / Math.max(fontSize * 0.5f, 5f))));
            List<String> lines = new ArrayList<>();

            String remaining = normalized;
            while (remaining.length() > maxChars) {
                int splitIndex = remaining.lastIndexOf(' ', maxChars);
                if (splitIndex <= 0) {
                    splitIndex = maxChars;
                }
                lines.add(remaining.substring(0, splitIndex).trim());
                remaining = remaining.substring(Math.min(splitIndex + 1, remaining.length())).trim();
            }

            if (!remaining.isBlank()) {
                lines.add(remaining);
            }

            return lines.isEmpty() ? List.of("-") : lines;
        }

        private void drawFilledRect(float x, float y, float width, float height, float r, float g, float b) {
            currentPage.append(formatColor(r, g, b)).append(" rg\n");
            currentPage.append(formatNumber(x)).append(" ")
                    .append(formatNumber(y)).append(" ")
                    .append(formatNumber(width)).append(" ")
                    .append(formatNumber(height)).append(" re f\n");
        }

        private void drawStrokeRect(float x, float y, float width, float height, float r, float g, float b, float lineWidth) {
            currentPage.append(formatNumber(lineWidth)).append(" w\n");
            currentPage.append(formatColor(r, g, b)).append(" RG\n");
            currentPage.append(formatNumber(x)).append(" ")
                    .append(formatNumber(y)).append(" ")
                    .append(formatNumber(width)).append(" ")
                    .append(formatNumber(height)).append(" re S\n");
        }

        private void drawText(float x, float y, float size, int fontIndex, float r, float g, float b, String text) {
            currentPage.append("BT\n");
            currentPage.append("/F").append(fontIndex).append(" ").append(formatNumber(size)).append(" Tf\n");
            currentPage.append(formatColor(r, g, b)).append(" rg\n");
            currentPage.append(formatNumber(x)).append(" ").append(formatNumber(y)).append(" Td\n");
            currentPage.append("(").append(normalizePdfText(text)).append(") Tj\n");
            currentPage.append("ET\n");
        }

        private String formatColor(float r, float g, float b) {
            return formatNumber(r) + " " + formatNumber(g) + " " + formatNumber(b);
        }

        private String formatNumber(float value) {
            return String.format(java.util.Locale.US, "%.2f", value);
        }

        private byte[] buildPdfBinary(List<String> pageStreams) {
            List<byte[]> objects = new ArrayList<>();

            objects.add(ascii("<< /Type /Catalog /Pages 2 0 R >>"));

            StringBuilder kids = new StringBuilder();
            for (int index = 0; index < pageStreams.size(); index++) {
                int pageObjectNumber = 3 + (index * 2);
                kids.append(pageObjectNumber).append(" 0 R ");
            }
            objects.add(ascii("<< /Type /Pages /Kids [" + kids.toString().trim() + "] /Count " + pageStreams.size() + " >>"));

            int fontRegularObject = 3 + (pageStreams.size() * 2);
            int fontBoldObject = fontRegularObject + 1;

            for (int index = 0; index < pageStreams.size(); index++) {
                int contentObjectNumber = 4 + (index * 2);
                objects.add(ascii("<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Resources << /Font << /F1 "
                        + fontRegularObject + " 0 R /F2 " + fontBoldObject + " 0 R >> >> /Contents " + contentObjectNumber + " 0 R >>"));

                byte[] contentBytes = ascii(pageStreams.get(index));
                objects.add(ascii("<< /Length " + contentBytes.length + " >>\nstream\n" + pageStreams.get(index) + "\nendstream"));
            }

            objects.add(ascii("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>"));
            objects.add(ascii("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold >>"));

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
                writeAscii(output, String.format(java.util.Locale.US, "%010d 00000 n \n", offset));
            }
            writeAscii(output, "trailer\n<< /Size " + (objects.size() + 1) + " /Root 1 0 R >>\n");
            writeAscii(output, "startxref\n" + xrefOffset + "\n%%EOF");

            return output.toByteArray();
        }

        private byte[] ascii(String value) {
            return value.getBytes(StandardCharsets.US_ASCII);
        }

        private void writeAscii(ByteArrayOutputStream output, String value) {
            output.writeBytes(ascii(value));
        }

        private static byte[] build(
                String title,
                String subtitle,
                String generatedAt,
                List<ReportField> summaryFields,
                List<ReportSection> sections
        ) {
            return new StyledPdfBuilder(title, subtitle, generatedAt).render(summaryFields, sections);
        }
    }
}
