package com.etnetera.hr.service;

import com.etnetera.hr.data.Version;
import com.etnetera.hr.repository.VersionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class VersionService implements IService<Version> {

    private final VersionRepository repository;

    @Autowired
    public VersionService(VersionRepository repository) {
        this.repository = repository;
    }

    @Override
    public Iterable<Version> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Version> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Boolean existsById(Long id) {
        return repository.existsById(id);
    }

    @Override
    public Version save(Version entity) {
        return repository.save(entity);
    }

    @Override
    public void saveAll(Set<Version> entities) {
        entities.forEach(repository::save);
    }

    @Override
    public void delete(Version entity) {
        repository.delete(entity);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
