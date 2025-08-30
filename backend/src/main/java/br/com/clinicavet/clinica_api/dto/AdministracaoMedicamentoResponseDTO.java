package br.com.clinicavet.clinica_api.dto;

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
public class AdministracaoMedicamentoResponseDTO {
    
    private Long id;
    private Long medicamentoId;
    private String nomeMedicamento;
    private Long entradaProntuarioId;
    private Long funcionarioExecutorId;
    private String nomeFuncionarioExecutor;
    private BigDecimal quantidadeAdministrada;
    private LocalDateTime dataHora;
    private String dosagem;
}