package com.orbitz.vault.auth;

import com.orbitz.vault.auth.model.LoginResponse;
import com.orbitz.vault.auth.model.UserId;
import com.orbitz.vault.util.VaultApiException;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

import static com.orbitz.vault.util.Http.extract;

public class AppIdClient {

    private Api api;

    AppIdClient(Retrofit retrofit) {
        api = retrofit.create(Api.class);
    }

    public LoginResponse login(String appId, String userId) throws LoginFailedException {
        try {
            return extract(api.login(appId, new UserId(userId)));
        } catch (VaultApiException ex) {
            throw new LoginFailedException(ex);
        }
    }
    private interface Api {

        @POST("/v1/auth/app-id/login/{appId}")
        @Headers({
                "Content-Type: application/json",
                "Accept: application/json"
        })
        Call<LoginResponse> login(@Path("appId") String appId,
                                  @Body UserId userId);
    }
}
