package com.orbitz.vault.secret;

import com.orbitz.vault.secret.model.SecretResponse;
import retrofit2.Retrofit;

import java.util.Map;

import static com.orbitz.vault.util.Http.extract;
import static com.orbitz.vault.util.Http.handle;
import static com.orbitz.vault.util.Tokens.assertToken;

public class LogicalClient {

    private Secrets secrets;
    private String token;

    public LogicalClient(Retrofit retrofit, String token) {
        assertToken(token);

        secrets = retrofit.create(Secrets.class);

        this.token = token;
    }

    public SecretResponse getSecret(String name) {
        return extract(secrets.getSecret(name, token));
    }

    public void writeSecret(String name, Map<String, String> secret) {
        handle(secrets.writeSecret(name, secret, token));
    }
}
