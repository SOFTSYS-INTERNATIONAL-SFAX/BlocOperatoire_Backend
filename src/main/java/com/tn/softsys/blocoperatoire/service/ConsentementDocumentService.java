package com.tn.softsys.blocoperatoire.service;

import com.tn.softsys.blocoperatoire.domain.Consentement;
import com.tn.softsys.blocoperatoire.domain.ConsentementDocument;
import com.tn.softsys.blocoperatoire.domain.User;
import com.tn.softsys.blocoperatoire.dto.consentement.ConsentementDocumentDownload;
import com.tn.softsys.blocoperatoire.dto.consentement.ConsentementDocumentResponseDTO;
import com.tn.softsys.blocoperatoire.exception.ResourceNotFoundException;
import com.tn.softsys.blocoperatoire.repository.ConsentementDocumentRepository;
import com.tn.softsys.blocoperatoire.repository.ConsentementRepository;
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
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ConsentementDocumentService {

    private static final String MODULE = "CONSENTEMENT_DOCUMENT";
    private static final long MAX_SIZE = 8L * 1024L * 1024L;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            ".pdf", ".doc", ".docx", ".jpg", ".jpeg", ".png", ".txt"
    );
    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "image/jpeg",
            "image/png",
            "text/plain",
            "application/octet-stream"
    );

    private final ConsentementDocumentRepository repository;
    private final ConsentementRepository consentementRepository;
    private final AuditLogService auditLogService;
    private final AuditContextService auditContextService;

    @Value("${app.storage.consentement-documents-dir:uploads/consentement-documents}")
    private String storageDir;

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(rootPath());
        } catch (IOException e) {
            throw new IllegalStateException("Unable to initialize consent document storage", e);
        }
    }

    @Transactional(readOnly = true)
    public List<ConsentementDocumentResponseDTO> listByConsentement(UUID consentId) {
        if (!consentementRepository.existsById(consentId)) {
            throw new ResourceNotFoundException("Consentement not found");
        }

        return repository.findByConsentementConsentIdOrderByUploadedAtDesc(consentId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ConsentementDocumentResponseDTO upload(UUID consentId, String title, MultipartFile file) {
        validateFile(file);

        Consentement consentement = consentementRepository.findById(consentId)
                .orElseThrow(() -> new ResourceNotFoundException("Consentement not found"));

        String originalFileName = sanitizeFileName(file.getOriginalFilename());
        String extension = getExtension(originalFileName);
        String storageFileName = UUID.randomUUID() + extension;
        Path target = rootPath().resolve(storageFileName);

        try {
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to store consent document", e);
        }

        User currentUser = auditContextService.getCurrentUserOrNull();
        String normalizedTitle = normalizeTitle(title, originalFileName);

        ConsentementDocument saved = repository.save(
                ConsentementDocument.builder()
                        .consentement(consentement)
                        .patient(consentement.getPatient())
                        .intervention(consentement.getIntervention())
                        .uploadedBy(currentUser)
                        .title(normalizedTitle)
                        .originalFileName(originalFileName)
                        .storageFileName(storageFileName)
                        .mimeType(resolveMimeType(file))
                        .sizeBytes(file.getSize())
                        .storagePath(target.toString())
                        .uploadedByDisplayName(resolveUserDisplayName(currentUser))
                        .build()
        );

        auditLogService.log(
                currentUser,
                "CONSENTEMENT_DOCUMENT_UPLOAD",
                MODULE,
                saved.getDocumentId(),
                "Upload consent document consentement=" + consentId
                        + " title=" + normalizedTitle,
                auditContextService.getClientIp()
        );

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public ConsentementDocumentDownload download(UUID documentId) {
        ConsentementDocument document = repository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Consent document not found"));

        Path filePath = Paths.get(document.getStoragePath());
        if (!Files.exists(filePath)) {
            throw new ResourceNotFoundException("Stored consent document not found");
        }

        auditLogService.log(
                auditContextService.getCurrentUserOrNull(),
                "CONSENTEMENT_DOCUMENT_DOWNLOAD",
                MODULE,
                document.getDocumentId(),
                "Download consent document file=" + document.getOriginalFileName(),
                auditContextService.getClientIp()
        );

        Resource resource = new FileSystemResource(filePath);
        return new ConsentementDocumentDownload(
                document.getOriginalFileName(),
                document.getMimeType(),
                document.getSizeBytes(),
                resource
        );
    }

    public void delete(UUID documentId) {
        ConsentementDocument document = repository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Consent document not found"));

        Path filePath = Paths.get(document.getStoragePath());
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to delete consent document", e);
        }

        auditLogService.log(
                auditContextService.getCurrentUserOrNull(),
                "CONSENTEMENT_DOCUMENT_DELETE",
                MODULE,
                document.getDocumentId(),
                "Delete consent document file=" + document.getOriginalFileName(),
                auditContextService.getClientIp()
        );

        repository.delete(document);
    }

    private ConsentementDocumentResponseDTO toResponse(ConsentementDocument entity) {
        return ConsentementDocumentResponseDTO.builder()
                .documentId(entity.getDocumentId())
                .consentId(entity.getConsentement() != null ? entity.getConsentement().getConsentId() : null)
                .patientId(entity.getPatient() != null ? entity.getPatient().getPatientId() : null)
                .interventionId(entity.getIntervention() != null ? entity.getIntervention().getInterventionId() : null)
                .title(entity.getTitle())
                .fileName(entity.getOriginalFileName())
                .mimeType(entity.getMimeType())
                .sizeBytes(entity.getSizeBytes())
                .uploadedAt(entity.getUploadedAt())
                .uploadedByUserId(entity.getUploadedBy() != null ? entity.getUploadedBy().getUserId() : null)
                .uploadedBy(entity.getUploadedByDisplayName())
                .build();
    }

    private Path rootPath() {
        return Paths.get(storageDir).toAbsolutePath().normalize();
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }

        if (file.getSize() > MAX_SIZE) {
            throw new IllegalArgumentException("File exceeds 8 MB");
        }

        String fileName = sanitizeFileName(file.getOriginalFilename());
        String extension = getExtension(fileName);
        String mimeType = resolveMimeType(file).toLowerCase();

        if (!ALLOWED_EXTENSIONS.contains(extension) && !ALLOWED_MIME_TYPES.contains(mimeType)) {
            throw new IllegalArgumentException("Unsupported file type");
        }
    }

    private String normalizeTitle(String title, String originalFileName) {
        String candidate = title == null ? "" : title.trim().replaceAll("\\s+", " ");
        if (!candidate.isBlank()) {
            return candidate;
        }

        int lastDot = originalFileName.lastIndexOf('.');
        return lastDot > 0 ? originalFileName.substring(0, lastDot) : originalFileName;
    }

    private String sanitizeFileName(String value) {
        String raw = value == null ? "document" : value.trim();
        String cleaned = raw.replaceAll("[\\\\/:*?\"<>|]", "_").replaceAll("\\s+", " ");
        return cleaned.isBlank() ? "document" : cleaned;
    }

    private String getExtension(String fileName) {
        int lastDot = fileName.lastIndexOf(".");
        if (lastDot < 0) {
            return "";
        }
        return fileName.substring(lastDot).toLowerCase();
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
