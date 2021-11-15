package com.orbitz.vault.auth;

import com.orbitz.vault.util.VaultApiException;

public class LoginFailedException extends Exception {

    public LoginFailedException(VaultApiException exception) {
        super("Login failed", exception);
    }
}
