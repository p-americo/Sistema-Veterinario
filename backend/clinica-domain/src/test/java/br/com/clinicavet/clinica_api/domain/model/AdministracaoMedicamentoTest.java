package br.com.clinicavet.clinica_api.domain.model;

import br.com.clinicavet.clinica_api.domain.exception.BusinessRuleException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AdministracaoMedicamentoTest {

    @Test
    void registrarAdministracao_ComDadosValidosEProdutoSemEstoqueSuficiente_DeveLancarExcecao() {
        Funcionario executor = new Funcionario();
        Medicamento medicamento = new Medicamento();
        Produto produto = new Produto();
        produto.setNome("Dipirona");
        produto.inicializarEstoque(5); // Estoque = 5
        medicamento.setProduto(produto);

        AdministracaoMedicamento administracao = new AdministracaoMedicamento();

        // Tenta administrar 6 unidades (maior que o estoque de 5)
        assertThrows(BusinessRuleException.class, () -> {
            administracao.registrarAdministracao(medicamento, executor, new BigDecimal("6"), LocalDateTime.now(), "1ml");
        });

        // O estoque deve permanecer 5
        assertEquals(5, produto.getQuantidadeEstoque());
    }

    @Test
    void registrarAdministracao_ComDadosValidosEProdutoComEstoqueSuficiente_DeveRegistrarEDebitarEstoque() {
        Funcionario executor = new Funcionario();
        Medicamento medicamento = new Medicamento();
        Produto produto = new Produto();
        produto.setNome("Dipirona");
        produto.inicializarEstoque(10); // Estoque = 10
        medicamento.setProduto(produto);

        AdministracaoMedicamento administracao = new AdministracaoMedicamento();
        LocalDateTime agora = LocalDateTime.now();

        administracao.registrarAdministracao(medicamento, executor, new BigDecimal("4"), agora, "1ml");

        assertEquals(medicamento, administracao.getMedicamento());
        assertEquals(executor, administracao.getFuncionarioExecutor());
        assertEquals(new BigDecimal("4"), administracao.getQuantidadeAdministrada());
        assertEquals(agora, administracao.getDataHora());
        assertEquals("1ml", administracao.getDosagem());

        // O estoque deve ser debitado de 10 para 6
        assertEquals(6, produto.getQuantidadeEstoque());
    }

    @Test
    void registrarAdministracao_SemProdutoEstocavel_DeveRegistrarSemLancarExcecao() {
        Funcionario executor = new Funcionario();
        Medicamento medicamento = new Medicamento();
        medicamento.setProduto(null); // Medicamento sem controle de produto/estoque

        AdministracaoMedicamento administracao = new AdministracaoMedicamento();
        LocalDateTime agora = LocalDateTime.now();

        administracao.registrarAdministracao(medicamento, executor, new BigDecimal("2"), agora, "1ml");

        assertEquals(medicamento, administracao.getMedicamento());
        assertEquals(executor, administracao.getFuncionarioExecutor());
        assertEquals(new BigDecimal("2"), administracao.getQuantidadeAdministrada());
    }

    @Test
    void registrarAdministracao_ComParametrosObrigatoriosNulos_DeveLancarExcecao() {
        Funcionario executor = new Funcionario();
        Medicamento medicamento = new Medicamento();
        AdministracaoMedicamento administracao = new AdministracaoMedicamento();

        // Medicamento nulo
        assertThrows(IllegalArgumentException.class, () -> {
            administracao.registrarAdministracao(null, executor, new BigDecimal("1"), LocalDateTime.now(), "1ml");
        });

        // Executor nulo
        assertThrows(IllegalArgumentException.class, () -> {
            administracao.registrarAdministracao(medicamento, null, new BigDecimal("1"), LocalDateTime.now(), "1ml");
        });
    }

    @Test
    void registrarAdministracao_ComQuantidadeInvalida_DeveLancarExcecao() {
        Funcionario executor = new Funcionario();
        Medicamento medicamento = new Medicamento();
        AdministracaoMedicamento administracao = new AdministracaoMedicamento();

        // Quantidade nula
        assertThrows(BusinessRuleException.class, () -> {
            administracao.registrarAdministracao(medicamento, executor, null, LocalDateTime.now(), "1ml");
        });

        // Quantidade zero
        assertThrows(BusinessRuleException.class, () -> {
            administracao.registrarAdministracao(medicamento, executor, BigDecimal.ZERO, LocalDateTime.now(), "1ml");
        });

        // Quantidade negativa
        assertThrows(BusinessRuleException.class, () -> {
            administracao.registrarAdministracao(medicamento, executor, new BigDecimal("-1.5"), LocalDateTime.now(), "1ml");
        });
    }

    @Test
    void associarAoProntuario_ComDadosValidos_DeveAssociarBidirecionalmente() {
        RegistroProntuario prontuario = new RegistroProntuario();
        AdministracaoMedicamento administracao = new AdministracaoMedicamento();

        administracao.associarAoProntuario(prontuario);

        assertEquals(prontuario, administracao.getEntradaProntuario());
        assertTrue(prontuario.getMedicamentosAdministrados().contains(administracao));
    }

    @Test
    void associarAoProntuario_ComParametroNulo_DeveLancarExcecao() {
        AdministracaoMedicamento administracao = new AdministracaoMedicamento();

        assertThrows(IllegalArgumentException.class, () -> {
            administracao.associarAoProntuario(null);
        });
    }

    @Test
    void associarAoProntuario_ChamadoDuasVezes_NaoDeveDuplicarNaLista() {
        RegistroProntuario prontuario = new RegistroProntuario();
        AdministracaoMedicamento administracao = new AdministracaoMedicamento();

        administracao.associarAoProntuario(prontuario);
        administracao.associarAoProntuario(prontuario);

        assertEquals(1, prontuario.getMedicamentosAdministrados().size());
    }

    @Test
    void associarDiaria_ComDadosValidos_DeveAssociarBidirecionalmente() {
        DiariaInternacao diaria = new DiariaInternacao();
        AdministracaoMedicamento administracao = new AdministracaoMedicamento();

        administracao.associarDiaria(diaria);

        assertEquals(diaria, administracao.getDiaria());
        assertTrue(diaria.getMedicamentos().contains(administracao));
    }

    @Test
    void associarDiaria_ComParametroNulo_DeveLancarExcecao() {
        AdministracaoMedicamento administracao = new AdministracaoMedicamento();

        assertThrows(IllegalArgumentException.class, () -> {
            administracao.associarDiaria(null);
        });
    }

    @Test
    void associarDiaria_ChamadoDuasVezes_NaoDeveDuplicarNaLista() {
        DiariaInternacao diaria = new DiariaInternacao();
        AdministracaoMedicamento administracao = new AdministracaoMedicamento();

        administracao.associarDiaria(diaria);
        administracao.associarDiaria(diaria);

        assertEquals(1, diaria.getMedicamentos().size());
    }
}
