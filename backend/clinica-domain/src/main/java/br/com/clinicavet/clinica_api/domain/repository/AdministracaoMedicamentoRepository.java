package br.com.clinicavet.clinica_api.domain.repository;

import br.com.clinicavet.clinica_api.domain.model.AdministracaoMedicamento;
import br.com.clinicavet.clinica_api.domain.repository.GenericRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AdministracaoMedicamentoRepository extends GenericRepository<AdministracaoMedicamento, Long> {
    
    List<AdministracaoMedicamento> findByEntradaProntuarioId(Long entradaProntuarioId);
    
    List<AdministracaoMedicamento> findByMedicamentoId(Long medicamentoId);
    
    List<AdministracaoMedicamento> findByFuncionarioExecutorId(Long funcionarioId);
    
    
    List<AdministracaoMedicamento> findByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim);
    
    
    List<AdministracaoMedicamento> findByEntradaProntuarioIdWithDetails(Long entradaProntuarioId);
}