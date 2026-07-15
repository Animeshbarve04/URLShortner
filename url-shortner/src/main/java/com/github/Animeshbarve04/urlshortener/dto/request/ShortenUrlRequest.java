package com.github.Animeshbarve04.urlshortener.dto.request;

import jakarta.validation.constraints.NotBlank;

public class ShortenUrlRequest {

    @NotBlank(message = "URL is required")
    private String url;

    private String customAlias;

    public ShortenUrlRequest() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCustomAlias() {
        return customAlias;
    }

    public void setCustomAlias(String customAlias) {
        this.customAlias = customAlias;
    }
}