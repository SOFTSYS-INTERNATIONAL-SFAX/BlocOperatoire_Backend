package com.tn.softsys.blocoperatoire.mapper;

import com.tn.softsys.blocoperatoire.domain.FHIRResource;
import com.tn.softsys.blocoperatoire.dto.fhir.FHIRResourceResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class FHIRResourceMapper {

    public FHIRResourceResponseDTO toDTO(FHIRResource entity) {

        return FHIRResourceResponseDTO.builder()
                .resourceId(entity.getResourceId())
                .resourceType(entity.getResourceType())
                .payloadJson(entity.getPayloadJson())
                .dateCreation(entity.getDateCreation())
                .patientId(entity.getPatient() != null ?
                        entity.getPatient().getPatientId() : null)
                .interventionId(entity.getIntervention() != null ?
                        entity.getIntervention().getInterventionId() : null)
                .build();
    }
}