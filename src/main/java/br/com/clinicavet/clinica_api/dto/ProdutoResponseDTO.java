package br.com.clinicavet.clinica_api.dto;

import br.com.clinicavet.clinica_api.model.enums.EnumTipoProduto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProdutoResponseDTO {

    private Long id;

    private String nome;

    private String descricao;

    private EnumTipoProduto tipoProduto;

    private String unidadeMedida;
}
