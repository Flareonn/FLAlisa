package org.flareon.alisa.cooldown;

import org.flareon.alisa.Config;

public class CooldownsHandler {
    public Cooldown votesunGlobalCooldown;
    public Cooldown votedayGlobalCooldown;
    public CooldownPlayerBased votesunPersonalCooldowns;
    public CooldownPlayerBased votedayPersonalCooldowns;

    public CooldownsHandler(Config config) {
        this.votesunGlobalCooldown = new Cooldown(config.getInt("votesun-global"));
        this.votedayGlobalCooldown = new Cooldown(config.getInt("voteday-global"));
        this.votesunPersonalCooldowns = new CooldownPlayerBased(config.getInt("votesun-personal"));
        this.votedayPersonalCooldowns = new CooldownPlayerBased(config.getInt("voteday-personal"));
    }
}
