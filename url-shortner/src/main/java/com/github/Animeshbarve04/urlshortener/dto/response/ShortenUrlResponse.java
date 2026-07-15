package com.github.Animeshbarve04.urlshortener.dto.response;

public class ShortenUrlResponse {

    private String shortCode;
    private String shortUrl;

    public ShortenUrlResponse() {
    }

    public ShortenUrlResponse(String shortCode, String shortUrl) {
        this.shortCode = shortCode;
        this.shortUrl = shortUrl;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }
}