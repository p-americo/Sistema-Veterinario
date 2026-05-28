package br.com.clinicavet.clinica_api.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.AccessLevel;

@Entity
@Table(name = "produto")
@Getter
@Setter
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100, unique = true)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false)
    @Setter(AccessLevel.PROTECTED)
    private Integer quantidadeEstoque;

    // Métodos de Regra de Negócio (DDD)

    public void inicializarEstoque(Integer estoque) {
        if (estoque == null || estoque < 0) {
            throw new br.com.clinicavet.clinica_api.domain.exception.BusinessRuleException("Estoque inicial não pode ser nulo ou negativo.");
        }
        this.quantidadeEstoque = estoque;
    }

    public void debitarEstoque(int quantidade) {
        if (quantidade <= 0) {
            throw new br.com.clinicavet.clinica_api.domain.exception.BusinessRuleException("A quantidade a debitar deve ser maior que zero.");
        }
        if (this.quantidadeEstoque < quantidade) {
            throw new br.com.clinicavet.clinica_api.domain.exception.BusinessRuleException(
                    "Estoque insuficiente para o produto: " + this.nome + ". Disponível: " + this.quantidadeEstoque);
        }
        this.quantidadeEstoque -= quantidade;
    }

    public void creditarEstoque(int quantidade) {
        if (quantidade <= 0) {
            throw new br.com.clinicavet.clinica_api.domain.exception.BusinessRuleException("A quantidade a creditar deve ser maior que zero.");
        }
        this.quantidadeEstoque += quantidade;
    }
}
