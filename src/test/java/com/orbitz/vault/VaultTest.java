package com.orbitz.vault;

import com.orbitz.vault.auth.LoginFailedException;
import com.orbitz.vault.secret.LogicalClient;
import com.orbitz.vault.sys.SysClient;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class VaultTest extends TestSupport {

    @Test
    public void shouldInit() throws LoginFailedException {
        Vault vault = vault();

        assertTrue(vault.sys(login()).init().isInitialized());
    }

    @Test
    public void shouldGetSecretClient() {
        LogicalClient logicalClient = vault().logical(login());

        assertNotNull(logicalClient);
    }

    @Test
    public void shouldGetSysClient() {
        SysClient sysClient = vault().sys(login());

        assertNotNull(sysClient);
    }
}
