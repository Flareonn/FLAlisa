package org.flareon.alisa.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.flareon.alisa.FLAlisa;

import java.util.ArrayList;

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
        if(toSpawnPlayerNames.contains(playerName)) {
            joinedPlayer.teleport(joinedPlayer.getWorld().getSpawnLocation().add(0.0, 0.5, 0.0));
            toSpawnPlayerNames.remove(playerName);
            this.ALISA.config.set("tospawn-playernames", toSpawnPlayerNames);
            this.ALISA.say("Вы были отправлены на спавн", joinedPlayer);
        }
    }
}
