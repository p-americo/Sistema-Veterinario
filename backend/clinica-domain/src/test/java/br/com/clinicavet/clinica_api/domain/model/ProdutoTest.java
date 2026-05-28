package br.com.clinicavet.clinica_api.domain.model;

import br.com.clinicavet.clinica_api.domain.exception.BusinessRuleException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProdutoTest {

    @Test
    void debitarEstoque_QuantidadeValida_DeveDecrementar() {
        Produto produto = new Produto();
        produto.setNome("Medicamento A");
        produto.inicializarEstoque(10);

        produto.debitarEstoque(4);

        assertEquals(6, produto.getQuantidadeEstoque());
    }

    @Test
    void debitarEstoque_QuantidadeMaiorQueEstoque_DeveLancarExcecao() {
        Produto produto = new Produto();
        produto.setNome("Medicamento A");
        produto.inicializarEstoque(5);

        assertThrows(BusinessRuleException.class, () -> {
            produto.debitarEstoque(6);
        });
        
        // Stock should remain unchanged
        assertEquals(5, produto.getQuantidadeEstoque());
    }

    @Test
    void debitarEstoque_QuantidadeNegativaOuZero_DeveLancarExcecao() {
        Produto produto = new Produto();
        produto.setNome("Medicamento A");
        produto.inicializarEstoque(10);

        assertThrows(BusinessRuleException.class, () -> {
            produto.debitarEstoque(0);
        });

        assertThrows(BusinessRuleException.class, () -> {
            produto.debitarEstoque(-3);
        });
    }

    @Test
    void creditarEstoque_QuantidadeValida_DeveIncrementar() {
        Produto produto = new Produto();
        produto.setNome("Medicamento A");
        produto.inicializarEstoque(10);

        produto.creditarEstoque(5);

        assertEquals(15, produto.getQuantidadeEstoque());
    }

    @Test
    void creditarEstoque_QuantidadeNegativaOuZero_DeveLancarExcecao() {
        Produto produto = new Produto();
        produto.setNome("Medicamento A");
        produto.inicializarEstoque(10);

        assertThrows(BusinessRuleException.class, () -> {
            produto.creditarEstoque(0);
        });

        assertThrows(BusinessRuleException.class, () -> {
            produto.creditarEstoque(-5);
        });
    }
}
