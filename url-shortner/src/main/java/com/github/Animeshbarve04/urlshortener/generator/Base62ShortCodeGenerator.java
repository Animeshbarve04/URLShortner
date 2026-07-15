package com.github.Animeshbarve04.urlshortener.generator;

import org.springframework.stereotype.Component;

import com.github.Animeshbarve04.urlshortener.util.Base62Encoder;

@Component
public class Base62ShortCodeGenerator implements ShortCodeGenerator {

    @Override
    public String generate(long id) {

        return Base62Encoder.encode(id);

    }

}