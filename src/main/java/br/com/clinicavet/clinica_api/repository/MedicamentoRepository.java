package br.com.clinicavet.clinica_api.repository;

import br.com.clinicavet.clinica_api.model.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {


    List<Medicamento> findByProdutoNomeContainingIgnoreCase(String nome);


    Optional<Medicamento> findByProdutoNomeIgnoreCase(String nome);
}
