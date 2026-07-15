package com.github.Animeshbarve04.urlshortener.service.impl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
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
    private final String baseUrl;

    public UrlShortenerServiceImpl(
            UrlMappingRepository repository,
            IdGenerator idGenerator,
            ShortCodeGenerator shortCodeGenerator,
            RequestValidator requestValidator,
            @Value("${app.base-url}") String baseUrl) {

        this.repository = repository;
        this.idGenerator = idGenerator;
        this.shortCodeGenerator = shortCodeGenerator;
        this.requestValidator = requestValidator;
        this.baseUrl = baseUrl;
    }

    @Override
    public ShortenUrlResponse shortenUrl(ShortenUrlRequest request) {

        requestValidator.validate(request);

        Long id = idGenerator.nextId();

        String shortCode = hasCustomAlias(request)
                ? getValidatedCustomAlias(request)
                : shortCodeGenerator.generate(id);

        UrlMapping mapping = new UrlMapping();
        mapping.setId(id);
        mapping.setOriginalUrl(request.getUrl());
        mapping.setShortCode(shortCode);
        mapping.setCreatedAt(LocalDateTime.now());

        repository.save(mapping);

        return new ShortenUrlResponse(
                shortCode,
                baseUrl + "/" + shortCode
        );
    }

    @Override
    public String getOriginalUrl(String shortCode) {

        return repository.findByShortCode(shortCode)
                .orElseThrow(() ->
                        new ShortCodeNotFoundException(shortCode))
                .getOriginalUrl();
    }

    /**
     * Returns true if the request contains a non-blank custom alias.
     */
    private boolean hasCustomAlias(ShortenUrlRequest request) {

        return request.getCustomAlias() != null
                && !request.getCustomAlias().isBlank();
    }

    /**
     * Checks whether the requested alias is available and returns it.
     */
    private String getValidatedCustomAlias(ShortenUrlRequest request) {

        String alias = request.getCustomAlias();

        if (repository.existsByShortCode(alias)) {
            throw new AliasAlreadyExistsException(alias);
        }

        return alias;
    }
}