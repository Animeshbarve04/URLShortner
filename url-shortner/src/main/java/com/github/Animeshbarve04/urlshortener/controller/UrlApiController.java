package com.github.Animeshbarve04.urlshortener.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.github.Animeshbarve04.urlshortener.dto.request.ShortenUrlRequest;
import com.github.Animeshbarve04.urlshortener.dto.response.ShortenUrlResponse;
import com.github.Animeshbarve04.urlshortener.service.UrlShortenerService;

import jakarta.validation.Valid;

@RestController
@Validated
public class UrlApiController {

    private final UrlShortenerService urlShortenerService;

    public UrlApiController(UrlShortenerService urlShortenerService) {
        this.urlShortenerService = urlShortenerService;
    }

    @PostMapping("/shorten")
    public ResponseEntity<ShortenUrlResponse> shortenUrl(
            @Valid @RequestBody ShortenUrlRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(urlShortenerService.shortenUrl(request));
    }
}