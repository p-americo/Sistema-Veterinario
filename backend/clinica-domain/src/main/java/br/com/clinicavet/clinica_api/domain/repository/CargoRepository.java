package br.com.clinicavet.clinica_api.domain.repository;

import br.com.clinicavet.clinica_api.domain.model.Cargo;
import br.com.clinicavet.clinica_api.domain.repository.GenericRepository;
import br.com.clinicavet.clinica_api.domain.model.enums.EnumCargo;


import java.util.Optional;

public interface CargoRepository extends GenericRepository<Cargo, Long> {


    Optional<Cargo> findByCargo(EnumCargo cargo);


}
