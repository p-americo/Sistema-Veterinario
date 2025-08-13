package br.com.clinicavet.clinica_api.repository;


import br.com.clinicavet.clinica_api.model.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgendamentoRepository extends JpaRepository <Agendamento, Long>{

}
