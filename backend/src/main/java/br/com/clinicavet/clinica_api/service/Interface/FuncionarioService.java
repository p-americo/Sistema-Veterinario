package br.com.clinicavet.clinica_api.service.Interface;

import br.com.clinicavet.clinica_api.dto.FuncionarioRequestDTO;
import br.com.clinicavet.clinica_api.dto.FuncionarioResponseDTO;
import br.com.clinicavet.clinica_api.dto.FuncionarioUpdateDTO;
import br.com.clinicavet.clinica_api.model.Funcionario;

import java.util.List;

public interface FuncionarioService extends BaseService<Funcionario, Long, FuncionarioRequestDTO, FuncionarioResponseDTO> {

    FuncionarioResponseDTO criar(FuncionarioRequestDTO requestDTO);

    void deletar(Long id);

    FuncionarioResponseDTO atualizar(Long id, FuncionarioUpdateDTO dto);

    List<FuncionarioResponseDTO> listarVeterinarios();
}
