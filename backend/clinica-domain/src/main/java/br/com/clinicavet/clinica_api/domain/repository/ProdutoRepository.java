package br.com.clinicavet.clinica_api.domain.repository;

import br.com.clinicavet.clinica_api.domain.model.Produto;
import br.com.clinicavet.clinica_api.domain.repository.GenericRepository;



import java.util.Optional;


public interface ProdutoRepository extends GenericRepository<Produto, Long> {

    Optional<Produto> findByNomeIgnoreCase(String nome);
}
