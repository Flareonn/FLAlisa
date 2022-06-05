package org.flareon.alisa.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.flareon.alisa.FLAlisa;

import java.util.ArrayList;
import java.util.UUID;

public class JoinEvent implements Listener {
    private final FLAlisa ALISA;

    public JoinEvent() {
        this.ALISA = FLAlisa.getInstance();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player joinedPlayer = event.getPlayer();
        final String playerName = joinedPlayer.getName();
        ArrayList<String> toSpawnPlayerNames = this.ALISA.config.getList("tospawn-playernames");
        this.ALISA.addKnownPlayer(playerName);
        if (toSpawnPlayerNames.contains(playerName)) {
            joinedPlayer.teleport(joinedPlayer.getWorld().getSpawnLocation().add(0.0, 0.5, 0.0));
            toSpawnPlayerNames.remove(playerName);
            this.ALISA.config.set("tospawn-playernames", toSpawnPlayerNames);
            this.ALISA.say("Вы были отправлены на спавн", joinedPlayer);
        }

        if (ALISA.moderatorsHandler.isModerator(playerName)) {
            final UUID uuid = joinedPlayer.getUniqueId();
            if (ALISA.playtimeHandler.hasPlaytime(uuid)) {
                ALISA.playtimeHandler.startPlaytime(uuid);
            } else {
                ALISA.playtimeHandler.newPlaytime(uuid);
            }
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        final Player p = event.getPlayer();
        final String playerName = p.getName();
        final UUID uuid = p.getUniqueId();

        if (ALISA.moderatorsHandler.isModerator(playerName)) {
            ALISA.playtimeHandler.savePlayTime(uuid);
        }
    }
}
