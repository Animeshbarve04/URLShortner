package com.github.Animeshbarve04.urlshortener.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.Animeshbarve04.urlshortener.dto.request.ShortenUrlRequest;
import com.github.Animeshbarve04.urlshortener.dto.response.ShortenUrlResponse;
import com.github.Animeshbarve04.urlshortener.exception.AliasAlreadyExistsException;
import com.github.Animeshbarve04.urlshortener.exception.InvalidUrlException;
import com.github.Animeshbarve04.urlshortener.exception.ShortCodeNotFoundException;
import com.github.Animeshbarve04.urlshortener.service.UrlShortenerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {UrlApiController.class, RedirectController.class})
class UrlControllersTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private UrlShortenerService urlShortenerService;

    @Test
    void shorten_returns201WithShortCodeAndShortUrl_onSuccess() throws Exception {
        ShortenUrlRequest request = new ShortenUrlRequest();
        request.setUrl("https://example.com/some/long/path");

        when(urlShortenerService.shortenUrl(any())).thenReturn(
                new ShortenUrlResponse("cb", "http://localhost:8080/cb"));

        mockMvc.perform(post("/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortCode").value("cb"))
                .andExpect(jsonPath("$.shortUrl").value("http://localhost:8080/cb"));
    }

    @Test
    void shorten_returns400_whenUrlIsBlank() throws Exception {
        ShortenUrlRequest request = new ShortenUrlRequest();
        request.setUrl("");

        mockMvc.perform(post("/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shorten_returns400_whenUrlIsInvalid() throws Exception {
        ShortenUrlRequest request = new ShortenUrlRequest();
        request.setUrl("not-a-url");

        when(urlShortenerService.shortenUrl(any())).thenThrow(new InvalidUrlException("not-a-url"));

        mockMvc.perform(post("/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shorten_returns409_whenAliasAlreadyExists() throws Exception {
        ShortenUrlRequest request = new ShortenUrlRequest();
        request.setUrl("https://example.com");
        request.setCustomAlias("taken");

        when(urlShortenerService.shortenUrl(any())).thenThrow(new AliasAlreadyExistsException("taken"));

        mockMvc.perform(post("/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void redirect_returns301WithLocationHeader_whenCodeExists() throws Exception {
        when(urlShortenerService.getOriginalUrl("abc123")).thenReturn("https://example.com/target");

        mockMvc.perform(get("/abc123"))
                .andExpect(status().isMovedPermanently())
                .andExpect(header().string("Location", "https://example.com/target"));
    }

    @Test
    void redirect_returns404_whenCodeIsUnknown() throws Exception {
        when(urlShortenerService.getOriginalUrl("missing")).thenThrow(new ShortCodeNotFoundException("missing"));

        mockMvc.perform(get("/missing"))
                .andExpect(status().isNotFound());
    }
}
