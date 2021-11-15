package com.orbitz.vault.sys.model;

import com.google.gson.annotations.SerializedName;

public class Status {

    private boolean sealed;
    @SerializedName("t")
    private int threshold;
    @SerializedName("n")
    private int numShares;
    private int progress;

    public boolean isSealed() {
        return sealed;
    }

    public void setSealed(boolean sealed) {
        this.sealed = sealed;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public int getNumShares() {
        return numShares;
    }

    public void setNumShares(int numShares) {
        this.numShares = numShares;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
