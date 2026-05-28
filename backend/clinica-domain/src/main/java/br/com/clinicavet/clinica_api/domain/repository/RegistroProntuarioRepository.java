package br.com.clinicavet.clinica_api.domain.repository;

import br.com.clinicavet.clinica_api.domain.model.RegistroProntuario;
import br.com.clinicavet.clinica_api.domain.repository.GenericRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RegistroProntuarioRepository extends GenericRepository<RegistroProntuario, Long> {

    // Este método simples pode ser mantido para outras finalidades
    List<RegistroProntuario> findByProntuarioId(Long prontuarioId);

    // --- CORREÇÃO APLICADA AQUI ---
    // Adicionamos a query com JOIN FETCH a este método, que é o que seu serviço usa.
    // Isso garante que os dados do veterinário sejam carregados imediatamente.
    
    List<RegistroProntuario> findByProntuarioIdOrderByDataHoraDesc(Long prontuarioId);

    List<RegistroProntuario> findByVeterinarioResponsavelId(Long veterinarioId);

    List<RegistroProntuario> findByInternacaoId(Long internacaoId);

    List<RegistroProntuario> findByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim);

    // Esta query está correta para buscar um único registro com seus medicamentos
    
    RegistroProntuario findByIdWithMedicamentos(Long id);

    // Este método se torna redundante com a correção acima, mas pode ser mantido se for usado em outro lugar.
    
    List<RegistroProntuario> findByProntuarioIdWithDetails(Long prontuarioId);
}