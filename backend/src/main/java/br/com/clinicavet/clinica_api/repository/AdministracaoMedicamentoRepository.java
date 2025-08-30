package br.com.clinicavet.clinica_api.repository;

import br.com.clinicavet.clinica_api.model.AdministracaoMedicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AdministracaoMedicamentoRepository extends JpaRepository<AdministracaoMedicamento, Long> {
    
    List<AdministracaoMedicamento> findByEntradaProntuarioId(Long entradaProntuarioId);
    
    List<AdministracaoMedicamento> findByMedicamentoId(Long medicamentoId);
    
    List<AdministracaoMedicamento> findByFuncionarioExecutorId(Long funcionarioId);
    
    @Query("SELECT a FROM AdministracaoMedicamento a WHERE a.dataHora BETWEEN :inicio AND :fim")
    List<AdministracaoMedicamento> findByDataHoraBetween(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);
    
    @Query("SELECT a FROM AdministracaoMedicamento a " +
           "LEFT JOIN FETCH a.medicamento " +
           "LEFT JOIN FETCH a.funcionarioExecutor " +
           "WHERE a.entradaProntuario.id = :entradaProntuarioId")
    List<AdministracaoMedicamento> findByEntradaProntuarioIdWithDetails(@Param("entradaProntuarioId") Long entradaProntuarioId);
}