package org.flareon.alisa.chat;

import org.bukkit.entity.Player;
import org.flareon.alisa.FLAlisa;
import org.flareon.alisa.processors.*;

import java.util.ArrayList;

public class MessageHandler {
    private final ArrayList<IProcessor> globalMessageProcessors;
    private final ArrayList<IProcessor> localMessageProcessors;
    public MessageHandler(final FLAlisa ALISA) {
        this.globalMessageProcessors = new ArrayList<IProcessor>() {{
            add(new ProcessorCaps(ALISA));
            add(new ProcessorFlood(ALISA));
            add(new ProcessorSymbolFlood(ALISA));
            add(new ProcessorProfanity(ALISA));
            add(new ProcessorHelloBye(ALISA));
        }};
        this.localMessageProcessors = new ArrayList<>();
    }

    public void handleMessage(final Player player, final String playerMessage) {
        final boolean isGlobal = this.isGlobalMessage(playerMessage);
        if (isGlobal) {
            String message = this.deColor(playerMessage);
            message = message.substring(1);
            for (final IProcessor processor : this.globalMessageProcessors) {
                if (processor.processMessage(player, message)) {
                    return;
                }
            }
        }
    }
    private boolean isGlobalMessage(final String message) {
        return message.indexOf("!") == 0;
    }

    private String deColor(final String msg) {
        return msg.replaceAll("§.", "");
    }

}
