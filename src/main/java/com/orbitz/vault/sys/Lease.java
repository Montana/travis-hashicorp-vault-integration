package com.orbitz.vault.sys;

import com.orbitz.vault.sys.model.RenewResponse;
import com.orbitz.vault.sys.model.Renewal;
import retrofit2.Call;
import retrofit2.http.*;

import static com.orbitz.vault.util.Tokens.X_VAULT_TOKEN;

interface Lease {

    @PUT("/v1/sys/renew/{leaseId}")
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json"
    })
    Call<RenewResponse> renewLease(@Path(value = "leaseId", encoded = true) String leaseId,
                                   @Header(X_VAULT_TOKEN) String token);

    @PUT("/v1/sys/renew/{leaseId}")
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json"
    })
    Call<RenewResponse> renewLease(@Path("leaseId") String leaseId,
                                   @Body Renewal renewal,
                                   @Header(X_VAULT_TOKEN) String token);
}
