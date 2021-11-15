package com.orbitz.vault.util;

import java.util.concurrent.TimeUnit;

public class Time {

    public static String timeString(Long time, TimeUnit timeUnit) {

        String unit;

        switch (timeUnit) {
            case MILLISECONDS:
                unit = "ms";
                break;
            case SECONDS:
                unit = "s";
                break;
            case MINUTES:
                unit = "m";
                break;
            case HOURS:
                unit = "h";
                break;
            default:
                throw new IllegalArgumentException("Invalid time unit: " + timeUnit.toString());

        }

        return String.format("%s%s", time, unit);
    }
}
