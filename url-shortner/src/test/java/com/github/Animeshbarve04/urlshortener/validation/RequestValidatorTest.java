package com.github.Animeshbarve04.urlshortener.validation;

import com.github.Animeshbarve04.urlshortener.dto.request.ShortenUrlRequest;
import com.github.Animeshbarve04.urlshortener.exception.InvalidAliasException;
import com.github.Animeshbarve04.urlshortener.exception.InvalidUrlException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RequestValidatorTest {

    private final RequestValidator validator = new RequestValidator();

    @Test
    void acceptsWellFormedHttpAndHttpsUrls() {
        ShortenUrlRequest http = new ShortenUrlRequest();
        http.setUrl("http://example.com/path");
        ShortenUrlRequest https = new ShortenUrlRequest();
        https.setUrl("https://example.com/path");

        assertThatCode(() -> validator.validate(http)).doesNotThrowAnyException();
        assertThatCode(() -> validator.validate(https)).doesNotThrowAnyException();
    }

    @Test
    void rejectsUrlWithNoScheme() {
        ShortenUrlRequest request = new ShortenUrlRequest();
        request.setUrl("example.com/path");

        assertThatThrownBy(() -> validator.validate(request)).isInstanceOf(InvalidUrlException.class);
    }

    @Test
    void rejectsNonHttpScheme() {
        ShortenUrlRequest request = new ShortenUrlRequest();
        request.setUrl("ftp://example.com/path");

        assertThatThrownBy(() -> validator.validate(request)).isInstanceOf(InvalidUrlException.class);
    }

    @Test
    void rejectsSchemeWithNoHost() {
        // "http:foo" is a syntactically valid URI (scheme + opaque part) but
        // has no authority/host, so it's not a usable redirect target.
        ShortenUrlRequest request = new ShortenUrlRequest();
        request.setUrl("http:foo");

        assertThatThrownBy(() -> validator.validate(request)).isInstanceOf(InvalidUrlException.class);
    }

    @Test
    void acceptsAliasWithinCharsetAndLengthRange() {
        ShortenUrlRequest request = new ShortenUrlRequest();
        request.setUrl("https://example.com");
        request.setCustomAlias("my-alias_1");

        assertThatCode(() -> validator.validate(request)).doesNotThrowAnyException();
    }

    @Test
    void rejectsAliasWithDisallowedCharacters() {
        ShortenUrlRequest request = new ShortenUrlRequest();
        request.setUrl("https://example.com");
        request.setCustomAlias("bad alias!");

        assertThatThrownBy(() -> validator.validate(request)).isInstanceOf(InvalidAliasException.class);
    }

    @Test
    void rejectsAliasShorterThanThreeCharacters() {
        ShortenUrlRequest request = new ShortenUrlRequest();
        request.setUrl("https://example.com");
        request.setCustomAlias("ab");

        assertThatThrownBy(() -> validator.validate(request)).isInstanceOf(InvalidAliasException.class);
    }

    @Test
    void ignoresBlankAlias() {
        // A blank alias should be treated as "no alias", not an invalid one.
        ShortenUrlRequest request = new ShortenUrlRequest();
        request.setUrl("https://example.com");
        request.setCustomAlias("   ");

        assertThatCode(() -> validator.validate(request)).doesNotThrowAnyException();
    }
}
