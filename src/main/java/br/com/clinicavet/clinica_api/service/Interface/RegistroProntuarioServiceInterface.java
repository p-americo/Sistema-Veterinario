package br.com.clinicavet.clinica_api.service.Interface;

import br.com.clinicavet.clinica_api.dto.RegistroProntuarioRequestDTO;
import br.com.clinicavet.clinica_api.dto.RegistroProntuarioResponseDTO;
import br.com.clinicavet.clinica_api.dto.RegistroProntuarioUpdateDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface RegistroProntuarioServiceInterface {
    
    RegistroProntuarioResponseDTO criarRegistro(RegistroProntuarioRequestDTO registroRequestDTO);
    
    RegistroProntuarioResponseDTO atualizarRegistro(Long id, RegistroProntuarioUpdateDTO registroUpdateDTO);
    
    void deletarRegistro(Long id);
    
    RegistroProntuarioResponseDTO buscarPorId(Long id);
    
    List<RegistroProntuarioResponseDTO> buscarPorProntuarioId(Long prontuarioId);
    
    List<RegistroProntuarioResponseDTO> buscarPorVeterinarioId(Long veterinarioId);
    
    List<RegistroProntuarioResponseDTO> buscarPorInternacaoId(Long internacaoId);
    
    List<RegistroProntuarioResponseDTO> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim);
    
    List<RegistroProntuarioResponseDTO> listarTodos();
    
    RegistroProntuarioResponseDTO buscarPorIdComMedicamentos(Long id);
}