package br.com.clinicavet.clinica_api.application.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface GenericCrudService<T, ID, REQ, RES> {

    List<RES> listarTodos();

    Page<RES> listarTodos(Pageable pageable);

    RES buscarPorId(ID id);

    RES criar(REQ requestDTO);

    RES atualizar(ID id, REQ requestDTO);

    void deletar(ID id);
}
