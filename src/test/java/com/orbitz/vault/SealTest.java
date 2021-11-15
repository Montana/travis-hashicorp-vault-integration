package com.orbitz.vault;

import com.orbitz.vault.sys.SysClient;
import com.orbitz.vault.sys.model.Status;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class SealTest extends TestSupport {

    @Test
    public void shouldGetSealStatus() {
        Vault vault = vault();
        SysClient sysClient = vault.sys(login());
        Status status = sysClient.getSealStatus();

        assertFalse(status.isSealed());
    }
}
