package br.com.clinicavet.clinica_api.repository;

import br.com.clinicavet.clinica_api.model.Funcionario;
import br.com.clinicavet.clinica_api.model.enums.EnumCargo; // <-- Importe o Enum
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FuncionarioRepository extends JpaRepository <Funcionario, Long>{

    boolean existsByCargoId(Long cargoId);
    boolean existsByCrmv(String crmv);

    @Query("SELECT f FROM Funcionario f WHERE f.cargo.cargo = :cargoEnum")
    List<Funcionario> findByCargoEnum(@Param("cargoEnum") EnumCargo cargoEnum);

}