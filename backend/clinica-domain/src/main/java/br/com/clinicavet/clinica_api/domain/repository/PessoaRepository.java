package br.com.clinicavet.clinica_api.domain.repository;

import br.com.clinicavet.clinica_api.domain.model.Pessoa;
import br.com.clinicavet.clinica_api.domain.repository.GenericRepository;


public interface PessoaRepository extends GenericRepository<Pessoa, Long> {

    // Verifica se cpf já é existente

    boolean existsByCpf(String cpf);
    boolean existsByEmail(String email);
}