package br.com.clinicavet.clinica_api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class InternacaoRequestDTO {

    @NotNull(message = "O ID do animal é obrigatório")
    private Long animalId;

    @NotNull(message = "A data de entrada é obrigatória")
    private LocalDateTime dataEntrada;
}
