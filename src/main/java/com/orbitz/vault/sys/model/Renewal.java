package com.orbitz.vault.sys.model;

import com.orbitz.vault.util.Time;

import java.util.concurrent.TimeUnit;

public class Renewal {

    private String increment;

    public Renewal() {
    }

    private Renewal(String increment) {
        this.increment = increment;
    }

    public static Renewal forIncrement(long time, TimeUnit timeUnit) {
        return new Renewal(Time.timeString(time, timeUnit));
    }

    public String getIncrement() {
        return increment;
    }

    public void setIncrement(String increment) {
        this.increment = increment;
    }
}
