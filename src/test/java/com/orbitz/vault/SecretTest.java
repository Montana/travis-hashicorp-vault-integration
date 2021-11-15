package com.orbitz.vault;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class SecretTest extends TestSupport {

    @Test
    public void shouldWriteAndGetSecret() {
        Vault vault = vault();
        String secret = UUID.randomUUID().toString();
        String token = login();

        vault.logical(token).writeSecret("secret/whatever", ImmutableMap.of("value", secret));

        assertEquals(secret, vault.logical(token).getSecret("secret/whatever").getData().get("value"));
    }
}
