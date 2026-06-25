package br.com.clinicavet.clinica_api.application.event;

import br.com.clinicavet.clinica_api.application.dto.ClienteRequestDTO;
import br.com.clinicavet.clinica_api.application.dto.FuncionarioRequestDTO;
import br.com.clinicavet.clinica_api.application.service.UsuarioService;
import br.com.clinicavet.clinica_api.domain.model.Cliente;
import br.com.clinicavet.clinica_api.domain.model.Funcionario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UsuarioEventListenerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioEventListener usuarioEventListener;

    private Cliente cliente;
    private Funcionario funcionario;
    private ClienteRequestDTO clienteRequestDTO;
    private FuncionarioRequestDTO funcionarioRequestDTO;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Maria Silva");
        cliente.setCpf("12345678901");

        funcionario = new Funcionario();
        funcionario.setId(10L);
        funcionario.setNome("Dr. João");
        funcionario.setCpf("98765432109");

        clienteRequestDTO = new ClienteRequestDTO();
        clienteRequestDTO.setNome("Maria Silva");
        clienteRequestDTO.setCpf("12345678901");

        funcionarioRequestDTO = new FuncionarioRequestDTO();
        funcionarioRequestDTO.setNome("Dr. João");
        funcionarioRequestDTO.setCpf("98765432109");
    }

    @Test
    void handleClienteCriado_Sucesso() {
        ClienteCriadoEvent event = new ClienteCriadoEvent(clienteRequestDTO, cliente);

        usuarioEventListener.handleClienteCriado(event);

        verify(usuarioService, times(1)).criarUsuarioCliente(clienteRequestDTO, cliente);
    }

    @Test
    void handleFuncionarioCriado_Sucesso() {
        FuncionarioCriadoEvent event = new FuncionarioCriadoEvent(funcionarioRequestDTO, funcionario);

        usuarioEventListener.handleFuncionarioCriado(event);

        verify(usuarioService, times(1)).criarUsuarioFuncionario(funcionarioRequestDTO, funcionario);
    }
}
