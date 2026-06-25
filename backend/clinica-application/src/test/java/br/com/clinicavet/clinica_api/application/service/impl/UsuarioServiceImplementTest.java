package br.com.clinicavet.clinica_api.application.service.impl;

import br.com.clinicavet.clinica_api.application.dto.ClienteRequestDTO;
import br.com.clinicavet.clinica_api.application.dto.FuncionarioRequestDTO;
import br.com.clinicavet.clinica_api.domain.exception.DataIntegrityViolationException;
import br.com.clinicavet.clinica_api.domain.model.Cliente;
import br.com.clinicavet.clinica_api.domain.model.Funcionario;
import br.com.clinicavet.clinica_api.domain.model.Pessoa;
import br.com.clinicavet.clinica_api.domain.model.Usuario;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumUsuarioRole;
import br.com.clinicavet.clinica_api.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UsuarioServiceImplementTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioServiceImplement usuarioService;

    private Cliente cliente;
    private Funcionario funcionario;
    private ClienteRequestDTO clienteRequestDTO;
    private FuncionarioRequestDTO funcionarioRequestDTO;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Maria Silva");
        cliente.setCpf("12345678901");
        cliente.setEmail("maria@email.com");

        funcionario = new Funcionario();
        funcionario.setId(10L);
        funcionario.setNome("Dr. João");
        funcionario.setCpf("98765432109");
        funcionario.setEmail("joao@email.com");

        clienteRequestDTO = new ClienteRequestDTO();
        clienteRequestDTO.setNome("Maria Silva");
        clienteRequestDTO.setCpf("12345678901");
        clienteRequestDTO.setEmail("maria@email.com");
        clienteRequestDTO.setSenha("Senha@123");

        funcionarioRequestDTO = new FuncionarioRequestDTO();
        funcionarioRequestDTO.setNome("Dr. João");
        funcionarioRequestDTO.setCpf("98765432109");
        funcionarioRequestDTO.setEmail("joao@email.com");
        funcionarioRequestDTO.setDataAdmissao(LocalDate.of(2024, 1, 10));
        funcionarioRequestDTO.setCrmv("12345-SP");
        funcionarioRequestDTO.setCargoId(1L);
        funcionarioRequestDTO.setSenha("Senha@123");

        usuario = Usuario.criarUsuario("12345678901", "hashSenha", EnumUsuarioRole.ROLE_CLIENTE, cliente);
        usuario.setId(100L);
    }

    @Test
    void criarUsuarioCliente_Sucesso() {
        when(passwordEncoder.encode(clienteRequestDTO.getSenha())).thenReturn("hashSenha");

        assertDoesNotThrow(() -> usuarioService.criarUsuarioCliente(clienteRequestDTO, cliente));

        verify(passwordEncoder, times(1)).encode("Senha@123");
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void criarUsuarioCliente_ExceptionSenhaNula() {
        clienteRequestDTO.setSenha(null);

        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> {
            usuarioService.criarUsuarioCliente(clienteRequestDTO, cliente);
        });

        assertEquals("Senha é obrigatória para criação de usuário.", exception.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void criarUsuarioCliente_ExceptionSenhaEmBranco() {
        clienteRequestDTO.setSenha("   ");

        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> {
            usuarioService.criarUsuarioCliente(clienteRequestDTO, cliente);
        });

        assertEquals("Senha é obrigatória para criação de usuário.", exception.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void criarUsuarioFuncionario_SucessoComCrmv() {
        when(passwordEncoder.encode(funcionarioRequestDTO.getSenha())).thenReturn("hashSenha");

        assertDoesNotThrow(() -> usuarioService.criarUsuarioFuncionario(funcionarioRequestDTO, funcionario));

        verify(passwordEncoder, times(1)).encode("Senha@123");
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void criarUsuarioFuncionario_SucessoSemCrmvUsaCpfComoLogin() {
        funcionarioRequestDTO.setCrmv(null);
        when(passwordEncoder.encode(funcionarioRequestDTO.getSenha())).thenReturn("hashSenha");

        assertDoesNotThrow(() -> usuarioService.criarUsuarioFuncionario(funcionarioRequestDTO, funcionario));

        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void criarUsuarioFuncionario_SucessoComCrmvEmBrancoUsaCpfComoLogin() {
        funcionarioRequestDTO.setCrmv("   ");
        when(passwordEncoder.encode(funcionarioRequestDTO.getSenha())).thenReturn("hashSenha");

        assertDoesNotThrow(() -> usuarioService.criarUsuarioFuncionario(funcionarioRequestDTO, funcionario));

        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void criarUsuarioFuncionario_ExceptionSenhaNula() {
        funcionarioRequestDTO.setSenha(null);

        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> {
            usuarioService.criarUsuarioFuncionario(funcionarioRequestDTO, funcionario);
        });

        assertEquals("Senha é obrigatória para criação de usuário.", exception.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void criarUsuarioFuncionario_ExceptionSenhaEmBranco() {
        funcionarioRequestDTO.setSenha("");

        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> {
            usuarioService.criarUsuarioFuncionario(funcionarioRequestDTO, funcionario);
        });

        assertEquals("Senha é obrigatória para criação de usuário.", exception.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void atualizarSenha_Sucesso() {
        when(usuarioRepository.findById(100L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senhaAtual", usuario.getSenha())).thenReturn(true);
        when(passwordEncoder.encode("novaSenha")).thenReturn("novaHash");
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        assertDoesNotThrow(() -> usuarioService.atualizarSenha(100L, "senhaAtual", "novaSenha"));

        assertEquals("novaHash", usuario.getSenha());
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    void atualizarSenha_ExceptionUsuarioNaoEncontrado() {
        when(usuarioRepository.findById(100L)).thenReturn(Optional.empty());

        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> {
            usuarioService.atualizarSenha(100L, "senhaAtual", "novaSenha");
        });

        assertEquals("Usuário não encontrado.", exception.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void atualizarSenha_ExceptionSenhaAtualIncorreta() {
        when(usuarioRepository.findById(100L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senhaErrada", usuario.getSenha())).thenReturn(false);

        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> {
            usuarioService.atualizarSenha(100L, "senhaErrada", "novaSenha");
        });

        assertEquals("Senha atual incorreta.", exception.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void listarTodosUsuarios_Sucesso() {
        Pageable pageable = Pageable.unpaged();
        Page<Usuario> page = new PageImpl<>(Collections.singletonList(usuario));
        when(usuarioRepository.findAll(pageable)).thenReturn(page);

        Page<Usuario> resultado = usuarioService.listarTodosUsuarios(pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals(usuario, resultado.getContent().get(0));
        verify(usuarioRepository, times(1)).findAll(pageable);
    }
}
