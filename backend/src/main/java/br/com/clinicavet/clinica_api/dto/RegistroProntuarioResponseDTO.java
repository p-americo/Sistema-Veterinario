package br.com.clinicavet.clinica_api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegistroProntuarioResponseDTO {
    
    private Long id;

    private Long prontuarioId;

    private Long agendamentoId;

    private Long veterinarioResponsavelId;

    private String nomeVeterinario;

    private LocalDateTime dataHora;

    private Long internacaoId;

    private BigDecimal pesoNoDia;

    private String observacoesClinicas;

    private String diagnostico;

    private List<AdministracaoMedicamentoResponseDTO> medicamentosAdministrados;
}