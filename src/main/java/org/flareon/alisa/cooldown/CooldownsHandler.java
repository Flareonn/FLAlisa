package org.shy.alisa.cooldown;

import org.shy.alisa.Config;
import org.shy.alisa.FLAlisa;

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
