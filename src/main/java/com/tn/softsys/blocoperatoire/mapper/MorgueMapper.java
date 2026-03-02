package com.tn.softsys.blocoperatoire.mapper;

import com.tn.softsys.blocoperatoire.domain.Morgue;
import com.tn.softsys.blocoperatoire.dto.morgue.MorgueResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class MorgueMapper {

    public MorgueResponseDTO toDTO(Morgue entity) {

        return MorgueResponseDTO.builder()
                .morgueId(entity.getMorgueId())
                .nom(entity.getNom())
                .localisation(entity.getLocalisation())
                .build();
    }
}