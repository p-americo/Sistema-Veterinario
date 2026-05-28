package br.com.clinicavet.clinica_api.infrastructure.persistence.jpa;

import br.com.clinicavet.clinica_api.domain.model.Funcionario;
import br.com.clinicavet.clinica_api.domain.repository.FuncionarioRepository;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumCargo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface SpringDataFuncionarioRepository extends JpaRepository<Funcionario, Long>, FuncionarioRepository {
    @Override
    @Query("SELECT f FROM Funcionario f WHERE f.cargo.cargo = :cargoEnum")
    List<Funcionario> findByCargoEnum(@Param("cargoEnum") EnumCargo cargoEnum);

}
