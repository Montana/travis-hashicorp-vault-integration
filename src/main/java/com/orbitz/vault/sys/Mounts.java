package com.orbitz.vault.sys;

import com.orbitz.vault.sys.model.Mount;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.Map;

import static com.orbitz.vault.util.Tokens.X_VAULT_TOKEN;

interface Mounts {

    @GET("/v1/sys/mounts")
    @Headers({"Accept: application/json"})
    Call<Map<String, Mount>> getMounts(@Header(X_VAULT_TOKEN) String token);

    @GET("/v1/sys/mounts/{mountPoint}/tune")
    @Headers({"Accept: application/json"})
    Call<Mount.Config> getConfig(@Path("mountPoint") String mountPoint,
                                 @Header(X_VAULT_TOKEN) String token);

    @POST("/v1/sys/mounts/{mountPoint}")
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json"
    })
    Call<Void> mount(@Path("mountPoint") String mountPoint,
                     @Body Mount mount,
                     @Header(X_VAULT_TOKEN) String token);

    @POST("/v1/sys/mounts/{mountPoint}/tune")
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json"
    })
    Call<Void> tune(@Path("mountPoint") String mountPoint,
                    @Body Mount.Config config,
                    @Header(X_VAULT_TOKEN) String token);
}
