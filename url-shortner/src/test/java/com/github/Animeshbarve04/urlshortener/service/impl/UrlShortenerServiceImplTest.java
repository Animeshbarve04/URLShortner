package com.github.Animeshbarve04.urlshortener.service.impl;

import com.github.Animeshbarve04.urlshortener.dto.request.ShortenUrlRequest;
import com.github.Animeshbarve04.urlshortener.dto.response.ShortenUrlResponse;
import com.github.Animeshbarve04.urlshortener.entity.UrlMapping;
import com.github.Animeshbarve04.urlshortener.exception.AliasAlreadyExistsException;
import com.github.Animeshbarve04.urlshortener.exception.ShortCodeNotFoundException;
import com.github.Animeshbarve04.urlshortener.generator.IdGenerator;
import com.github.Animeshbarve04.urlshortener.generator.ShortCodeGenerator;
import com.github.Animeshbarve04.urlshortener.repository.UrlMappingRepository;
import com.github.Animeshbarve04.urlshortener.validation.RequestValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlShortenerServiceImplTest {

    private static final String BASE_URL = "http://localhost:8080";

    @Mock
    private UrlMappingRepository repository;
    @Mock
    private IdGenerator idGenerator;
    @Mock
    private ShortCodeGenerator shortCodeGenerator;
    @Mock
    private RequestValidator requestValidator;

    @InjectMocks
    private UrlShortenerServiceImpl service;

    @BeforeEach
    void wireBaseUrl() {
        // @InjectMocks can't populate the @Value-injected constructor arg,
        // so we build the service directly with the same collaborators.
        service = new UrlShortenerServiceImpl(repository, idGenerator, shortCodeGenerator, requestValidator, BASE_URL);
    }

    @Test
    void shortenUrl_generatesBase62Code_whenNoAliasProvided() {
        ShortenUrlRequest request = new ShortenUrlRequest();
        request.setUrl("https://example.com/very/long/path");

        doNothing().when(requestValidator).validate(request);
        when(idGenerator.nextId()).thenReturn(125L);
        when(shortCodeGenerator.generate(125L)).thenReturn("cb");

        ShortenUrlResponse response = service.shortenUrl(request);

        assertThat(response.getShortCode()).isEqualTo("cb");
        assertThat(response.getShortUrl()).isEqualTo(BASE_URL + "/cb");
        verify(repository, times(1)).save(any(UrlMapping.class));
        // Custom alias path should never be touched when no alias is given.
        verify(repository, times(0)).existsByShortCode(anyString());
    }

    @Test
    void shortenUrl_usesCustomAlias_whenAvailable() {
        ShortenUrlRequest request = new ShortenUrlRequest();
        request.setUrl("https://example.com/page");
        request.setCustomAlias("my-alias");

        doNothing().when(requestValidator).validate(request);
        when(idGenerator.nextId()).thenReturn(7L);
        when(repository.existsByShortCode("my-alias")).thenReturn(false);

        ShortenUrlResponse response = service.shortenUrl(request);

        assertThat(response.getShortCode()).isEqualTo("my-alias");
        assertThat(response.getShortUrl()).isEqualTo(BASE_URL + "/my-alias");
        // The generated-code path should be bypassed entirely for aliases.
        verify(shortCodeGenerator, times(0)).generate(any(Long.class));
        verify(repository, times(1)).save(any(UrlMapping.class));
    }

    @Test
    void shortenUrl_throwsConflict_whenAliasAlreadyTaken() {
        ShortenUrlRequest request = new ShortenUrlRequest();
        request.setUrl("https://example.com/page");
        request.setCustomAlias("taken");

        doNothing().when(requestValidator).validate(request);
        when(idGenerator.nextId()).thenReturn(9L);
        when(repository.existsByShortCode("taken")).thenReturn(true);

        assertThatThrownBy(() -> service.shortenUrl(request))
                .isInstanceOf(AliasAlreadyExistsException.class);

        // No row should be written when the alias is rejected.
        verify(repository, times(0)).save(any(UrlMapping.class));
    }

    @Test
    void shortenUrl_createsANewRow_evenForARepeatedUrl() {
        // Documents the deliberate duplicate-URL policy: every call creates
        // an independently trackable link, rather than deduping by URL.
        ShortenUrlRequest first = new ShortenUrlRequest();
        first.setUrl("https://example.com/same-page");
        ShortenUrlRequest second = new ShortenUrlRequest();
        second.setUrl("https://example.com/same-page");

        doNothing().when(requestValidator).validate(any());
        when(idGenerator.nextId()).thenReturn(1L, 2L);
        when(shortCodeGenerator.generate(1L)).thenReturn("a");
        when(shortCodeGenerator.generate(2L)).thenReturn("b");

        ShortenUrlResponse firstResponse = service.shortenUrl(first);
        ShortenUrlResponse secondResponse = service.shortenUrl(second);

        assertThat(firstResponse.getShortCode()).isNotEqualTo(secondResponse.getShortCode());
        verify(repository, times(2)).save(any(UrlMapping.class));
    }

    @Test
    void getOriginalUrl_returnsUrl_whenCodeExists() {
        UrlMapping mapping = new UrlMapping("https://example.com", "abc123", null);
        when(repository.findByShortCode("abc123")).thenReturn(Optional.of(mapping));

        String result = service.getOriginalUrl("abc123");

        assertThat(result).isEqualTo("https://example.com");
    }

    @Test
    void getOriginalUrl_throwsNotFound_whenCodeIsUnknown() {
        when(repository.findByShortCode("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getOriginalUrl("missing"))
                .isInstanceOf(ShortCodeNotFoundException.class);
    }
}
