package br.com.clinicavet.clinica_api.domain.repository;

import br.com.clinicavet.clinica_api.domain.model.Medicamento;
import br.com.clinicavet.clinica_api.domain.repository.GenericRepository;



import java.util.List;
import java.util.Optional;


public interface MedicamentoRepository extends GenericRepository<Medicamento, Long> {


    List<Medicamento> findByProdutoNomeContainingIgnoreCase(String nome);


    Optional<Medicamento> findByProdutoNomeIgnoreCase(String nome);
}
