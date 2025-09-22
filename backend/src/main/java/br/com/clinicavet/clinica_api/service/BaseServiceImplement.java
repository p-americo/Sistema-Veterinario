package br.com.clinicavet.clinica_api.service;

import br.com.clinicavet.clinica_api.service.Interface.BaseService;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public abstract class BaseServiceImplement<T, ID, REQ, RES> implements BaseService<T, ID, REQ, RES> {

    protected final JpaRepository<T, ID> repository;
    protected final ModelMapper modelMapper;
    private final Class<T> entityClass;
    private final Class<RES> responseDTOClass;

    @SuppressWarnings("unchecked")
    public BaseServiceImplement(JpaRepository<T, ID> repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
        // Usa reflexão para obter as classes genéricas em tempo de execução
        this.entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        this.responseDTOClass = (Class<RES>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[3];
    }

    @Override
    public List<RES> listarTodos() {
        return repository.findAll()
                .stream()
                .map(entity -> modelMapper.map(entity, responseDTOClass))
                .collect(Collectors.toList());
    }

    @Override
    public RES buscarPorId(ID id) {
        T entity = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(entityClass.getSimpleName() + " não encontrado com o ID: " + id));
        return modelMapper.map(entity, responseDTOClass);
    }

    @Override
    public RES criar(REQ requestDTO) {
        T entity = modelMapper.map(requestDTO, entityClass);
        T savedEntity = repository.save(entity);
        return modelMapper.map(savedEntity, responseDTOClass);
    }

    @Override
    public RES atualizar(ID id, REQ requestDTO) {
        T entity = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(entityClass.getSimpleName() + " não encontrado para atualização com o ID: " + id));

        modelMapper.map(requestDTO, entity);
        T updatedEntity = repository.save(entity);
        return modelMapper.map(updatedEntity, responseDTOClass);
    }

    @Override
    public void deletar(ID id) {
        if (!repository.existsById(id)) {
            throw new NoSuchElementException(entityClass.getSimpleName() + " não encontrado para deleção com o ID: " + id);
        }
        repository.deleteById(id);
    }
}