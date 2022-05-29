package org.flareon.alisa.processors;

import org.bukkit.entity.Player;
import org.flareon.alisa.FLAlisa;
import org.flareon.alisa.chat.Answer;

public class ProcessorSymbolFlood implements IProcessor {
    private final FLAlisa ALISA;
    private final int minimumMessageLengthForSymbolCheck;

    public ProcessorSymbolFlood(final FLAlisa ALISA) {
        this.minimumMessageLengthForSymbolCheck = 12;
        this.ALISA = ALISA;
    }

    @Override
    public boolean processMessage(final Player player, final String message) {
        if (message.length() >= this.minimumMessageLengthForSymbolCheck && this.getSymbolRatioOfMessage(message) > this.getAllowedSymbolRatio()) {
            this.ALISA.supervision.punish(player, this.getTempmuteDurationSymbolFlood(), "3.1 (флуд символами)", Answer.AnswerReason.FLOOD);
            return true;
        }
        return false;
    }

    private int getTempmuteDurationSymbolFlood() {
        return this.ALISA.config.getInt("tempmute.symbol-flood");
    }

    public float getSymbolRatioOfMessage(final String message) {
        if (message == null || message.trim().isEmpty()) {
            return 0.0f;
        }
        int count = 0;
        final String specialChars = "/*!@#$%^&*()\"{}_[]|\\?/<>,.№;:+=-'";
        for (int i = 0; i < message.length(); ++i) {
            if (specialChars.contains(message.substring(i, i + 1))) {
                ++count;
            }
        }
        return count / (float) message.length();
    }

    private float getAllowedSymbolRatio() {
        return this.ALISA.config.getFloat("symbol-ratio");
    }
}
