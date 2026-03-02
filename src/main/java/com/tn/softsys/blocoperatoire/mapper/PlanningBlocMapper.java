package com.tn.softsys.blocoperatoire.mapper;

import com.tn.softsys.blocoperatoire.domain.PlanningBloc;
import com.tn.softsys.blocoperatoire.dto.planning.PlanningBlocResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class PlanningBlocMapper {

    public PlanningBlocResponseDTO toDTO(PlanningBloc entity) {

        return PlanningBlocResponseDTO.builder()
                .planningId(entity.getPlanningId())
                .date(entity.getDate())
                .nombreInterventions(
                        entity.getInterventions() != null
                                ? entity.getInterventions().size()
                                : 0
                )
                .build();
    }
}