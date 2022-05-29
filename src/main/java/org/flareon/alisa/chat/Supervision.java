package org.flareon.alisa.chat;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.flareon.alisa.Config;
import org.flareon.alisa.FLAlisa;
import org.flareon.alisa.cooldown.CooldownsHandler;
import org.flareon.alisa.utils.ColorUtil;

import java.util.HashMap;

import static java.lang.String.format;

public class Supervision {
    private final FLAlisa ALISA;
    private final Config config;
    private final CooldownsHandler cooldownsHandler;
    public HashMap<PunishmentType, String> punishments;

    public enum PunishmentType {
        MUTE,
        WARN,
        BAN;
    }

    public Supervision() {
        this.punishments = new HashMap<>();
        this.ALISA = FLAlisa.getInstance();
        this.config = this.ALISA.config;
        this.cooldownsHandler = this.ALISA.cooldownsHandler;
        this.createPunishments();
    }


    private void createPunishments() {
        this.punishments.put(PunishmentType.MUTE, config.getString("mute-command"));
        this.punishments.put(PunishmentType.WARN, config.getString("warn-command"));
    }

    private String getPunishmentCommand(final PunishmentType punishmentType) {
        return punishments.get(punishmentType);
    }


    private void mute(final String playerName, final int durationSeconds, final String reason) {
        new BukkitRunnable() {
            public void run() {
                ALISA.executeCommand(String.format(getPunishmentCommand(PunishmentType.MUTE), playerName, durationSeconds + " sec", reason));
                ++ALISA.statistics.mutes;
                ALISA.statistics.mutesDuration += durationSeconds;
            }
        }.runTaskLater(ALISA, 1L);
    }

    private void warn(final String playerName, final String reason) {
        new BukkitRunnable() {
            public void run() {
                ALISA.executeCommand(String.format(getPunishmentCommand(PunishmentType.WARN), playerName, reason));
                ++ALISA.statistics.warns;
            }
        }.runTaskLater(ALISA, 1L);
    }

    public void punish(final Player player, final int durationSeconds, final String reason, Answer.AnswerReason answerReason) {
        final String playerName = player.getName();
        if (cooldownsHandler.warnCooldowns.isExpired(playerName)) {
            warn(playerName, reason);
            answerReason = Answer.AnswerReason.WARN;
        } else {
            mute(playerName, durationSeconds, reason);
        }
        ALISA.say(format("%s, ", ColorUtil.wrap(playerName, ChatColor.GOLD)) + ALISA.answer.answers.get(answerReason).getRandomAnswer(playerName), player);
        cooldownsHandler.warnCooldowns.trigger(playerName);
    }

}
