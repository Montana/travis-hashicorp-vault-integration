package com.orbitz.vault.util;

import retrofit2.Response;

import java.io.IOException;

public class VaultApiException extends RuntimeException {

    public VaultApiException(int code, Response<?> response) {
        super(String.format("Vault request failed with status [%s]: %s",
                code, message(response)));
    }

    public VaultApiException(Throwable throwable) {
        super("Vault request failed", throwable);
    }

    static String message(Response response) {
        try {
            return response.errorBody().string();
        } catch (IOException e) {
            return response.message();
        }
    }
}
