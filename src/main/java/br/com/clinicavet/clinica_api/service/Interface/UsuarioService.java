package br.com.clinicavet.clinica_api.service.Interface;

import br.com.clinicavet.clinica_api.dto.CadastrarClienteDTO;
import br.com.clinicavet.clinica_api.dto.ClienteRequestDTO;
import br.com.clinicavet.clinica_api.dto.FuncionarioRequestDTO;
import br.com.clinicavet.clinica_api.model.Cliente;
import br.com.clinicavet.clinica_api.model.Funcionario;
import br.com.clinicavet.clinica_api.model.Pessoa;
import br.com.clinicavet.clinica_api.model.Usuario;

import java.util.List;

public interface UsuarioService {

    void criarUsuarioCliente(ClienteRequestDTO clienteDTO, Pessoa pessoa);

    void criarUsuarioFuncionario(FuncionarioRequestDTO funcionarioDTO, Funcionario funcionario);

    void atualizarSenha(Long usuarioId, String senhaAtual, String novaSenha);

    List<Usuario> listarTodosUsuarios();
}
