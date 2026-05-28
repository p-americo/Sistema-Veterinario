package br.com.clinicavet.clinica_api.domain.repository;

import br.com.clinicavet.clinica_api.domain.model.Cliente;
import br.com.clinicavet.clinica_api.domain.repository.GenericRepository;


import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends GenericRepository<Cliente, Long> {

    boolean existsByCpf(String cpf);
    
    Optional<Cliente> findByCpf(String cpf);
    
    List<Cliente> findByNomeContainingIgnoreCase(String nome);

    Optional<Cliente> findByCpfOrEmail(String cpf, String email);


}
