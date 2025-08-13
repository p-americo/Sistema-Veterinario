package br.com.clinicavet.clinica_api.service.Interface;

import br.com.clinicavet.clinica_api.dto.FuncionarioRequestDTO;
import br.com.clinicavet.clinica_api.dto.FuncionarioResponseDTO;
import br.com.clinicavet.clinica_api.dto.FuncionarioUpdateDTO;

import java.util.List;

public interface FuncionarioService {

    FuncionarioResponseDTO criarFuncionario(FuncionarioRequestDTO requestDTO);

    FuncionarioResponseDTO atualizarFuncionario(Long id, FuncionarioUpdateDTO requestDTO);

    FuncionarioResponseDTO buscarPorId(Long id);

    List<FuncionarioResponseDTO> buscarTodos();

    void deletarFuncionario(Long id);
}
