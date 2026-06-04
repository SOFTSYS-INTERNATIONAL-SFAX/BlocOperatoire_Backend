package com.tn.softsys.blocoperatoire.service;

import com.tn.softsys.blocoperatoire.domain.Autopsie;
import com.tn.softsys.blocoperatoire.domain.MorgueDocument;
import com.tn.softsys.blocoperatoire.domain.User;
import com.tn.softsys.blocoperatoire.dto.morgue.MorgueDocumentDownload;
import com.tn.softsys.blocoperatoire.dto.morgue.MorgueDocumentResponseDTO;
import com.tn.softsys.blocoperatoire.exception.ResourceNotFoundException;
import com.tn.softsys.blocoperatoire.mapper.MorgueDocumentMapper;
import com.tn.softsys.blocoperatoire.repository.AutopsieRepository;
import com.tn.softsys.blocoperatoire.repository.MorgueDocumentRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MorgueDocumentService {

    private static final String MODULE = "AUTOPSIE_DOCUMENT";
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

    private final MorgueDocumentRepository repository;
    private final AutopsieRepository autopsieRepository;
    private final MorgueDocumentMapper mapper;
    private final AuditLogService auditLogService;
    private final AuditContextService auditContextService;

    @Value("${app.storage.morgue-documents-dir:uploads/morgue-documents}")
    private String storageDir;

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(rootPath());
        } catch (IOException e) {
            throw new IllegalStateException("Unable to initialize morgue document storage", e);
        }
    }

    @Transactional(readOnly = true)
    public List<MorgueDocumentResponseDTO> listByAutopsie(UUID autopsieId) {
        if (!autopsieRepository.existsById(autopsieId)) {
            throw new ResourceNotFoundException("Autopsie not found");
        }

        return repository.findByAutopsieAutopsieIdOrderByUploadedAtDesc(autopsieId)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    public MorgueDocumentResponseDTO upload(UUID autopsieId, MultipartFile file) {
        validateFile(file);

        Autopsie autopsie = autopsieRepository.findById(autopsieId)
                .orElseThrow(() -> new ResourceNotFoundException("Autopsie not found"));

        String originalFileName = sanitizeFileName(file.getOriginalFilename());
        String extension = getExtension(originalFileName);
        String storageFileName = UUID.randomUUID() + extension;
        Path target = rootPath().resolve(storageFileName);

        try {
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to store document", e);
        }

        User currentUser = auditContextService.getCurrentUserOrNull();

        MorgueDocument entity = MorgueDocument.builder()
                .autopsie(autopsie)
                .uploadedBy(currentUser)
                .originalFileName(originalFileName)
                .storageFileName(storageFileName)
                .mimeType(resolveMimeType(file))
                .sizeBytes(file.getSize())
                .storagePath(target.toString())
                .build();

        MorgueDocument saved = repository.save(entity);

        auditLogService.log(
                currentUser,
                "AUTOPSIE_DOCUMENT_UPLOAD",
                MODULE,
                saved.getDocumentId(),
                "Upload document autopsie=" + autopsieId + " file=" + originalFileName,
                auditContextService.getClientIp()
        );

        return mapper.toDTO(saved);
    }

    @Transactional(readOnly = true)
    public MorgueDocumentDownload download(UUID documentId) {
        MorgueDocument entity = repository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found"));

        Path filePath = Paths.get(entity.getStoragePath());

        if (!Files.exists(filePath)) {
            throw new ResourceNotFoundException("Stored file not found");
        }

        Resource resource = new FileSystemResource(filePath);

        auditLogService.log(
                auditContextService.getCurrentUserOrNull(),
                "AUTOPSIE_DOCUMENT_DOWNLOAD",
                MODULE,
                entity.getDocumentId(),
                "Download document file=" + entity.getOriginalFileName(),
                auditContextService.getClientIp()
        );

        return new MorgueDocumentDownload(
                entity.getOriginalFileName(),
                entity.getMimeType(),
                entity.getSizeBytes(),
                resource
        );
    }

    public void delete(UUID documentId) {
        MorgueDocument entity = repository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found"));

        Path filePath = Paths.get(entity.getStoragePath());

        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to delete stored file", e);
        }

        auditLogService.log(
                auditContextService.getCurrentUserOrNull(),
                "AUTOPSIE_DOCUMENT_DELETE",
                MODULE,
                entity.getDocumentId(),
                "Delete document file=" + entity.getOriginalFileName(),
                auditContextService.getClientIp()
        );

        repository.delete(entity);
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
}
