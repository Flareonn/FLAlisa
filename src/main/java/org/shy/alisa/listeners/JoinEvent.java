package org.shy.alisa.listeners;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.shy.alisa.Main;

public class JoinEvent implements Listener {
    public static Server plugin;
//    @EventHandler
//    public void onPlayerJoin(PlayerJoinEvent event) {
//        Player p = event.getPlayer();
//        event.setJoinMessage(p.getDisplayName() + " " + Main.getInstance().getConfig().getString("messages.join"));
//    }
}
