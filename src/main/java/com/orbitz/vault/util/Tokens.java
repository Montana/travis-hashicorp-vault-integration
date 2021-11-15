package com.orbitz.vault.util;

public class Tokens {

    public static final String X_VAULT_TOKEN = "X-Vault-Token";

    public static void assertToken(String token) {

        if(token == null || token.trim().isEmpty()) {
            throw new AuthTokenMissingException();
        }
    }
}
