package org.flareon.alisa.processors;

import org.bukkit.entity.Player;
import org.flareon.alisa.FLAlisa;
import org.flareon.alisa.chat.Answer.AnswerReason;
import org.flareon.alisa.utils.FileUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class ProcessorFlood implements IProcessor {
    protected FLAlisa ALISA;
    private final ArrayList<String> tradeFilters;
    private final HashMap<String, MessageData> messages;
    private final int flood_any_trade_messages_trigger;
    private final int flood_identical_trade_messages_trigger;
    private final int flood_timeout;
    private final int flood_normal_messages_trigger;
    private final int flood_any_trade_messages_period;

    public ProcessorFlood(final FLAlisa ALISA) {
        this.messages = new HashMap<>();
        this.flood_any_trade_messages_trigger = 3;
        this.flood_identical_trade_messages_trigger = 2;
        this.flood_timeout = 45;
        this.flood_normal_messages_trigger = 3;
        this.flood_any_trade_messages_period = 180;
        this.ALISA = ALISA;
        this.tradeFilters = FileUtil.readProjectFileLines("trade-filters.txt");
    }

    @Override
    public boolean processMessage(Player player, String playerMessage) {
        final String playerName = player.getName();
        if (!this.messages.containsKey(playerName)) {
            this.messages.put(playerName, new MessageData(this.flood_any_trade_messages_trigger));
        }
        final boolean isTrade = this.isTradeMessage(playerMessage);
        final MessageData md = this.messages.get(playerName);
        if (md.previousMessage.isEmpty() || !md.previousMessage.equalsIgnoreCase(playerMessage)
                || System.currentTimeMillis() - md.previousMessageTime > this.flood_timeout * 1000L) {
            md.subsequentMessagesCount = 1;
        } else {
            ++md.subsequentMessagesCount;
        }

        md.previousMessage = playerMessage;
        md.previousMessageTime = System.currentTimeMillis();

        if (isTrade) {
            md.tradeMessageTimers.remove();
            md.tradeMessageTimers.add(System.currentTimeMillis());
        }
        if (isTrade && md.subsequentMessagesCount >= this.flood_identical_trade_messages_trigger) {
            this.ALISA.supervision.punish(player, this.getTempmuteDurationAdvertisement(), "3.1 (Частая реклама)", AnswerReason.ADVERTISEMENT);
            return true;
        }
        if (isTrade && md.tradeMessageTimers.get(0) != 0L && System.currentTimeMillis() - md.tradeMessageTimers.get(0) <= this.flood_any_trade_messages_period * 1000L) {
            this.ALISA.supervision.punish(player, this.getTempmuteDurationAdvertisement(), "3.1 (Частая реклама+)", AnswerReason.ADVERTISEMENT);
            return true;
        }
        if (md.subsequentMessagesCount >= this.flood_normal_messages_trigger) {
            this.ALISA.supervision.punish(player, this.getTempmuteDurationFlood(), "3.1 (Флуд)", AnswerReason.FLOOD);
            return true;
        }
        return false;
    }

    private int getTempmuteDurationFlood() {
        return this.ALISA.config.getInt("tempmute.flood");
    }

    private int getTempmuteDurationAdvertisement() {
        return this.ALISA.config.getInt("tempmute.advertisement");
    }

    private boolean isTradeMessage(String message) {
        message = message.toLowerCase();
        if (message.length() > 12) {
            for (final String word : this.tradeFilters) {
                if (message.contains(word)) {
                    return true;
                }
            }
        }
        return false;
    }

    static class MessageData {
        String previousMessage;
        int subsequentMessagesCount;
        long previousMessageTime;
        LinkedList<Long> tradeMessageTimers;

        MessageData(final int anyMessagesTrigger) {
            this.previousMessage = "";
            this.subsequentMessagesCount = 1;
            this.previousMessageTime = 0L;
            this.tradeMessageTimers = new LinkedList<>();
            for (int i = 0; i < anyMessagesTrigger; ++i) {
                this.tradeMessageTimers.add(0L);
            }
        }
    }
}
