package com.tn.softsys.blocoperatoire.dto.compterendu;

import com.tn.softsys.blocoperatoire.domain.CompteRenduDocumentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CompteRenduDocumentRequestDTO {

    @NotNull
    private UUID patientId;

    private UUID interventionId;

    private UUID templateId;

    @NotBlank
    @Size(max = 220)
    private String title;

    @NotBlank
    @Size(max = 60000)
    private String content;

    @NotNull
    private CompteRenduDocumentStatus status;
}
