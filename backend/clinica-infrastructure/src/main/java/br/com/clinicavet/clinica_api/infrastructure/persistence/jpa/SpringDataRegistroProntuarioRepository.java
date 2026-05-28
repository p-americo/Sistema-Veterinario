package br.com.clinicavet.clinica_api.infrastructure.persistence.jpa;

import br.com.clinicavet.clinica_api.domain.model.RegistroProntuario;
import br.com.clinicavet.clinica_api.domain.repository.RegistroProntuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface SpringDataRegistroProntuarioRepository extends JpaRepository<RegistroProntuario, Long>, RegistroProntuarioRepository {

    @Override
    @EntityGraph(attributePaths = {"prontuario", "veterinarioResponsavel", "internacao", "agendamento"})
    Page<RegistroProntuario> findAll(Pageable pageable);
    @Override
    @Query("SELECT r FROM RegistroProntuario r LEFT JOIN FETCH r.veterinarioResponsavel WHERE r.prontuario.id = :prontuarioId ORDER BY r.dataHora DESC")
    List<RegistroProntuario> findByProntuarioIdOrderByDataHoraDesc(@Param("prontuarioId") Long prontuarioId);

    @Override
    @Query("SELECT r FROM RegistroProntuario r LEFT JOIN FETCH r.medicamentosAdministrados WHERE r.id = :id")
    RegistroProntuario findByIdWithMedicamentos(@Param("id") Long id);

    @Override
    @Query("SELECT r FROM RegistroProntuario r " +
            "LEFT JOIN FETCH r.veterinarioResponsavel " +
            "LEFT JOIN FETCH r.internacao " +
            "WHERE r.prontuario.id = :prontuarioId " +
            "ORDER BY r.dataHora DESC")
    List<RegistroProntuario> findByProntuarioIdWithDetails(@Param("prontuarioId") Long prontuarioId);

}
