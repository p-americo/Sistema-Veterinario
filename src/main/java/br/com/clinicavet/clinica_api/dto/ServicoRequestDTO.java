package br.com.clinicavet.clinica_api.dto;

import br.com.clinicavet.clinica_api.model.enums.EnumServico;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServicoRequestDTO {

    @NotNull
    private EnumServico tipo;

    @NotNull
    private Long veterinarioId;

    @Positive
    private BigDecimal valor;
}
