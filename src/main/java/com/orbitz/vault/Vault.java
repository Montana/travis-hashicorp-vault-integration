package com.orbitz.vault;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.orbitz.vault.auth.AuthClients;
import com.orbitz.vault.secret.LogicalClient;
import com.orbitz.vault.sys.SysClient;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.net.ssl.SSLContext;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Vault
{
    private Retrofit retrofit;

    private ConcurrentMap<String, SysClient> sysClients =
            new ConcurrentHashMap<>();
    private ConcurrentMap<String, LogicalClient> logicalClients =
            new ConcurrentHashMap<>();

    private Vault(String host, int port, SSLContext sslContext) throws MalformedURLException {
        String protocol = sslContext == null ? "http" : "https";
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

        if(sslContext != null) {
            clientBuilder.sslSocketFactory(sslContext.getSocketFactory());
        }

        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory
                        .create(new GsonBuilder()
                                .setFieldNamingPolicy(FieldNamingPolicy
                                        .LOWER_CASE_WITH_UNDERSCORES).create()))
                .baseUrl(new URL(protocol, host, port, "").toExternalForm())
                .build();
    }

    public SysClient sys(String token) {
        if (!sysClients.containsKey(token)) {
            sysClients.put(token, new SysClient(retrofit, token));
        }

        return sysClients.get(token);
    }

    public LogicalClient logical(String token) {
        if (!logicalClients.containsKey(token)) {
            logicalClients.put(token, new LogicalClient(retrofit, token));
        }

        return logicalClients.get(token);
    }

    public AuthClients authClients() {
        return new AuthClients(retrofit);
    }

    public static class Builder {

        private String host = "localhost";
        private int port = 8200;
        private SSLContext sslContext;

        public static Builder builder() {
            return new Builder();
        }

        private Builder() {

        }

        public Builder host(String host) {
            this.host = host;

            return this;
        }

        public Builder port(int port) {
            this.port = port;

            return this;
        }

        public Builder sslContext(SSLContext sslContext) {
            this.sslContext = sslContext;

            return this;
        }

        public Vault build() {
            try {
                return new Vault(host, port, sslContext);
            } catch (MalformedURLException e) {
                throw new RuntimeException("Bad Vault hostname");
            }
        }
    }
}
