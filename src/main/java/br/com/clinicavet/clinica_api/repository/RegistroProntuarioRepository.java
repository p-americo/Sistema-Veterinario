package br.com.clinicavet.clinica_api.repository;

import br.com.clinicavet.clinica_api.model.RegistroProntuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RegistroProntuarioRepository extends JpaRepository<RegistroProntuario, Long> {

    // Este método simples pode ser mantido para outras finalidades
    List<RegistroProntuario> findByProntuarioId(Long prontuarioId);

    // --- CORREÇÃO APLICADA AQUI ---
    // Adicionamos a query com JOIN FETCH a este método, que é o que seu serviço usa.
    // Isso garante que os dados do veterinário sejam carregados imediatamente.
    @Query("SELECT r FROM RegistroProntuario r LEFT JOIN FETCH r.veterinarioResponsavel WHERE r.prontuario.id = :prontuarioId ORDER BY r.dataHora DESC")
    List<RegistroProntuario> findByProntuarioIdOrderByDataHoraDesc(@Param("prontuarioId") Long prontuarioId);

    List<RegistroProntuario> findByVeterinarioResponsavelId(Long veterinarioId);

    List<RegistroProntuario> findByInternacaoId(Long internacaoId);

    List<RegistroProntuario> findByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim);

    // Esta query está correta para buscar um único registro com seus medicamentos
    @Query("SELECT r FROM RegistroProntuario r LEFT JOIN FETCH r.medicamentosAdministrados WHERE r.id = :id")
    RegistroProntuario findByIdWithMedicamentos(@Param("id") Long id);

    // Este método se torna redundante com a correção acima, mas pode ser mantido se for usado em outro lugar.
    @Query("SELECT r FROM RegistroProntuario r " +
            "LEFT JOIN FETCH r.veterinarioResponsavel " +
            "LEFT JOIN FETCH r.internacao " +
            "WHERE r.prontuario.id = :prontuarioId " +
            "ORDER BY r.dataHora DESC")
    List<RegistroProntuario> findByProntuarioIdWithDetails(@Param("prontuarioId") Long prontuarioId);
}