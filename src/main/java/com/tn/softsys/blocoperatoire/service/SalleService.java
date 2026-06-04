package com.tn.softsys.blocoperatoire.service;

import com.tn.softsys.blocoperatoire.domain.Salle;
import com.tn.softsys.blocoperatoire.domain.StatutSalle;
import com.tn.softsys.blocoperatoire.dto.salle.SalleRequestDTO;
import com.tn.softsys.blocoperatoire.dto.salle.SalleResponseDTO;
import com.tn.softsys.blocoperatoire.exception.ResourceNotFoundException;
import com.tn.softsys.blocoperatoire.mapper.SalleMapper;
import com.tn.softsys.blocoperatoire.repository.SalleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional
public class SalleService {

    private static final String MODULE = "SALLE";
    private static final Pattern BLOC_SUFFIX_PATTERN = Pattern.compile("(?i).*-\\s*((bloc|block)\\s*[^-]+)$");
    private static final Pattern BLOC_PREFIX_PATTERN = Pattern.compile("(?i)^((bloc|block)\\s*[^-]+)");

    private final SalleRepository salleRepository;
    private final SalleMapper mapper;
    private final AlertService alertService;
    private final AuditLogService auditLogService;
    private final AuditContextService auditContextService;

    public SalleResponseDTO create(SalleRequestDTO dto) {
        Salle salle = mapper.toEntity(dto);
        normalizeSalleFields(salle);
        applyManagedStatus(salle, dto.getStatut(), dto.getActive());
        Salle saved = salleRepository.save(salle);

        audit(
                "SALLE_CREATE",
                saved.getSalleId(),
                "Creation salle nom=" + saved.getNom()
                        + " bloc=" + saved.getIdBloc()
                        + " etage=" + saved.getEtageBatiment()
                        + " active=" + saved.getActive()
                        + " statut=" + saved.getStatut()
        );

        return mapper.toDTO(saved);
    }

    public SalleResponseDTO update(UUID id, SalleRequestDTO dto) {
        Salle existing = salleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salle not found"));

        mapper.updateEntity(existing, dto);
        normalizeSalleFields(existing);
        applyManagedStatus(existing, dto.getStatut(), dto.getActive());
        Salle saved = salleRepository.save(existing);

        audit(
                "SALLE_UPDATE",
                saved.getSalleId(),
                "Mise a jour salle nom=" + saved.getNom()
                        + " bloc=" + saved.getIdBloc()
                        + " etage=" + saved.getEtageBatiment()
                        + " active=" + saved.getActive()
                        + " statut=" + saved.getStatut()
        );

        return mapper.toDTO(saved);
    }

    @Transactional(readOnly = true)
    public SalleResponseDTO getById(UUID id) {
        Salle salle = salleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salle not found"));

        return mapper.toDTO(salle);
    }

    @Transactional(readOnly = true)
    public Page<SalleResponseDTO> search(
            String nom,
            String etageBatiment,
            Boolean active,
            Pageable pageable
    ) {
        Page<Salle> page;

        if (nom != null && etageBatiment != null) {
            page = salleRepository
                    .findByNomContainingIgnoreCaseAndEtageBatimentContainingIgnoreCase(
                            nom, etageBatiment, pageable
                    );
        } else if (nom != null) {
            page = salleRepository.findByNomContainingIgnoreCase(nom, pageable);
        } else if (etageBatiment != null) {
            page = salleRepository.findByEtageBatimentContainingIgnoreCase(etageBatiment, pageable);
        } else if (active != null) {
            page = salleRepository.findByActive(active, pageable);
        } else {
            page = salleRepository.findAll(pageable);
        }

        return page.map(mapper::toDTO);
    }

    public SalleResponseDTO updateActive(UUID id, Boolean active) {
        Salle salle = salleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salle not found"));

        Boolean oldActive = salle.getActive();
        StatutSalle oldStatut = salle.getStatut();
        StatutSalle nextStatut = Boolean.TRUE.equals(active) ? StatutSalle.DISPONIBLE : StatutSalle.FERMEE;
        salle.setStatut(nextStatut);
        salle.setActive(nextStatut.isOperational());

        Salle saved = salleRepository.save(salle);

        alertService.createSalleStatusAlert(saved);

        audit(
                "SALLE_STATUS_UPDATE",
                saved.getSalleId(),
                "Changement statut salle nom=" + saved.getNom()
                        + " ancienActif=" + oldActive
                        + " nouveauActif=" + saved.getActive()
                        + " ancienStatut=" + oldStatut
                        + " nouveauStatut=" + saved.getStatut()
        );

        return mapper.toDTO(saved);
    }

    public void delete(UUID id) {
        Salle salle = salleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salle not found"));

        audit(
                "SALLE_DELETE",
                salle.getSalleId(),
                "Suppression salle nom=" + salle.getNom()
                        + " bloc=" + salle.getIdBloc()
                        + " etage=" + salle.getEtageBatiment()
        );

        salleRepository.deleteById(id);
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

    private void normalizeSalleFields(Salle salle) {
        salle.setNom(normalizeText(salle.getNom()));
        salle.setNomEn(normalizeNullableText(salle.getNomEn()));
        salle.setNomAr(normalizeNullableText(salle.getNomAr()));
        salle.setEtageBatiment(normalizeText(salle.getEtageBatiment()));
        salle.setEtageBatimentEn(normalizeNullableText(salle.getEtageBatimentEn()));
        salle.setEtageBatimentAr(normalizeNullableText(salle.getEtageBatimentAr()));
        salle.setEquipements(normalizeNullableText(salle.getEquipements()));
        salle.setIdBloc(normalizeBlocValue(salle.getIdBloc(), salle.getNom()));
        salle.setIdBlocEn(normalizeNullableText(salle.getIdBlocEn()));
        salle.setIdBlocAr(normalizeNullableText(salle.getIdBlocAr()));
    }

    private void applyManagedStatus(Salle salle, String rawStatut, Boolean activeFallback) {
        StatutSalle statut = resolveManagedStatus(rawStatut, activeFallback);
        salle.setStatut(statut);
        salle.setActive(statut.isOperational());
    }

    private StatutSalle resolveManagedStatus(String rawStatut, Boolean activeFallback) {
        String normalized = normalizeText(rawStatut).toUpperCase(Locale.ROOT);

        if (!normalized.isEmpty()) {
            try {
                return StatutSalle.valueOf(normalized);
            } catch (IllegalArgumentException ignored) {
                // Fallback below keeps compatibility with existing clients.
            }
        }

        if (activeFallback != null) {
            return Boolean.TRUE.equals(activeFallback) ? StatutSalle.DISPONIBLE : StatutSalle.FERMEE;
        }

        return StatutSalle.DISPONIBLE;
    }

    private String normalizeBlocValue(String rawBloc, String roomName) {
        String bloc = normalizeText(rawBloc);
        String nom = normalizeText(roomName);

        if (bloc.isEmpty()) {
            return inferBlocFromRoomName(nom);
        }

        if (!nom.isEmpty() && bloc.equalsIgnoreCase(nom)) {
            return inferBlocFromRoomName(nom);
        }

        return bloc;
    }

    private String inferBlocFromRoomName(String roomName) {
        if (roomName.isBlank()) {
            return "BLOC-INDISPONIBLE";
        }

        Matcher suffixMatcher = BLOC_SUFFIX_PATTERN.matcher(roomName);
        if (suffixMatcher.matches()) {
            return normalizeText(suffixMatcher.group(1));
        }

        Matcher prefixMatcher = BLOC_PREFIX_PATTERN.matcher(roomName);
        if (prefixMatcher.find()) {
            return normalizeText(prefixMatcher.group(1));
        }

        return roomName;
    }

    private String normalizeText(String value) {
        return value == null ? "" : value.trim();
    }

    private String normalizeNullableText(String value) {
        String normalized = normalizeText(value);
        return normalized.isEmpty() ? null : normalized;
    }
}
