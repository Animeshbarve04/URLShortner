package com.github.Animeshbarve04.urlshortener.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.github.Animeshbarve04.urlshortener.dto.request.ShortenUrlRequest;
import com.github.Animeshbarve04.urlshortener.dto.response.ShortenUrlResponse;
import com.github.Animeshbarve04.urlshortener.entity.UrlMapping;
import com.github.Animeshbarve04.urlshortener.exception.AliasAlreadyExistsException;
import com.github.Animeshbarve04.urlshortener.exception.ShortCodeNotFoundException;
import com.github.Animeshbarve04.urlshortener.generator.IdGenerator;
import com.github.Animeshbarve04.urlshortener.generator.ShortCodeGenerator;
import com.github.Animeshbarve04.urlshortener.repository.UrlMappingRepository;
import com.github.Animeshbarve04.urlshortener.service.UrlShortenerService;
import com.github.Animeshbarve04.urlshortener.validation.RequestValidator;

@Service
public class UrlShortenerServiceImpl implements UrlShortenerService {

    private final UrlMappingRepository repository;
    private final IdGenerator idGenerator;
    private final ShortCodeGenerator shortCodeGenerator;
    private final RequestValidator requestValidator;

    public UrlShortenerServiceImpl(
            UrlMappingRepository repository,
            IdGenerator idGenerator,
            ShortCodeGenerator shortCodeGenerator,
            RequestValidator requestValidator) {

        this.repository = repository;
        this.idGenerator = idGenerator;
        this.shortCodeGenerator = shortCodeGenerator;
        this.requestValidator = requestValidator;
    }

    @Override
    public ShortenUrlResponse shortenUrl(ShortenUrlRequest request) {

        requestValidator.validate(request);

        Long id = idGenerator.nextId();

        String shortCode;

        if (request.getCustomAlias() != null &&
                !request.getCustomAlias().isBlank()) {

            if (repository.existsByShortCode(request.getCustomAlias())) {
                throw new AliasAlreadyExistsException(request.getCustomAlias());
            }

            shortCode = request.getCustomAlias();

        } else {

            shortCode = shortCodeGenerator.generate(id);

        }

        UrlMapping mapping = new UrlMapping();

        mapping.setId(id);
        mapping.setOriginalUrl(request.getUrl());
        mapping.setShortCode(shortCode);
        mapping.setCreatedAt(LocalDateTime.now());

        repository.save(mapping);

        return new ShortenUrlResponse(
                shortCode,
                "http://localhost:8080/" + shortCode
        );
    }

    @Override
    public String getOriginalUrl(String shortCode) {

        return repository.findByShortCode(shortCode)
                .orElseThrow(() ->
                        new ShortCodeNotFoundException(shortCode))
                .getOriginalUrl();
    }

}