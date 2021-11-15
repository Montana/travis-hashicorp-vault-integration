package com.orbitz.vault.secret;

import com.orbitz.vault.secret.model.SecretResponse;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.Map;

import static com.orbitz.vault.util.Tokens.X_VAULT_TOKEN;

interface Secrets {

    @GET("/v1/{name}")
    @Headers({"Accept: application/json"})
    Call<SecretResponse> getSecret(@Path("name") String name,
                                   @Header(X_VAULT_TOKEN) String token);

    @POST("/v1/{name}")
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json"
    })
    Call<Void> writeSecret(@Path(value = "name", encoded = true) String name,
                           @Body Map<String, String> secret,
                           @Header(X_VAULT_TOKEN) String token);
}
