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
public class AdministracaoMedicamentoUpdateDTO {
    
    private Long medicamentoId;

    private Long funcionarioExecutorId;
    
    @Positive(message = "A quantidade administrada deve ser um valor positivo.")
    private BigDecimal quantidadeAdministrada;
    
    private LocalDateTime dataHora;
    
    @Size(max = 100, message = "A dosagem deve ter no máximo 100 caracteres.")
    private String dosagem;
}