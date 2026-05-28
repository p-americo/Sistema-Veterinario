package br.com.clinicavet.clinica_api.application.service;

import br.com.clinicavet.clinica_api.application.dto.CadastrarClienteDTO;
import br.com.clinicavet.clinica_api.application.dto.ClienteRequestDTO;
import br.com.clinicavet.clinica_api.application.dto.FuncionarioRequestDTO;
import br.com.clinicavet.clinica_api.domain.model.Cliente;
import br.com.clinicavet.clinica_api.domain.model.Funcionario;
import br.com.clinicavet.clinica_api.domain.model.Pessoa;
import br.com.clinicavet.clinica_api.domain.model.Usuario;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UsuarioService {

    void criarUsuarioCliente(ClienteRequestDTO clienteDTO, Pessoa pessoa);

    void criarUsuarioFuncionario(FuncionarioRequestDTO funcionarioDTO, Funcionario funcionario);

    void atualizarSenha(Long usuarioId, String senhaAtual, String novaSenha);

    Page<Usuario> listarTodosUsuarios(Pageable pageable);
}
