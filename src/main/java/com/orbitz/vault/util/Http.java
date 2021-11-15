package com.orbitz.vault.util;

import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

public class Http {

    public static <T> T extract(Call<T> call) {
        Response<T> response;
        try {
            response = call.execute();
        } catch (IOException e) {
            throw new VaultApiException(e);
        }

        if(response.isSuccessful()) {
            return response.body();
        } else {
            throw new VaultApiException(response.code(), response);
        }
    }

    public static void handle(Call<Void> call) {
        Response<Void> response;
        try {
            response = call.execute();
        } catch (IOException e) {
            throw new VaultApiException(e);
        }

        if(!response.isSuccessful()) {
            throw new VaultApiException(response.code(), response);
        }
    }
}
