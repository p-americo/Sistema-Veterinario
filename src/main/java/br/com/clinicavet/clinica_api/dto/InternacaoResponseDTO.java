package br.com.clinicavet.clinica_api.dto;

import br.com.clinicavet.clinica_api.model.enums.EnumInternacaoStatus; // Importe o Enum
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class InternacaoResponseDTO {

    private Long id;

    private Long animalId;

    private String nomeAnimal;

    private LocalDateTime dataEntrada;

    private LocalDateTime dataSaida;

    private EnumInternacaoStatus status;
}