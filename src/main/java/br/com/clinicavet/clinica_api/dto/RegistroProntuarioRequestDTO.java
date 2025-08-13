package br.com.clinicavet.clinica_api.dto;

import jakarta.validation.constraints.NotNull;
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
public class RegistroProntuarioRequestDTO {
    
    @NotNull(message = "O ID do prontuário é obrigatório.")
    private Long prontuarioId;
    
    private Long agendamentoId;
    
    @NotNull(message = "O ID do veterinário responsável é obrigatório.")
    private Long veterinarioResponsavelId;
    
    @NotNull(message = "A data e hora são obrigatórias.")
    private LocalDateTime dataHora;
    
    private Long internacaoId;
    
    @Positive(message = "O peso deve ser um valor positivo.")
    private BigDecimal pesoNoDia;
    
    @Size(max = 2000, message = "As observações clínicas devem ter no máximo 2000 caracteres.")
    private String observacoesClinicas;
    
    @Size(max = 1000, message = "O diagnóstico deve ter no máximo 1000 caracteres.")
    private String diagnostico;
}