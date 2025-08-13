package br.com.clinicavet.clinica_api.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InternacaoUpdateDTO {
    
    private Long veterinarioResponsavelId;

    private LocalDateTime dataInicio;

    private LocalDateTime dataFim;
    
    @Size(max = 1000, message = "O motivo da internação deve ter no máximo 1000 caracteres.")
    private String motivoInternacao;
    
    @Size(max = 2000, message = "As observações devem ter no máximo 2000 caracteres.")
    private String observacoes;
    
    private Boolean ativa;
}