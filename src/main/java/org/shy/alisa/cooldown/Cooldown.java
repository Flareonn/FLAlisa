package org.shy.alisa;

public class Cooldown {
    private long lastTriggered;
    private long duration;

    protected Cooldown(final long durationSeconds) {
        this.duration = durationSeconds * 1000L;
        this.lastTriggered = 0L;
    }

    protected boolean isExpired() {
        return System.currentTimeMillis() - this.lastTriggered >= this.duration;
    }

    protected void trigger() {
        this.lastTriggered = System.currentTimeMillis();
    }

    protected long getSecondsLeft() {
        return (this.lastTriggered + this.duration - System.currentTimeMillis()) / 1000L;
    }
}

class CooldownsHandler
{
    protected FLAlisa ALISA;
    protected Cooldown helloByeCooldown;
    protected Cooldown votesunGlobalCooldown;
    protected Cooldown votedayGlobalCooldown;
//    protected CooldownIndexBased questionsCooldowns;
//    protected CooldownPlayerBased warnCooldowns;
//    protected CooldownPlayerBased votesunPersonalCooldowns;
//    protected CooldownPlayerBased votedayPersonalCooldowns;

    protected CooldownsHandler() {
        this.ALISA = FLAlisa.getInstance();
//        this.helloByeCooldown = new Cooldown(ALISA.config.getInt("cooldown.hello"));
//        this.questionsCooldowns = new CooldownIndexBased(ALISA.config.getInt("cooldown.answers"));
//        this.warnCooldowns = new CooldownPlayerBased(ALISA.config.getInt("cooldown.warn"));
        this.votesunGlobalCooldown = new Cooldown(ALISA.config.getInt("votesun-global"));
        this.votedayGlobalCooldown = new Cooldown(ALISA.config.getInt("voteday-global"));
//        this.votesunPersonalCooldowns = new CooldownPlayerBased(ALISA.config.getInt("cooldown.votesun-personal"));
//        this.votedayPersonalCooldowns = new CooldownPlayerBased(ALISA.config.getInt("cooldown.voteday-personal"));
    }
}