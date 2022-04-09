package org.flareon.alisa.cooldown;

import java.util.HashMap;

public class CooldownPlayerBased {
    private final HashMap<String, Cooldown> cooldowns;
    private final long duration;

    public CooldownPlayerBased(final int separateDurationSeconds) {
        this.cooldowns = new HashMap<>();
        this.duration = separateDurationSeconds * 1000L;
    }

    public boolean isExpired(final String playerName) {
        return !this.cooldowns.containsKey(playerName) || this.cooldowns.get(playerName).isExpired();
    }

    public void trigger(final String playerName) {
        if (!this.cooldowns.containsKey(playerName)) {
            this.cooldowns.put(playerName, new Cooldown(this.duration / 1000L));
        }
        this.cooldowns.get(playerName).trigger();
    }

    public long getSecondsLeft(final String playerName) {
        if (this.cooldowns.containsKey(playerName)) {
            return this.cooldowns.get(playerName).getSecondsLeft();
        }
        return 0L;
    }
}