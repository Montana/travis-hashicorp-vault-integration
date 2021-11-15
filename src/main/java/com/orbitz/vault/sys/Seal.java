package com.orbitz.vault.sys;

import com.orbitz.vault.sys.model.Status;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PUT;
import retrofit2.http.Query;

import static com.orbitz.vault.util.Tokens.X_VAULT_TOKEN;

interface Seal {

    @GET("/v1/sys/seal-status")
    @Headers({"Accept: application/json"})
    Call<Status> getSealStatus(@Header(X_VAULT_TOKEN) String token);

    @PUT("/v1/sys/seal")
    @Headers({"Accept: application/json"})
    Call<Void> seal(@Header(X_VAULT_TOKEN) String token);

    @PUT("/v1/sys/unseal")
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json"
    })
    Call<Status> unseal(@Query("key") String key,
                        @Header(X_VAULT_TOKEN) String token);

    @PUT("/v1/sys/unseal")
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json"
    })
    Call<Status> unseal(@Query("reset") boolean reset,
                        @Header(X_VAULT_TOKEN) String token);
}
