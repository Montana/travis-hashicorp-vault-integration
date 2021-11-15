package com.orbitz.vault.util;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class TimeTest {

    @Test
    public void shouldFormatValidUnits() {
        assertEquals("10s", Time.timeString(10L, TimeUnit.SECONDS));
        assertEquals("10ms", Time.timeString(10L, TimeUnit.MILLISECONDS));
        assertEquals("10h", Time.timeString(10L, TimeUnit.HOURS));
        assertEquals("10m", Time.timeString(10L, TimeUnit.MINUTES));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOnInvalidUnits() {
        Time.timeString(10L, TimeUnit.MICROSECONDS);
    }
}
