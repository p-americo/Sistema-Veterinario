package br.com.clinicavet.clinica_api.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegistroProntuarioUpdateDTO {
    
    private Long agendamentoId;

    private Long veterinarioResponsavelId;

    private LocalDateTime dataHora;

    private Long internacaoId;
    
    @Positive(message = "O peso deve ser um valor positivo.")
    private BigDecimal pesoNoDia;
    
    @Size(max = 2000, message = "As observações clínicas devem ter no máximo 2000 caracteres.")
    private String observacoesClinicas;
    
    @Size(max = 1000, message = "O diagnóstico deve ter no máximo 1000 caracteres.")
    private String diagnostico;
}