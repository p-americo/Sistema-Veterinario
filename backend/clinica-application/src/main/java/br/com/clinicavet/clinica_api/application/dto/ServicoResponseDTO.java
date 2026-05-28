package br.com.clinicavet.clinica_api.application.dto;

import br.com.clinicavet.clinica_api.domain.model.enums.EnumServico;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter

public class ServicoResponseDTO {

    private Long id;

    private EnumServico tipo;

    private BigDecimal valor;

    private String nomeVeterinario;

    private Long veterinarioId;
}
