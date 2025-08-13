package br.com.clinicavet.clinica_api.dto;

import br.com.clinicavet.clinica_api.model.enums.EnumCategoriaMedicameno;
import br.com.clinicavet.clinica_api.model.enums.EnumViaMedicamento;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO para receber dados de atualização de um medicamento.
 * Todos os campos são opcionais, permitindo atualizações parciais.
 * O ModelMapper será configurado para ignorar campos nulos deste DTO
 * ao mapear para a entidade existente.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicamentoUpdateDTO {

    // Campos herdados de Produto que podem ser atualizados
    private String nome;

    private String descricao;

    private BigDecimal preco;

    private Integer quantidadeEstoque;
    private EnumCategoriaMedicameno categoria;

    private EnumViaMedicamento viaAdministracao;

    private String dosagemPadrao;

    private String principioAtivo;

    private String fabricante;

    private String lote;

    private Boolean prescricaoObrigatoria;

}
