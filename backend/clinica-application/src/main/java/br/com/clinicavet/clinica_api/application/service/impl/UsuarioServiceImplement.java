package br.com.clinicavet.clinica_api.application.service.impl;

import br.com.clinicavet.clinica_api.application.service.*;


import br.com.clinicavet.clinica_api.application.dto.ClienteRequestDTO;
import br.com.clinicavet.clinica_api.application.dto.FuncionarioRequestDTO;
import br.com.clinicavet.clinica_api.domain.model.Funcionario;
import br.com.clinicavet.clinica_api.domain.model.Pessoa;
import br.com.clinicavet.clinica_api.domain.model.Usuario;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumUsuarioRole;
import br.com.clinicavet.clinica_api.domain.repository.UsuarioRepository;
import br.com.clinicavet.clinica_api.application.service.UsuarioService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import br.com.clinicavet.clinica_api.domain.exception.DataIntegrityViolationException;
import java.util.Optional;


@Service
public class UsuarioServiceImplement implements UsuarioService {


    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImplement (UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void criarUsuarioCliente(ClienteRequestDTO clienteDTO, Pessoa pessoa) {
        if(clienteDTO.getSenha() == null || clienteDTO.getSenha().isBlank()) {
            throw new DataIntegrityViolationException("Senha é obrigatória para criação de usuário.");
        }

        Usuario usuario = Usuario.criarUsuario(
                clienteDTO.getCpf(),
                passwordEncoder.encode(clienteDTO.getSenha()),
                EnumUsuarioRole.ROLE_CLIENTE,
                pessoa
        );
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void criarUsuarioFuncionario(FuncionarioRequestDTO funcionarioDTO, Funcionario funcionario) {
        if(funcionarioDTO.getSenha() == null || funcionarioDTO.getSenha().isBlank()) {
            throw new DataIntegrityViolationException("Senha é obrigatória para criação de usuário.");
        }

        // Login pode ser o CRMV se informado, senão o CPF
        String login = (funcionarioDTO.getCrmv() != null && !funcionarioDTO.getCrmv().isBlank()) ? funcionarioDTO.getCrmv() : funcionarioDTO.getCpf();
        
        Usuario usuario = Usuario.criarUsuario(
                login,
                passwordEncoder.encode(funcionarioDTO.getSenha()),
                EnumUsuarioRole.ROLE_VETERINARIO, // ou outra role conforme regra
                funcionario
        );
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void atualizarSenha(Long usuarioId, String senhaAtual, String novaSenha) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
        if (usuarioOpt.isEmpty()) {
            throw new DataIntegrityViolationException("Usuário não encontrado.");
        }
        Usuario usuario = usuarioOpt.get();
        if (!passwordEncoder.matches(senhaAtual, usuario.getSenha())) {
            throw new DataIntegrityViolationException("Senha atual incorreta.");
        }
        usuario.alterarSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Usuario> listarTodosUsuarios(Pageable pageable) {
        return usuarioRepository.findAll(pageable);
    }
}