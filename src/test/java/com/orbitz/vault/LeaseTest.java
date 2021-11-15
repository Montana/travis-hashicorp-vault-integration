package com.orbitz.vault;

import com.orbitz.vault.secret.model.SecretResponse;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class LeaseTest extends TestSupport {

    @Test
    @Ignore
    public void shouldRenewMySqlLease() {
        Vault vault = vault();
        String token = login();

        SecretResponse response =
                vault.logical(token).getSecret("mysql/creds/readonly");

        String leaseId = response.getLeaseId();

        assertEquals(leaseId, vault.sys(token).renewLease(leaseId, 1L, TimeUnit.HOURS).getLeaseId());
    }
}
