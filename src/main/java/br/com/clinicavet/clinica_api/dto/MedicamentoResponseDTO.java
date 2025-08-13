package br.com.clinicavet.clinica_api.dto;

import br.com.clinicavet.clinica_api.model.enums.EnumCategoriaMedicameno;
import br.com.clinicavet.clinica_api.model.enums.EnumViaMedicamento;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicamentoResponseDTO {

        // Campos herdados de Produto
        private Long id;

        private String nome;

        private String descricao;

        private Integer quantidadeEstoque;

        private EnumCategoriaMedicameno categoria;

        private EnumViaMedicamento viaAdministracao;

        private String dosagemPadrao;

        private String principioAtivo;

        private Boolean prescricaoObrigatoria;
}
