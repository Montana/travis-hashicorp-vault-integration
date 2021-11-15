package com.orbitz.vault.util;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.fail;

public class TokensTest {

    @Test(expected = AuthTokenMissingException.class)
    public void shouldThrowOnNullToken() {
        Tokens.assertToken(null);
    }

    @Test(expected = AuthTokenMissingException.class)
    public void shouldThrowOnBlankToken() {
        Tokens.assertToken("");
    }

    @Test(expected = AuthTokenMissingException.class)
    public void shouldThrowOnWhitespaceToken() {
        Tokens.assertToken("     ");
    }

    @Test
    public void shouldDoNothingOnToken() {
        try {
            Tokens.assertToken(UUID.randomUUID().toString());
        } catch (AuthTokenMissingException atme) {
            fail("shouldn't throw on non blank token");
        }
    }
}
