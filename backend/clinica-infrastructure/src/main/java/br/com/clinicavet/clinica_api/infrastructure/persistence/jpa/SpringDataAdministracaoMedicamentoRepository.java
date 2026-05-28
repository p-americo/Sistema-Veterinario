package br.com.clinicavet.clinica_api.infrastructure.persistence.jpa;

import br.com.clinicavet.clinica_api.domain.model.AdministracaoMedicamento;
import br.com.clinicavet.clinica_api.domain.repository.AdministracaoMedicamentoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface SpringDataAdministracaoMedicamentoRepository extends JpaRepository<AdministracaoMedicamento, Long>, AdministracaoMedicamentoRepository {
    @Override
    @Query("SELECT a FROM AdministracaoMedicamento a WHERE a.dataHora BETWEEN :inicio AND :fim")
    List<AdministracaoMedicamento> findByDataHoraBetween(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Override
    @Query("SELECT a FROM AdministracaoMedicamento a " +
           "LEFT JOIN FETCH a.medicamento " +
           "LEFT JOIN FETCH a.funcionarioExecutor " +
           "WHERE a.entradaProntuario.id = :entradaProntuarioId")
    List<AdministracaoMedicamento> findByEntradaProntuarioIdWithDetails(@Param("entradaProntuarioId") Long entradaProntuarioId);

}
