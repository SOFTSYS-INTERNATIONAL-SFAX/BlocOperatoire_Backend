package com.tn.softsys.blocoperatoire.service;

import com.tn.softsys.blocoperatoire.domain.*;
import com.tn.softsys.blocoperatoire.dto.compterendu.*;
import com.tn.softsys.blocoperatoire.exception.ResourceNotFoundException;
import com.tn.softsys.blocoperatoire.repository.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CompteRenduService {

    private static final String MODULE = "COMPTE_RENDU";

    private final CompteRenduTemplateRepository templateRepository;
    private final CompteRenduDocumentRepository documentRepository;
    private final PatientRepository patientRepository;
    private final InterventionRepository interventionRepository;
    private final AuditLogService auditLogService;
    private final AuditContextService auditContextService;

    @Value("${app.storage.compte-rendu-audio-dir:uploads/compte-rendu-audio}")
    private String audioStorageDir;

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(rootPath());
        } catch (IOException e) {
            throw new IllegalStateException("Unable to initialize report audio storage", e);
        }
    }

    @Transactional(readOnly = true)
    public List<CompteRenduTemplateResponseDTO> listTemplates() {
        return templateRepository.findAllByOrderByUpdatedAtDesc().stream().map(this::toTemplateResponse).toList();
    }

    public CompteRenduTemplateResponseDTO createTemplate(CompteRenduTemplateRequestDTO dto) {
        User currentUser = auditContextService.getCurrentUserOrNull();
        CompteRenduTemplate saved = templateRepository.save(
                CompteRenduTemplate.builder()
                        .name(dto.getName().trim())
                        .libelle(dto.getLibelle().trim())
                        .blocLabel(dto.getBlocLabel().trim())
                        .content(dto.getContent().trim())
                        .createdBy(currentUser)
                        .createdByDisplayName(resolveUserDisplayName(currentUser))
                        .build()
        );
        audit("COMPTE_RENDU_TEMPLATE_CREATE", saved.getTemplateId(), "Create template " + saved.getName());
        return toTemplateResponse(saved);
    }

    public CompteRenduTemplateResponseDTO updateTemplate(UUID templateId, CompteRenduTemplateRequestDTO dto) {
        CompteRenduTemplate template = templateRepository.findById(templateId)
                .orElseThrow(() -> new ResourceNotFoundException("Report template not found"));
        template.setName(dto.getName().trim());
        template.setLibelle(dto.getLibelle().trim());
        template.setBlocLabel(dto.getBlocLabel().trim());
        template.setContent(dto.getContent().trim());
        audit("COMPTE_RENDU_TEMPLATE_UPDATE", template.getTemplateId(), "Update template " + template.getName());
        return toTemplateResponse(template);
    }

    public void deleteTemplate(UUID templateId) {
        CompteRenduTemplate template = templateRepository.findById(templateId)
                .orElseThrow(() -> new ResourceNotFoundException("Report template not found"));
        audit("COMPTE_RENDU_TEMPLATE_DELETE", template.getTemplateId(), "Delete template " + template.getName());
        templateRepository.delete(template);
    }

    @Transactional(readOnly = true)
    public List<CompteRenduDocumentResponseDTO> listDocumentsByPatient(UUID patientId) {
        return documentRepository.findByPatientPatientIdOrderByUpdatedAtDesc(patientId)
                .stream()
                .map(this::toDocumentResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CompteRenduDocumentResponseDTO getDocument(UUID documentId) {
        return toDocumentResponse(documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Report document not found")));
    }

    public CompteRenduDocumentResponseDTO createDocument(CompteRenduDocumentRequestDTO dto) {
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
        Intervention intervention = resolveIntervention(dto.getInterventionId(), patient.getPatientId());
        CompteRenduTemplate template = resolveTemplate(dto.getTemplateId());
        User currentUser = auditContextService.getCurrentUserOrNull();

        CompteRenduDocument saved = documentRepository.save(
                CompteRenduDocument.builder()
                        .patient(patient)
                        .intervention(intervention)
                        .template(template)
                        .title(dto.getTitle().trim())
                        .content(dto.getContent().trim())
                        .status(dto.getStatus())
                        .authoredBy(currentUser)
                        .authoredByDisplayName(resolveUserDisplayName(currentUser))
                        .dictationTemplateId(template != null ? template.getTemplateId() : null)
                        .build()
        );

        audit("COMPTE_RENDU_DOCUMENT_CREATE", saved.getDocumentId(), "Create report document " + saved.getTitle());
        return toDocumentResponse(saved);
    }

    public CompteRenduDocumentResponseDTO updateDocument(UUID documentId, CompteRenduDocumentRequestDTO dto) {
        CompteRenduDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Report document not found"));

        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
        Intervention intervention = resolveIntervention(dto.getInterventionId(), patient.getPatientId());
        CompteRenduTemplate template = resolveTemplate(dto.getTemplateId());
        User currentUser = auditContextService.getCurrentUserOrNull();

        document.setPatient(patient);
        document.setIntervention(intervention);
        document.setTemplate(template);
        document.setTitle(dto.getTitle().trim());
        document.setContent(dto.getContent().trim());
        document.setStatus(dto.getStatus());
        document.setAuthoredBy(currentUser);
        document.setAuthoredByDisplayName(resolveUserDisplayName(currentUser));
        document.setDictationTemplateId(template != null ? template.getTemplateId() : null);

        audit("COMPTE_RENDU_DOCUMENT_UPDATE", document.getDocumentId(), "Update report document " + document.getTitle());
        return toDocumentResponse(document);
    }

    public CompteRenduDocumentResponseDTO uploadAudio(UUID documentId, MultipartFile file, Integer durationSeconds, UUID templateId) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Audio file is required");
        }

        CompteRenduDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Report document not found"));

        String originalFileName = sanitizeFileName(file.getOriginalFilename());
        String extension = getExtension(originalFileName);
        String storageFileName = UUID.randomUUID() + extension;
        Path target = rootPath().resolve(storageFileName);

        try {
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to store dictation audio", e);
        }

        document.setAudioOriginalFileName(originalFileName);
        document.setAudioStorageFileName(storageFileName);
        document.setAudioMimeType(resolveMimeType(file));
        document.setAudioSizeBytes(file.getSize());
        document.setAudioStoragePath(target.toString());
        document.setDictationDurationSeconds(durationSeconds);
        document.setDictationTemplateId(templateId);

        audit("COMPTE_RENDU_AUDIO_UPLOAD", document.getDocumentId(), "Upload dictation audio for document " + document.getTitle());
        return toDocumentResponse(document);
    }

    @Transactional(readOnly = true)
    public CompteRenduAudioDownload downloadAudio(UUID documentId) {
        CompteRenduDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Report document not found"));

        if (document.getAudioStoragePath() == null || document.getAudioStoragePath().isBlank()) {
            throw new ResourceNotFoundException("No audio linked to this report document");
        }

        Path filePath = Paths.get(document.getAudioStoragePath());
        if (!Files.exists(filePath)) {
            throw new ResourceNotFoundException("Stored audio file not found");
        }

        audit("COMPTE_RENDU_AUDIO_DOWNLOAD", document.getDocumentId(), "Download dictation audio for document " + document.getTitle());
        Resource resource = new FileSystemResource(filePath);
        return new CompteRenduAudioDownload(
                document.getAudioOriginalFileName(),
                document.getAudioMimeType(),
                document.getAudioSizeBytes(),
                resource
        );
    }

    private CompteRenduTemplate resolveTemplate(UUID templateId) {
        if (templateId == null) {
            return null;
        }
        return templateRepository.findById(templateId)
                .orElseThrow(() -> new ResourceNotFoundException("Report template not found"));
    }

    private Intervention resolveIntervention(UUID interventionId, UUID patientId) {
        if (interventionId == null) {
            return null;
        }
        Intervention intervention = interventionRepository.findById(interventionId)
                .orElseThrow(() -> new ResourceNotFoundException("Intervention not found"));
        if (intervention.getPatient() == null || !intervention.getPatient().getPatientId().equals(patientId)) {
            throw new IllegalArgumentException("Intervention does not belong to patient");
        }
        return intervention;
    }

    private void audit(String action, UUID referenceId, String details) {
        auditLogService.log(
                auditContextService.getCurrentUserOrNull(),
                action,
                MODULE,
                referenceId,
                details,
                auditContextService.getClientIp()
        );
    }

    private CompteRenduTemplateResponseDTO toTemplateResponse(CompteRenduTemplate entity) {
        return CompteRenduTemplateResponseDTO.builder()
                .templateId(entity.getTemplateId())
                .name(entity.getName())
                .libelle(entity.getLibelle())
                .blocLabel(entity.getBlocLabel())
                .content(entity.getContent())
                .createdByUserId(entity.getCreatedBy() != null ? entity.getCreatedBy().getUserId() : null)
                .createdBy(entity.getCreatedByDisplayName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private CompteRenduDocumentResponseDTO toDocumentResponse(CompteRenduDocument entity) {
        String patientLabel = entity.getPatient() != null
                ? ((entity.getPatient().getPrenom() == null ? "" : entity.getPatient().getPrenom().trim()) + " "
                + (entity.getPatient().getNom() == null ? "" : entity.getPatient().getNom().trim())).trim()
                : null;
        String interventionLabel = entity.getIntervention() != null ? entity.getIntervention().getNomIntervention() : null;
        return CompteRenduDocumentResponseDTO.builder()
                .documentId(entity.getDocumentId())
                .patientId(entity.getPatient() != null ? entity.getPatient().getPatientId() : null)
                .patientLabel(patientLabel)
                .interventionId(entity.getIntervention() != null ? entity.getIntervention().getInterventionId() : null)
                .interventionLabel(interventionLabel)
                .templateId(entity.getTemplate() != null ? entity.getTemplate().getTemplateId() : null)
                .templateName(entity.getTemplate() != null ? entity.getTemplate().getName() : null)
                .blocLabel(entity.getIntervention() != null && entity.getIntervention().getSalle() != null
                        ? entity.getIntervention().getSalle().getNom()
                        : entity.getTemplate() != null ? entity.getTemplate().getBlocLabel() : null)
                .title(entity.getTitle())
                .content(entity.getContent())
                .status(entity.getStatus())
                .authoredByUserId(entity.getAuthoredBy() != null ? entity.getAuthoredBy().getUserId() : null)
                .authoredBy(entity.getAuthoredByDisplayName())
                .dictationTemplateId(entity.getDictationTemplateId())
                .audioOriginalFileName(entity.getAudioOriginalFileName())
                .audioMimeType(entity.getAudioMimeType())
                .audioSizeBytes(entity.getAudioSizeBytes())
                .dictationDurationSeconds(entity.getDictationDurationSeconds())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private Path rootPath() {
        return Paths.get(audioStorageDir).toAbsolutePath().normalize();
    }

    private String sanitizeFileName(String value) {
        String raw = value == null ? "dictation.webm" : value.trim();
        String cleaned = raw.replaceAll("[\\\\/:*?\"<>|]", "_").replaceAll("\\s+", " ");
        return cleaned.isBlank() ? "dictation.webm" : cleaned;
    }

    private String getExtension(String fileName) {
        int lastDot = fileName.lastIndexOf(".");
        return lastDot < 0 ? "" : fileName.substring(lastDot).toLowerCase();
    }

    private String resolveMimeType(MultipartFile file) {
        String mime = file.getContentType();
        return (mime == null || mime.isBlank()) ? "application/octet-stream" : mime;
    }

    private String resolveUserDisplayName(User user) {
        if (user == null) {
            return null;
        }
        String label = ((user.getPrenom() == null ? "" : user.getPrenom().trim()) + " "
                + (user.getNom() == null ? "" : user.getNom().trim())).trim();
        return label.isBlank() ? user.getEmail() : label;
    }
}