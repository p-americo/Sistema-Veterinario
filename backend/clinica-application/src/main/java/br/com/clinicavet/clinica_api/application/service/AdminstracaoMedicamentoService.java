package br.com.clinicavet.clinica_api.application.service;

import br.com.clinicavet.clinica_api.application.dto.AdministracaoMedicamentoRequestDTO;
import br.com.clinicavet.clinica_api.application.dto.AdministracaoMedicamentoResponseDTO;
import br.com.clinicavet.clinica_api.domain.model.AdministracaoMedicamento;


import java.time.LocalDateTime;
import java.util.List;

public interface AdminstracaoMedicamentoService extends GenericCrudService<AdministracaoMedicamento, Long, AdministracaoMedicamentoRequestDTO, AdministracaoMedicamentoResponseDTO> {
    
    AdministracaoMedicamentoResponseDTO criar(AdministracaoMedicamentoRequestDTO administracaoRequestDTO);

    List<AdministracaoMedicamentoResponseDTO> buscarPorEntradaProntuarioId(Long entradaProntuarioId);
    
    List<AdministracaoMedicamentoResponseDTO> buscarPorMedicamentoId(Long medicamentoId);
    
    List<AdministracaoMedicamentoResponseDTO> buscarPorFuncionarioId(Long funcionarioId);
    
    List<AdministracaoMedicamentoResponseDTO> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim);

    AdministracaoMedicamentoResponseDTO atualizar(Long id, AdministracaoMedicamentoRequestDTO dto);
}
