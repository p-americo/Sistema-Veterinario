package br.com.clinicavet.clinica_api.service.Interface;

import java.util.List;

/**
 * Interface genérica para operações de CRUD.
 *
 * @param <T> A classe da Entidade (ex: Cliente)
 * @param <ID> O tipo do ID da entidade (ex: Long)
 * @param <REQ> O DTO de requisição para criação/atualização (ex: ClienteRequestDTO)
 * @param <RES> O DTO de resposta (ex: ClienteResponseDTO)
 */
public interface BaseService<T, ID, REQ, RES> {

    List<RES> listarTodos();

    RES buscarPorId(ID id);

    RES criar(REQ requestDTO);

    RES atualizar(ID id, REQ requestDTO);

    void deletar(ID id);
}