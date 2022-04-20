package org.flareon.alisa.processors;

import org.bukkit.entity.Player;
import org.flareon.alisa.FLAlisa;
import org.flareon.alisa.utils.FileUtil;

import java.util.ArrayList;

public class ProcessorCaps implements IProcessor {
    private final FLAlisa ALISA;
    private final int minimumMessageLengthForCapsCheck;
    private final int minimumLettersForCapsCheck;
    private final ArrayList<String> ignoredWords;
    public ProcessorCaps(final FLAlisa ALISA) {
        this.minimumMessageLengthForCapsCheck = 6;
        this.minimumLettersForCapsCheck = 3;
        this.ALISA = ALISA;
        this.ignoredWords = FileUtil.readProjectFileLines("ignored-words-caps.txt");
    }
    @Override
    public boolean processMessage(Player player, String playerMessage) {
        String message = this.removeIgnoredWords(playerMessage);
        message = this.removePlayerNames(message);
        message = this.removeDigitsAndSpaces(message);
        if (message.length() > this.minimumMessageLengthForCapsCheck && this.getCapsRatioOfMessage(message) > this.getAllowedCapsRatio()) {
            this.ALISA.punish(player, this.getTempmuteDurationCaps(), "3.1 (капс)", FLAlisa.AnswerReason.CAPS);
            this.ALISA.debug("mute/caps: " + player.getName() + ": '" + playerMessage + "' (" + message + ")");
            return true;
        }
        return false;
    }

    private String removeIgnoredWords(String message) {
        for (final String word : this.ignoredWords) {
            message = message.replaceAll(word, "");
        }
        return message;
    }

    private String removeDigitsAndSpaces(String message) {
        message = message.replaceAll("\\d", "");
        message = message.replaceAll("\\s", "");
        return message;
    }

    private String removePlayerNames(String message) {
        for (final String playername : this.ALISA.knownPlayerNames) {
            message = message.replaceFirst(playername, "");
        }
        return message;
    }

    private int getTempmuteDurationCaps() {
        return this.ALISA.config.getInt("tempmute.caps");
    }

    private float getAllowedCapsRatio() {
        return this.ALISA.config.getFloat("caps-ratio");
    }

    private float getCapsRatioOfMessage(final String message) {
        int total = 0;
        int uppercase = 0;
        for (int i = 0; i < message.length(); ++i) {
            if (Character.isLetter(message.charAt(i))) {
                ++total;
                if (Character.isUpperCase(message.charAt(i))) {
                    ++uppercase;
                }
            }
        }
        if (total < this.minimumLettersForCapsCheck) {
            return 0.0f;
        }
        return uppercase / (float)total;
    }
}
