package com.github.Animeshbarve04.urlshortener.generator;

import org.springframework.stereotype.Component;

import com.github.Animeshbarve04.urlshortener.repository.UrlMappingRepository;

@Component
public final class DatabaseSequenceIdGenerator implements IdGenerator {

    private final UrlMappingRepository repository;

    public DatabaseSequenceIdGenerator(UrlMappingRepository repository) {
        this.repository = repository;
    }

    @Override
    public Long nextId() {
        return repository.getNextId();
    }
}