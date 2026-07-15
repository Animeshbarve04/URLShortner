package com.github.Animeshbarve04.urlshortener.service;

import com.github.Animeshbarve04.urlshortener.dto.request.ShortenUrlRequest;
import com.github.Animeshbarve04.urlshortener.dto.response.ShortenUrlResponse;

public interface UrlShortenerService {

    ShortenUrlResponse shortenUrl(ShortenUrlRequest request);

    String getOriginalUrl(String shortCode);

}