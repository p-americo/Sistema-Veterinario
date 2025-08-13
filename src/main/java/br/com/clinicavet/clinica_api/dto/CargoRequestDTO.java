package br.com.clinicavet.clinica_api.dto;

import br.com.clinicavet.clinica_api.model.enums.EnumCargo;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CargoRequestDTO {

    @NotNull(message = "O tipo do cargo é obrigatório.")
    private EnumCargo cargo;

    @NotNull(message = "O salário é obrigatório.")
    @Positive(message = "O salário deve ser um valor positivo.")
    private BigDecimal salario;
}