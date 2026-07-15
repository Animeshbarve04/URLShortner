package com.github.Animeshbarve04.urlshortener.util;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Base62EncoderTest {

    @Test
    void encodesZeroAsFirstCharsetCharacter() {
        assertEquals("0", Base62Encoder.encode(0));
    }

    @Test
    void encodesSmallValuesDirectlyFromCharset() {
        assertEquals("1", Base62Encoder.encode(1));
        assertEquals("9", Base62Encoder.encode(9));
    }

    @Test
    void rollsOverToTwoCharactersAtBase() {
        // BASE (62) is the first value needing a second digit.
        String rollover = Base62Encoder.encode(62);
        assertEquals(2, rollover.length());
    }

    @Test
    void rejectsNegativeValues() {
        assertThrows(IllegalArgumentException.class, () -> Base62Encoder.encode(-1));
    }

    @Test
    void producesUrlSafeCharactersOnly() {
        for (long i = 0; i < 100_000; i += 137) {
            String code = Base62Encoder.encode(i);
            assertTrue(code.matches("[0-9A-Za-z]+"), "Non URL-safe code: " + code);
        }
    }

    @Test
    void everyDistinctIdProducesADistinctCode() {
        // This is the real collision guarantee behind the design: since the
        // encoding is a pure positional-numeral conversion (no hashing, no
        // truncation), distinct ids can never map to the same code. As long
        // as the DB sequence handing out ids never repeats one, the codes
        // it produces can't collide either.
        Set<String> seen = new HashSet<>();
        for (long i = 0; i < 200_000; i++) {
            String code = Base62Encoder.encode(i);
            assertTrue(seen.add(code), "Collision detected for id " + i + " -> " + code);
        }
        assertEquals(200_000, seen.size());
    }

    @Test
    void largeValuesStayWithinExpectedLength() {
        String code = Base62Encoder.encode(Long.MAX_VALUE);
        assertTrue(code.length() <= 11, "Unexpectedly long code: " + code);
    }
}
