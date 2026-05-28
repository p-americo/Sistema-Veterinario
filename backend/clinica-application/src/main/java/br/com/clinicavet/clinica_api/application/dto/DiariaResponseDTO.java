package br.com.clinicavet.clinica_api.application.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class DiariaResponseDTO {


    private Long id;
    private LocalDateTime dataHora;

    private BigDecimal pesoNoDia;

    private String observacoesClinicas;

    private String diagnostico;

    private List<AdministracaoMedicamentoResponseDTO> medicamentos;

}
