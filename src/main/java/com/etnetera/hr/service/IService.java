package com.etnetera.hr.service;

import java.util.Optional;
import java.util.Set;

public interface IService<T> {
    Iterable<T> findAll();
    Optional<T> findById(Long id);
    Boolean existsById(Long id);
    T save(T entity);
    void saveAll(Set<T> entities);
    void delete(T entity);
    void deleteById(Long id);
}
