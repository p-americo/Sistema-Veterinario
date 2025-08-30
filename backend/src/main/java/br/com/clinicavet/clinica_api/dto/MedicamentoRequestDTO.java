package br.com.clinicavet.clinica_api.dto;

import br.com.clinicavet.clinica_api.model.enums.EnumCategoriaMedicameno;
import br.com.clinicavet.clinica_api.model.enums.EnumViaMedicamento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicamentoRequestDTO {

    // Campos herdados de Produto
    @NotBlank(message = "O nome do medicamento é obrigatório")
    private String nome;

    private String descricao;

    private Integer quantidadeEstoque;

    // Campos específicos de Medicamento
    @NotNull(message = "A categoria do medicamento é obrigatória")
    private EnumCategoriaMedicameno categoria;

    private EnumViaMedicamento viaAdministracao;

    private String dosagemPadrao;

    private String principioAtivo;

    private String fabricante;

    private Boolean prescricaoObrigatoria;
}
