package br.com.clinicavet.clinica_api.service.Interface;

import br.com.clinicavet.clinica_api.dto.AdministracaoMedicamentoRequestDTO;
import br.com.clinicavet.clinica_api.dto.AdministracaoMedicamentoResponseDTO;
import br.com.clinicavet.clinica_api.dto.AdministracaoMedicamentoUpdateDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminstracaoMedicamentoService {
    
    AdministracaoMedicamentoResponseDTO criarAdministracao(AdministracaoMedicamentoRequestDTO administracaoRequestDTO);
    
    AdministracaoMedicamentoResponseDTO atualizarAdministracao(Long id, AdministracaoMedicamentoUpdateDTO administracaoUpdateDTO);
    
    void deletarAdministracao(Long id);
    
    AdministracaoMedicamentoResponseDTO buscarPorId(Long id);
    
    List<AdministracaoMedicamentoResponseDTO> buscarPorEntradaProntuarioId(Long entradaProntuarioId);
    
    List<AdministracaoMedicamentoResponseDTO> buscarPorMedicamentoId(Long medicamentoId);
    
    List<AdministracaoMedicamentoResponseDTO> buscarPorFuncionarioId(Long funcionarioId);
    
    List<AdministracaoMedicamentoResponseDTO> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim);
    
    List<AdministracaoMedicamentoResponseDTO> listarTodos();
}
