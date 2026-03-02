package com.tn.softsys.blocoperatoire.dto.tempsoperatoire;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;
@Getter
@Setter
public class TempsOperatoireResponseDTO {

    private UUID tempsId;
    private UUID interventionId;

    private LocalDateTime entreeBloc;
    private LocalDateTime debutAnesthesie;
    private LocalDateTime incision;
    private LocalDateTime finActe;
    private LocalDateTime sortieSalle;

    private Long dureeActeMinutes;
}