package com.orbitz.vault.sys;

import com.orbitz.vault.sys.model.InitResponse;
import com.orbitz.vault.sys.model.RenewResponse;
import com.orbitz.vault.sys.model.Renewal;
import com.orbitz.vault.sys.model.Status;
import retrofit2.Retrofit;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.orbitz.vault.util.Http.extract;
import static com.orbitz.vault.util.Http.handle;
import static com.orbitz.vault.util.Tokens.assertToken;

public class SysClient {

    private Initialization initialization;
    private Seal seal;
    private Lease lease;
    private String token;

    public SysClient(Retrofit retrofit, String token) {
        assertToken(token);

        initialization = retrofit.create(Initialization.class);
        seal = retrofit.create(Seal.class);
        lease = retrofit.create(Lease.class);

        this.token = token;
    }

    public InitResponse init() {
        try {
            return initialization.init(token).execute().body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Status getSealStatus() {
        return extract(seal.getSealStatus(token));
    }

    public void seal() {
        handle(seal.seal(token));
    }

    public Status unseal(String key) {
        return extract(seal.unseal(key, token));
    }

    public Status unseal(boolean reset) {
        return extract(seal.unseal(reset, token));
    }

    public RenewResponse renewLease(String leaseId) {
        return extract(lease.renewLease(leaseId, token));
    }

    public RenewResponse renewLease(String leaseId, Long increment, TimeUnit timeUnit) {
        return extract(lease.renewLease(leaseId, Renewal.forIncrement(increment, timeUnit), token));
    }
}
