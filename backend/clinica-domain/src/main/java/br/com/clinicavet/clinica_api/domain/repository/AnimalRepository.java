package br.com.clinicavet.clinica_api.domain.repository;

import br.com.clinicavet.clinica_api.domain.model.Animal;
import br.com.clinicavet.clinica_api.domain.repository.GenericRepository;


public interface AnimalRepository extends GenericRepository<Animal, Long> {

    boolean existsByNome(String nome);
    Animal findByNome(String nome);
    boolean existsByClienteId(Long clienteId);
    boolean existsByClienteIdAndNome(Long clienteId, String nome);
    boolean existsByIdAndClienteId(Long id, Long clienteId);
    boolean existsByClienteIdAndIdNot(Long clienteId, Long id);
}
