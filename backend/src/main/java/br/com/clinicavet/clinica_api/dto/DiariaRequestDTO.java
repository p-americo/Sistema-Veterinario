package br.com.clinicavet.clinica_api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class DiariaRequestDTO {
    @NotNull
    private Long internacaoId;

    @NotNull
    private LocalDateTime dataHora;

    private BigDecimal pesoNoDia;

    private String observacoesClinicas;

    private String diagnostico;

}