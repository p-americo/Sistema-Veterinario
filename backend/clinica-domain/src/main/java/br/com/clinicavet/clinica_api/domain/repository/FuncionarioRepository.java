package br.com.clinicavet.clinica_api.domain.repository;

import br.com.clinicavet.clinica_api.domain.model.Funcionario;
import br.com.clinicavet.clinica_api.domain.repository.GenericRepository;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumCargo; // <-- Importe o Enum

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FuncionarioRepository extends GenericRepository<Funcionario, Long>{

    boolean existsByCargoId(Long cargoId);
    boolean existsByCrmv(String crmv);

    
    List<Funcionario> findByCargoEnum(EnumCargo cargoEnum);

    List<Funcionario> findByNomeContainingIgnoreCase(String nome);
}