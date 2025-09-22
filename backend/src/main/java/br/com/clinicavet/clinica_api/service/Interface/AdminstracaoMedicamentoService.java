package br.com.clinicavet.clinica_api.service.Interface;

import br.com.clinicavet.clinica_api.dto.AdministracaoMedicamentoRequestDTO;
import br.com.clinicavet.clinica_api.dto.AdministracaoMedicamentoResponseDTO;
import br.com.clinicavet.clinica_api.model.AdministracaoMedicamento;


import java.time.LocalDateTime;
import java.util.List;

public interface AdminstracaoMedicamentoService extends BaseService<AdministracaoMedicamento, Long, AdministracaoMedicamentoRequestDTO, AdministracaoMedicamentoResponseDTO> {
    
    AdministracaoMedicamentoResponseDTO criar(AdministracaoMedicamentoRequestDTO administracaoRequestDTO);

    List<AdministracaoMedicamentoResponseDTO> buscarPorEntradaProntuarioId(Long entradaProntuarioId);
    
    List<AdministracaoMedicamentoResponseDTO> buscarPorMedicamentoId(Long medicamentoId);
    
    List<AdministracaoMedicamentoResponseDTO> buscarPorFuncionarioId(Long funcionarioId);
    
    List<AdministracaoMedicamentoResponseDTO> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim);

    AdministracaoMedicamentoResponseDTO atualizar(Long id, AdministracaoMedicamentoRequestDTO dto);
}
