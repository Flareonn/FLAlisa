package org.flareon.alisa.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatEvent implements Listener {
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
//        if (event.getMessage().equals("/ahelp")) {
//            Bukkit.getServer().dispatchCommand(event.getPlayer(), "/help alisa");
//            event.getPlayer().sendMessage("HELLO");
//        }
    }

//    public void aliases(AsyncPlayerChatEvent event) {
//        if (event.getMessage().equals("/ahelp")) {
//            Bukkit.getServer().dispatchCommand(event.getPlayer(), "/help alisa");
//            event.getPlayer().sendMessage("HELLO");
//        }
//    }


}
