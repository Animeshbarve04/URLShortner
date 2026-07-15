package com.github.Animeshbarve04.urlshortener.exception;

public class ShortCodeNotFoundException extends RuntimeException {

    public ShortCodeNotFoundException(String shortCode) {
        super("Short code not found: " + shortCode);
    }

}