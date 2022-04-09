package org.flareon.alisa.cooldown;

public class Cooldown {
    private long lastTriggered;
    private long duration;

    protected Cooldown(final long durationSeconds) {
        this.duration = durationSeconds * 1000L;
        this.lastTriggered = 0L;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - this.lastTriggered >= this.duration;
    }

    public void trigger() {
        this.lastTriggered = System.currentTimeMillis();
    }

    public long getSecondsLeft() {
        return (this.lastTriggered + this.duration - System.currentTimeMillis()) / 1000L;
    }
}

