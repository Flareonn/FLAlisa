package org.flareon.alisa.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.flareon.alisa.FLAlisa;

public class ChatEvent implements Listener {
    private final FLAlisa ALISA;

    public ChatEvent() {
        this.ALISA = FLAlisa.getInstance();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (!event.isCancelled()) {
            this.ALISA.messageHandler.handleMessage(event.getPlayer(), event.getMessage());
        }
    }

}