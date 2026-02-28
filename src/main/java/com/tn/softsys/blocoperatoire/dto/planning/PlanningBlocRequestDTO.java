package com.tn.softsys.blocoperatoire.dto.planning;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PlanningBlocRequestDTO {

    @NotNull
    private LocalDateTime date;
}