package com.tn.softsys.blocoperatoire.dto.compterendu;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompteRenduTemplateRequestDTO {

    @NotBlank
    @Size(max = 180)
    private String name;

    @NotBlank
    @Size(max = 180)
    private String libelle;

    @NotBlank
    @Size(max = 180)
    private String blocLabel;

    @NotBlank
    @Size(max = 20000)
    private String content;
}
