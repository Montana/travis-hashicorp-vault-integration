package com.orbitz.vault.sys.model;

public class Mount {

    private String type;
    private String description;
    private Config config;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public static class Config {

        private Long defaultLeaseTtl;
        private Long maxLeaseTtl;

        public Long getDefaultLeaseTtl() {
            return defaultLeaseTtl;
        }

        public void setDefaultLeaseTtl(Long defaultLeaseTtl) {
            this.defaultLeaseTtl = defaultLeaseTtl;
        }

        public Long getMaxLeaseTtl() {
            return maxLeaseTtl;
        }

        public void setMaxLeaseTtl(Long maxLeaseTtl) {
            this.maxLeaseTtl = maxLeaseTtl;
        }
    }
}
