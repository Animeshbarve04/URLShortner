package com.github.Animeshbarve04.urlshortener.util;

public final class Base62Encoder {

    private static final char[] CHARSET =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
                    .toCharArray();

    private static final int BASE = CHARSET.length;

    private Base62Encoder() {
        throw new AssertionError("Utility class should not be instantiated.");
    }

    public static String encode(long value) {

        if (value < 0) {
            throw new IllegalArgumentException("Value cannot be negative.");
        }

        if (value == 0) {
            return String.valueOf(CHARSET[0]);
        }

        StringBuilder encoded = new StringBuilder();

        while (value > 0) {

            int remainder = (int) (value % BASE);

            encoded.append(CHARSET[remainder]);

            value /= BASE;
        }

        return encoded.reverse().toString();
    }
}