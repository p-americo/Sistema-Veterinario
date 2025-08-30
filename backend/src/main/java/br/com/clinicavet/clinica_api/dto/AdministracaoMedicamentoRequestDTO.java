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
public class AdministracaoMedicamentoRequestDTO {
    
    @NotNull(message = "O ID do medicamento é obrigatório.")
    private Long medicamentoId;
    
    @NotNull(message = "O ID da entrada do prontuário é obrigatório.")
    private Long entradaProntuarioId;
    
    @NotNull(message = "O ID do funcionário executor é obrigatório.")
    private Long funcionarioExecutorId;
    
    @NotNull(message = "A quantidade administrada é obrigatória.")
    @Positive(message = "A quantidade administrada deve ser um valor positivo.")
    private BigDecimal quantidadeAdministrada;
    
    @NotNull(message = "A data e hora são obrigatórias.")
    private LocalDateTime dataHora;
    
    @Size(max = 100, message = "A dosagem deve ter no máximo 100 caracteres.")
    private String dosagem;
}