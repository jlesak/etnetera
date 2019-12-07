package com.etnetera.hr.service;

import com.etnetera.hr.data.JavaScriptFramework;
import com.etnetera.hr.repository.JavaScriptFrameworkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class JavaScriptFrameworkService implements IService<JavaScriptFramework> {

    private final JavaScriptFrameworkRepository repository;

    @Autowired
    public JavaScriptFrameworkService(JavaScriptFrameworkRepository repository) {
        this.repository = repository;
    }

    @Override
    public Iterable<JavaScriptFramework> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<JavaScriptFramework> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Boolean existsById(Long id) {
        return repository.existsById(id);
    }

    @Override
    public JavaScriptFramework save(JavaScriptFramework entity) {
        return repository.save(entity);
    }

    @Override
    public void saveAll(Set<JavaScriptFramework> entities) {
        entities.forEach(repository::save);
    }

    @Override
    public void delete(JavaScriptFramework entity) {
        repository.delete(entity);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public Iterable<JavaScriptFramework> searchAllByName(String name) {
        return repository.findByNameContaining(name.trim());
    }
}
