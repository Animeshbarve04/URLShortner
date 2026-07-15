package com.github.Animeshbarve04.urlshortener.validation;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.github.Animeshbarve04.urlshortener.dto.request.ShortenUrlRequest;
import com.github.Animeshbarve04.urlshortener.exception.InvalidAliasException;
import com.github.Animeshbarve04.urlshortener.exception.InvalidUrlException;

@Component
public class RequestValidator {

    private static final Pattern ALIAS_PATTERN =
            Pattern.compile("^[A-Za-z0-9_-]{3,30}$");

    public void validate(ShortenUrlRequest request) {

        validateUrl(request.getUrl());

        if (request.getCustomAlias() != null
                && !request.getCustomAlias().isBlank()) {

            validateAlias(request.getCustomAlias());
        }
    }

    private void validateUrl(String url) {

        try {

            URI uri = new URI(url);

            String scheme = uri.getScheme();

            if (scheme == null ||
                    !(scheme.equalsIgnoreCase("http")
                            || scheme.equalsIgnoreCase("https"))) {

                throw new InvalidUrlException(url);
            }

        } catch (URISyntaxException e) {

            throw new InvalidUrlException(url);

        }

    }

    private void validateAlias(String alias) {

        if (!ALIAS_PATTERN.matcher(alias).matches()) {

            throw new InvalidAliasException(alias);

        }

    }

}