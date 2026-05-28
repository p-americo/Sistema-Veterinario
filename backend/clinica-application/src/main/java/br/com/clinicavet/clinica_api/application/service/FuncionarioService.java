package br.com.clinicavet.clinica_api.application.service;

import br.com.clinicavet.clinica_api.application.dto.FuncionarioRequestDTO;
import br.com.clinicavet.clinica_api.application.dto.FuncionarioResponseDTO;
import br.com.clinicavet.clinica_api.application.dto.FuncionarioUpdateDTO;
import br.com.clinicavet.clinica_api.domain.model.Funcionario;

import java.util.List;

public interface FuncionarioService extends GenericCrudService<Funcionario, Long, FuncionarioRequestDTO, FuncionarioResponseDTO> {

    FuncionarioResponseDTO criar(FuncionarioRequestDTO requestDTO);

    void deletar(Long id);

    FuncionarioResponseDTO atualizar(Long id, FuncionarioUpdateDTO dto);

    List<FuncionarioResponseDTO> listarVeterinarios();

    List<FuncionarioResponseDTO> buscarPorNome(String nome);
}
