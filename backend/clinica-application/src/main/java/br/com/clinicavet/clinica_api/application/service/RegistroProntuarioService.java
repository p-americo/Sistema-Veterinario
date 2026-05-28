package br.com.clinicavet.clinica_api.application.service;

import br.com.clinicavet.clinica_api.application.dto.RegistroProntuarioRequestDTO;
import br.com.clinicavet.clinica_api.application.dto.RegistroProntuarioResponseDTO;
import br.com.clinicavet.clinica_api.domain.model.RegistroProntuario;

import java.time.LocalDateTime;
import java.util.List;

public interface RegistroProntuarioService extends GenericCrudService<RegistroProntuario, Long, RegistroProntuarioRequestDTO, RegistroProntuarioResponseDTO> {
    
    RegistroProntuarioResponseDTO criar(RegistroProntuarioRequestDTO registroRequestDTO);
    
    List<RegistroProntuarioResponseDTO> buscarPorProntuarioId(Long prontuarioId);
    
    List<RegistroProntuarioResponseDTO> buscarPorVeterinarioId(Long veterinarioId);
    
    List<RegistroProntuarioResponseDTO> buscarPorInternacaoId(Long internacaoId);
    
    List<RegistroProntuarioResponseDTO> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim);

    RegistroProntuarioResponseDTO buscarPorIdComMedicamentos(Long id);

    RegistroProntuarioResponseDTO atualizar(Long id, RegistroProntuarioRequestDTO registroRequestDTO);
}