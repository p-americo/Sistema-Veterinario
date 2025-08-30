package br.com.clinicavet.clinica_api.service;


import br.com.clinicavet.clinica_api.dto.ClienteRequestDTO;
import br.com.clinicavet.clinica_api.dto.FuncionarioRequestDTO;
import br.com.clinicavet.clinica_api.model.Funcionario;
import br.com.clinicavet.clinica_api.model.Pessoa;
import br.com.clinicavet.clinica_api.model.Usuario;
import br.com.clinicavet.clinica_api.model.enums.EnumUsuarioRole;
import br.com.clinicavet.clinica_api.repository.UsuarioRepository;
import br.com.clinicavet.clinica_api.service.Interface.UsuarioService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import br.com.clinicavet.clinica_api.Execeptions.DataIntegrityViolationException;


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

        Usuario usuario = new Usuario();
        usuario.setLogin(clienteDTO.getCpf());
        usuario.setSenha(passwordEncoder.encode(clienteDTO.getSenha()));
        usuario.setRole(EnumUsuarioRole.ROLE_CLIENTE);
        usuario.setPessoa(pessoa);
        usuarioRepository.save(usuario);

    }

    @Override
    @Transactional
    public void criarUsuarioFuncionario(FuncionarioRequestDTO funcionarioDTO, Funcionario funcionario) {

        if(funcionarioDTO.getSenha() == null || funcionarioDTO.getSenha().isBlank()) {
            throw new DataIntegrityViolationException("Senha é obrigatória para criação de usuário.");
        }
        Usuario usuario = new Usuario();

        // Login pode ser o CRMV se informado, senão o CPF
        String login = (funcionarioDTO.getCrmv() != null && !funcionarioDTO.getCrmv().isBlank()) ? funcionarioDTO.getCrmv() : funcionarioDTO.getCpf();
        usuario.setLogin(login);
        usuario.setSenha(passwordEncoder.encode(funcionarioDTO.getSenha()));
        usuario.setRole(EnumUsuarioRole.ROLE_VETERINARIO); // ou outra role conforme regra
        usuario.setPessoa(funcionario);
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
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listarTodosUsuarios() {
        return usuarioRepository.findAll();
    }
}