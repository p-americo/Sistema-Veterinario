package br.com.clinicavet.clinica_api.application.service.impl;

import br.com.clinicavet.clinica_api.application.service.*;
import br.com.clinicavet.clinica_api.domain.exception.ResourceNotFoundException;
import br.com.clinicavet.clinica_api.domain.repository.GenericRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public abstract class GenericCrudServiceImplement<T, ID, REQ, RES> implements GenericCrudService<T, ID, REQ, RES> {

    protected final GenericRepository<T, ID> repository;
    protected final ModelMapper modelMapper;
    private final Class<T> entityClass;
    private final Class<RES> responseDTOClass;

    @SuppressWarnings("unchecked")
    public GenericCrudServiceImplement(GenericRepository<T, ID> repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
        this.entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        this.responseDTOClass = (Class<RES>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[3];
    }

    @Override
    public List<RES> listarTodos() {
        log.debug("Listando todos os registros de {}", entityClass.getSimpleName());
        return repository.findAll()
                .stream()
                .map(entity -> modelMapper.map(entity, responseDTOClass))
                .collect(Collectors.toList());
    }

    @Override
    public Page<RES> listarTodos(Pageable pageable) {
        log.debug("Listando todos os registros de {} de forma paginada: {}", entityClass.getSimpleName(), pageable);
        return repository.findAll(pageable)
                .map(entity -> modelMapper.map(entity, responseDTOClass));
    }

    @Override
    public RES buscarPorId(ID id) {
        log.debug("Buscando {} com ID: {}", entityClass.getSimpleName(), id);
        T entity = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("{} não encontrado com o ID: {}", entityClass.getSimpleName(), id);
                    return new ResourceNotFoundException(entityClass.getSimpleName() + " não encontrado com o ID: " + id);
                });
        return modelMapper.map(entity, responseDTOClass);
    }

    @Override
    public RES criar(REQ requestDTO) {
        log.info("Criando novo(a) {}...", entityClass.getSimpleName());
        T entity = modelMapper.map(requestDTO, entityClass);
        T savedEntity = repository.save(entity);
        log.info("{} criado(a) com sucesso.", entityClass.getSimpleName());
        return modelMapper.map(savedEntity, responseDTOClass);
    }

    @Override
    public RES atualizar(ID id, REQ requestDTO) {
        log.info("Atualizando {} com ID: {}...", entityClass.getSimpleName(), id);
        T entity = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("{} não encontrado para atualização com o ID: {}", entityClass.getSimpleName(), id);
                    return new ResourceNotFoundException(entityClass.getSimpleName() + " não encontrado para atualização com o ID: " + id);
                });

        modelMapper.map(requestDTO, entity);
        T updatedEntity = repository.save(entity);
        log.info("{} com ID: {} atualizado(a) com sucesso.", entityClass.getSimpleName(), id);
        return modelMapper.map(updatedEntity, responseDTOClass);
    }

    @Override
    public void deletar(ID id) {
        log.info("Deletando {} com ID: {}...", entityClass.getSimpleName(), id);
        if (!repository.existsById(id)) {
            log.warn("{} não encontrado para deleção com o ID: {}", entityClass.getSimpleName(), id);
            throw new ResourceNotFoundException(entityClass.getSimpleName() + " não encontrado para deleção com o ID: " + id);
        }
        repository.deleteById(id);
        log.info("{} com ID: {} deletado(a) com sucesso.", entityClass.getSimpleName(), id);
    }
}
