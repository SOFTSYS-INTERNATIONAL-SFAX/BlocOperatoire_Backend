package com.tn.softsys.blocoperatoire.service;

import com.tn.softsys.blocoperatoire.domain.Patient;
import com.tn.softsys.blocoperatoire.domain.PatientDocument;
import com.tn.softsys.blocoperatoire.domain.PatientDocumentCategory;
import com.tn.softsys.blocoperatoire.domain.User;
import com.tn.softsys.blocoperatoire.dto.patient.PatientDocumentDownload;
import com.tn.softsys.blocoperatoire.dto.patient.PatientDocumentResponseDTO;
import com.tn.softsys.blocoperatoire.exception.ResourceNotFoundException;
import com.tn.softsys.blocoperatoire.repository.PatientDocumentRepository;
import com.tn.softsys.blocoperatoire.repository.PatientRepository;
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
public class PatientDocumentService {

    private static final String MODULE = "PATIENT_DOCUMENT";
    private static final long MAX_SIZE = 8L * 1024L * 1024L;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".pdf", ".doc", ".docx", ".jpg", ".jpeg", ".png", ".txt");
    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "image/jpeg",
            "image/png",
            "text/plain",
            "application/octet-stream"
    );

    private final PatientDocumentRepository repository;
    private final PatientRepository patientRepository;
    private final AuditLogService auditLogService;
    private final AuditContextService auditContextService;

    @Value("${app.storage.patient-documents-dir:uploads/patient-documents}")
    private String storageDir;

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(rootPath());
        } catch (IOException e) {
            throw new IllegalStateException("Unable to initialize patient document storage", e);
        }
    }

    @Transactional(readOnly = true)
    public List<PatientDocumentResponseDTO> listByPatient(UUID patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient not found");
        }

        return repository.findByPatientPatientIdOrderByUploadedAtDesc(patientId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public PatientDocumentResponseDTO upload(
            UUID patientId,
            String title,
            PatientDocumentCategory category,
            MultipartFile file) {
        validateFile(file);

        if (category == null) {
            throw new IllegalArgumentException("Document category is required");
        }

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        String originalFileName = sanitizeFileName(file.getOriginalFilename());
        String extension = getExtension(originalFileName);
        String storageFileName = UUID.randomUUID() + extension;
        Path target = rootPath().resolve(storageFileName);

        try {
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to store patient document", e);
        }

        User currentUser = auditContextService.getCurrentUserOrNull();
        String normalizedTitle = normalizeTitle(title, originalFileName);

        PatientDocument saved = repository.save(
                PatientDocument.builder()
                        .patient(patient)
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
                "PATIENT_DOCUMENT_UPLOAD",
                MODULE,
                saved.getDocumentId(),
                "Upload document patient=" + patientId
                        + " category=" + category
                        + " title=" + normalizedTitle,
                auditContextService.getClientIp()
        );

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public PatientDocumentDownload download(UUID documentId) {
        PatientDocument document = repository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient document not found"));

        Path filePath = Paths.get(document.getStoragePath());
        if (!Files.exists(filePath)) {
            throw new ResourceNotFoundException("Stored patient document not found");
        }

        auditLogService.log(
                auditContextService.getCurrentUserOrNull(),
                "PATIENT_DOCUMENT_DOWNLOAD",
                MODULE,
                document.getDocumentId(),
                "Download patient document file=" + document.getOriginalFileName(),
                auditContextService.getClientIp()
        );

        Resource resource = new FileSystemResource(filePath);
        return new PatientDocumentDownload(
                document.getOriginalFileName(),
                document.getMimeType(),
                document.getSizeBytes(),
                resource
        );
    }

    public void delete(UUID documentId) {
        PatientDocument document = repository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient document not found"));

        Path filePath = Paths.get(document.getStoragePath());
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to delete patient document", e);
        }

        auditLogService.log(
                auditContextService.getCurrentUserOrNull(),
                "PATIENT_DOCUMENT_DELETE",
                MODULE,
                document.getDocumentId(),
                "Delete patient document file=" + document.getOriginalFileName(),
                auditContextService.getClientIp()
        );

        repository.delete(document);
    }

    private PatientDocumentResponseDTO toResponse(PatientDocument entity) {
        return PatientDocumentResponseDTO.builder()
                .documentId(entity.getDocumentId())
                .patientId(entity.getPatient().getPatientId())
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
