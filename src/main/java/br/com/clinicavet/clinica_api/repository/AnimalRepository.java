package br.com.clinicavet.clinica_api.repository;

import br.com.clinicavet.clinica_api.model.Animal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnimalRepository extends JpaRepository<Animal, Long> {

    boolean existsByNome(String nome);
    Animal findByNome(String nome);
    boolean existsByClienteId(Long clienteId);
    boolean existsByClienteIdAndNome(Long clienteId, String nome);
    boolean existsByIdAndClienteId(Long id, Long clienteId);
    boolean existsByClienteIdAndIdNot(Long clienteId, Long id);
}
