package com.orbitz.vault.auth.model;

import java.util.List;
import java.util.Map;

public class LoginResponse {

    private String leaseId;
    private Long leaseDuration;
    private String data;
    private Auth auth;

    public String getLeaseId() {
        return leaseId;
    }

    public void setLeaseId(String leaseId) {
        this.leaseId = leaseId;
    }

    public Long getLeaseDuration() {
        return leaseDuration;
    }

    public void setLeaseDuration(Long leaseDuration) {
        this.leaseDuration = leaseDuration;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Auth getAuth() {
        return auth;
    }

    public void setAuth(Auth auth) {
        this.auth = auth;
    }

    public static class Auth {
        private String clientToken;
        private List<String> policies;
        private Map<String, String> metadata;
        private Long leaseDuration;
        private Boolean renewable;

        public String getClientToken() {
            return clientToken;
        }

        public void setClientToken(String clientToken) {
            this.clientToken = clientToken;
        }

        public List<String> getPolicies() {
            return policies;
        }

        public void setPolicies(List<String> policies) {
            this.policies = policies;
        }

        public Map<String, String> getMetadata() {
            return metadata;
        }

        public void setMetadata(Map<String, String> metadata) {
            this.metadata = metadata;
        }

        public Long getLeaseDuration() {
            return leaseDuration;
        }

        public void setLeaseDuration(Long leaseDuration) {
            this.leaseDuration = leaseDuration;
        }

        public Boolean getRenewable() {
            return renewable;
        }

        public void setRenewable(Boolean renewable) {
            this.renewable = renewable;
        }
    }
}
