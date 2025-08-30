package br.com.clinicavet.clinica_api.repository;

import br.com.clinicavet.clinica_api.model.Cargo;
import br.com.clinicavet.clinica_api.model.enums.EnumCargo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CargoRepository extends JpaRepository<Cargo, Long> {


    Optional<Cargo> findByCargo(EnumCargo cargo);


}
