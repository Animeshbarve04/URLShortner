package com.github.Animeshbarve04.urlshortener.exception;

public class InvalidAliasException extends RuntimeException {

    public InvalidAliasException(String alias) {
        super("Invalid custom alias: " + alias);
    }

}