package br.com.clinicavet.clinica_api.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface GenericRepository<T, ID> {
    List<T> findAll();
    Page<T> findAll(Pageable pageable);
    Optional<T> findById(ID id);
    T save(T entity);
    void deleteById(ID id);
    void delete(T entity);
    boolean existsById(ID id);
    long count();
    <S extends T> List<S> saveAll(Iterable<S> entities);
}
