package org.flareon.alisa.processors;

import org.bukkit.entity.Player;
import org.flareon.alisa.FLAlisa;
import org.flareon.alisa.chat.Answer;
import org.flareon.alisa.utils.FileUtil;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProcessorProfanity implements IProcessor {
    protected FLAlisa ALISA;
    ArrayList<Pattern> badWords;

    public ProcessorProfanity(final FLAlisa ALISA) {
        this.ALISA = ALISA;
        final ArrayList<String> temp = FileUtil.readProjectFileLines("profanity.txt");
        this.badWords = new ArrayList<>(temp.size());
        for (final String word : temp) {
            final Pattern pat = Pattern.compile(word.replace("|", "\\b"));
            this.badWords.add(pat);
        }
    }

    @Override
    public boolean processMessage(final Player player, final String playerMesage) {
        final String message = playerMesage.toLowerCase();
        for (final Pattern p : this.badWords) {
            final Matcher matcher = p.matcher(message);
            if (matcher.find()) {
                this.ALISA.supervision.punish(player, this.getTempmuteDurationProfanity(), "3.2 (нецензурная брань)", Answer.AnswerReason.PROFANITY);
                this.ALISA.debug("mute/profanity: " + player.getName() + ": '" + message + "' (" + playerMesage + ")");
                return true;
            }
        }
        return false;
    }

    private int getTempmuteDurationProfanity() {
        return this.ALISA.config.getInt("tempmute.profanity");
    }
}
