package com.tn.softsys.blocoperatoire.dto.morgue;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class MorgueResponseDTO {

    private UUID morgueId;
    private String nom;
    private String localisation;
}