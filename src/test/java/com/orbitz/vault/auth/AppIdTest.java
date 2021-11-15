package com.orbitz.vault.auth;

import com.orbitz.vault.TestSupport;
import com.orbitz.vault.Vault;
import com.orbitz.vault.auth.model.LoginResponse;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class AppIdTest extends TestSupport {

    @Test
    public void shouldLoginViaAppId() {
        Vault vault = vault();
        try {
            LoginResponse response = vault.authClients().appIdClient().login("foo", "bar");

            assertNotNull(response.getAuth().getClientToken());
        } catch (LoginFailedException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
