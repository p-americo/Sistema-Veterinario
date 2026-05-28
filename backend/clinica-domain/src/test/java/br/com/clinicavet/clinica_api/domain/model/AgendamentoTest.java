package br.com.clinicavet.clinica_api.domain.model;

import br.com.clinicavet.clinica_api.domain.exception.BusinessRuleException;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumAgendamento;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AgendamentoTest {

    @Test
    void associarClienteEAnimal_ComDadosValidos_DeveAssociar() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);

        Animal animal = new Animal();
        animal.setId(10L);
        animal.setCliente(cliente);

        Agendamento agendamento = new Agendamento();
        agendamento.associarClienteEAnimal(cliente, animal);

        assertEquals(cliente, agendamento.getCliente());
        assertEquals(animal, agendamento.getAnimal());
    }

    @Test
    void associarClienteEAnimal_ComParametrosNulos_DeveLancarExcecao() {
        Agendamento agendamento = new Agendamento();

        assertThrows(IllegalArgumentException.class, () -> {
            agendamento.associarClienteEAnimal(null, new Animal());
        });

        assertThrows(IllegalArgumentException.class, () -> {
            agendamento.associarClienteEAnimal(new Cliente(), null);
        });
    }

    @Test
    void associarClienteEAnimal_AnimalNaoPertenceAoCliente_DeveLancarExcecao() {
        Cliente clienteA = new Cliente();
        clienteA.setId(1L);

        Cliente clienteB = new Cliente();
        clienteB.setId(2L);

        Animal animal = new Animal();
        animal.setId(10L);
        animal.setCliente(clienteB); // Animal pertence ao cliente B

        Agendamento agendamento = new Agendamento();

        // Tenta associar ao cliente A
        assertThrows(BusinessRuleException.class, () -> {
            agendamento.associarClienteEAnimal(clienteA, animal);
        });
    }

    @Test
    void associarClienteEAnimal_AnimalSemCliente_DeveLancarExcecao() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);

        Animal animal = new Animal();
        animal.setId(10L);
        animal.setCliente(null); // Animal sem cliente

        Agendamento agendamento = new Agendamento();

        assertThrows(BusinessRuleException.class, () -> {
            agendamento.associarClienteEAnimal(cliente, animal);
        });
    }

    @Test
    void cancelar_ComAgendamentoRealizado_DeveLancarExcecao() {
        Agendamento agendamento = new Agendamento();
        agendamento.setStatus(EnumAgendamento.REALIZADO);

        assertThrows(BusinessRuleException.class, agendamento::cancelar);
    }

    @Test
    void cancelar_ComAgendamentoPendenteOuConfirmado_DeveAlterarParaCancelado() {
        Agendamento agendamentoPendente = new Agendamento();
        agendamentoPendente.setStatus(EnumAgendamento.AGENDADO);
        agendamentoPendente.cancelar();
        assertEquals(EnumAgendamento.CANCELADO, agendamentoPendente.getStatus());

        Agendamento agendamentoConfirmado = new Agendamento();
        agendamentoConfirmado.setStatus(EnumAgendamento.CONFIRMADO);
        agendamentoConfirmado.cancelar();
        assertEquals(EnumAgendamento.CANCELADO, agendamentoConfirmado.getStatus());
    }

    @Test
    void confirmar_ComAgendamentoDiferenteDeAgendado_DeveLancarExcecao() {
        Agendamento agendamentoRealizado = new Agendamento();
        agendamentoRealizado.setStatus(EnumAgendamento.REALIZADO);
        assertThrows(BusinessRuleException.class, agendamentoRealizado::confirmar);

        Agendamento agendamentoCancelado = new Agendamento();
        agendamentoCancelado.setStatus(EnumAgendamento.CANCELADO);
        assertThrows(BusinessRuleException.class, agendamentoCancelado::confirmar);
    }

    @Test
    void confirmar_ComAgendamentoAgendado_DeveConfirmar() {
        Agendamento agendamento = new Agendamento();
        agendamento.setStatus(EnumAgendamento.AGENDADO);
        agendamento.confirmar();
        assertEquals(EnumAgendamento.CONFIRMADO, agendamento.getStatus());
    }

    @Test
    void realizar_ComAgendamentoCancelado_DeveLancarExcecao() {
        Agendamento agendamento = new Agendamento();
        agendamento.setStatus(EnumAgendamento.CANCELADO);
        assertThrows(BusinessRuleException.class, agendamento::realizar);
    }

    @Test
    void realizar_ComAgendamentoValido_DeveAlterarParaRealizado() {
        Agendamento agendamento = new Agendamento();
        agendamento.setStatus(EnumAgendamento.AGENDADO);
        agendamento.realizar();
        assertEquals(EnumAgendamento.REALIZADO, agendamento.getStatus());
    }

    @Test
    void atualizarDados_ComAgendamentoRealizadoOuCancelado_DeveLancarExcecao() {
        LocalDateTime novaData = LocalDateTime.now().plusDays(1);

        Agendamento agendamentoRealizado = new Agendamento();
        agendamentoRealizado.setStatus(EnumAgendamento.REALIZADO);
        assertThrows(BusinessRuleException.class, () -> {
            agendamentoRealizado.atualizarDados(novaData, "Obs");
        });

        Agendamento agendamentoCancelado = new Agendamento();
        agendamentoCancelado.setStatus(EnumAgendamento.CANCELADO);
        assertThrows(BusinessRuleException.class, () -> {
            agendamentoCancelado.atualizarDados(novaData, "Obs");
        });
    }

    @Test
    void atualizarDados_ComAgendamentoValido_DeveAtualizar() {
        LocalDateTime novaData = LocalDateTime.now().plusDays(1);
        Agendamento agendamento = new Agendamento();
        agendamento.setStatus(EnumAgendamento.CONFIRMADO);

        agendamento.atualizarDados(novaData, "Nova Observação");

        assertEquals(novaData, agendamento.getDataHoraAgendamento());
        assertEquals("Nova Observação", agendamento.getObservacoes());
    }
}
