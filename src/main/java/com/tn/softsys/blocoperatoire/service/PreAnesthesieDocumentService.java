package com.tn.softsys.blocoperatoire.service;

import com.tn.softsys.blocoperatoire.domain.ConsultationPreAnesthesique;
import com.tn.softsys.blocoperatoire.domain.PreAnesthesieDocument;
import com.tn.softsys.blocoperatoire.domain.PreAnesthesieDocumentCategory;
import com.tn.softsys.blocoperatoire.domain.User;
import com.tn.softsys.blocoperatoire.dto.preanesthesie.PreAnesthesieDocumentDownload;
import com.tn.softsys.blocoperatoire.dto.preanesthesie.PreAnesthesieDocumentResponseDTO;
import com.tn.softsys.blocoperatoire.exception.ResourceNotFoundException;
import com.tn.softsys.blocoperatoire.repository.ConsultationPreAnesthesiqueRepository;
import com.tn.softsys.blocoperatoire.repository.PreAnesthesieDocumentRepository;
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
public class PreAnesthesieDocumentService {

    private static final String MODULE = "PRE_ANESTHESIE_DOCUMENT";
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

    private final PreAnesthesieDocumentRepository repository;
    private final ConsultationPreAnesthesiqueRepository consultationRepository;
    private final AuditLogService auditLogService;
    private final AuditContextService auditContextService;

    @Value("${app.storage.pre-anesthesie-documents-dir:uploads/pre-anesthesie-documents}")
    private String storageDir;

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(rootPath());
        } catch (IOException e) {
            throw new IllegalStateException("Unable to initialize pre-anesthesia document storage", e);
        }
    }

    @Transactional(readOnly = true)
    public List<PreAnesthesieDocumentResponseDTO> listByConsultation(UUID consultationId) {
        if (!consultationRepository.existsById(consultationId)) {
            throw new ResourceNotFoundException("Pre-anesthesia consultation not found");
        }

        return repository.findByConsultationConsultationIdOrderByUploadedAtDesc(consultationId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public PreAnesthesieDocumentResponseDTO upload(
            UUID consultationId,
            String title,
            PreAnesthesieDocumentCategory category,
            MultipartFile file) {
        validateFile(file);

        if (category == null) {
            throw new IllegalArgumentException("Document category is required");
        }

        ConsultationPreAnesthesique consultation = consultationRepository.findById(consultationId)
                .orElseThrow(() -> new ResourceNotFoundException("Pre-anesthesia consultation not found"));

        String originalFileName = sanitizeFileName(file.getOriginalFilename());
        String extension = getExtension(originalFileName);
        String storageFileName = UUID.randomUUID() + extension;
        Path target = rootPath().resolve(storageFileName);

        try {
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to store pre-anesthesia document", e);
        }

        User currentUser = auditContextService.getCurrentUserOrNull();
        String normalizedTitle = normalizeTitle(title, originalFileName);

        PreAnesthesieDocument saved = repository.save(
                PreAnesthesieDocument.builder()
                        .consultation(consultation)
                        .patient(consultation.getPatient())
                        .uploadedBy(currentUser)
                        .title(normalizedTitle)
                        .category(category)
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
                "PRE_ANESTHESIE_DOCUMENT_UPLOAD",
                MODULE,
                saved.getDocumentId(),
                "Upload pre-anesthesia document consultation=" + consultationId
                        + " category=" + category
                        + " title=" + normalizedTitle,
                auditContextService.getClientIp()
        );

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public PreAnesthesieDocumentDownload download(UUID documentId) {
        PreAnesthesieDocument document = repository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Pre-anesthesia document not found"));

        Path filePath = Paths.get(document.getStoragePath());
        if (!Files.exists(filePath)) {
            throw new ResourceNotFoundException("Stored pre-anesthesia document not found");
        }

        auditLogService.log(
                auditContextService.getCurrentUserOrNull(),
                "PRE_ANESTHESIE_DOCUMENT_DOWNLOAD",
                MODULE,
                document.getDocumentId(),
                "Download pre-anesthesia document file=" + document.getOriginalFileName(),
                auditContextService.getClientIp()
        );

        Resource resource = new FileSystemResource(filePath);
        return new PreAnesthesieDocumentDownload(
                document.getOriginalFileName(),
                document.getMimeType(),
                document.getSizeBytes(),
                resource
        );
    }

    public void delete(UUID documentId) {
        PreAnesthesieDocument document = repository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Pre-anesthesia document not found"));

        Path filePath = Paths.get(document.getStoragePath());
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to delete pre-anesthesia document", e);
        }

        auditLogService.log(
                auditContextService.getCurrentUserOrNull(),
                "PRE_ANESTHESIE_DOCUMENT_DELETE",
                MODULE,
                document.getDocumentId(),
                "Delete pre-anesthesia document file=" + document.getOriginalFileName(),
                auditContextService.getClientIp()
        );

        repository.delete(document);
    }

    private PreAnesthesieDocumentResponseDTO toResponse(PreAnesthesieDocument entity) {
        UUID consultationId = entity.getConsultation() != null
                ? entity.getConsultation().getConsultationId()
                : null;
        UUID patientId = entity.getPatient() != null
                ? entity.getPatient().getPatientId()
                : entity.getConsultation() != null && entity.getConsultation().getPatient() != null
                ? entity.getConsultation().getPatient().getPatientId()
                : null;

        return PreAnesthesieDocumentResponseDTO.builder()
                .documentId(entity.getDocumentId())
                .consultationId(consultationId)
                .patientId(patientId)
                .title(entity.getTitle())
                .category(entity.getCategory())
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
