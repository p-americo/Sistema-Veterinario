package br.com.clinicavet.clinica_api.application.dto;

import br.com.clinicavet.clinica_api.domain.model.enums.EnumInternacaoStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class InternacaoResponseDTO {

    private Long id;

    private Long animalId;

    private String nomeAnimal;

    private LocalDateTime dataEntrada;

    private LocalDateTime dataSaida;

    private EnumInternacaoStatus status;
    private List<DiariaResponseDTO> diarias;
}