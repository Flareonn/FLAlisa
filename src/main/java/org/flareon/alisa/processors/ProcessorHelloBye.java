package org.flareon.alisa.processors;

import org.bukkit.entity.Player;
import org.flareon.alisa.FLAlisa;
import org.flareon.alisa.utils.FileUtil;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProcessorHelloBye implements IProcessor {
    private final FLAlisa ALISA;
    private final Pattern toAll;
    private final ArrayList<String> helloSecondaryWords;
    private final ArrayList<String> byeSecondaryWords;

    public ProcessorHelloBye(final FLAlisa ALISA) {
        this.ALISA = ALISA;
        this.toAll = Pattern.compile("\\Sвсем");
        this.helloSecondaryWords = FileUtil.readProjectFileLines("hello-secondary-words.txt");
        this.byeSecondaryWords = FileUtil.readProjectFileLines("bye-secondary-words.txt");
    }

    @Override
    public boolean processMessage(final Player player, String message) {
        if (this.ALISA.cooldownsHandler.helloByeCooldown.isExpired()) {
            message = message.toLowerCase();
            final Matcher m = this.toAll.matcher(message);
            if (message.contains("всем") && !m.find()) {
                if (this.containsWord(message, this.helloSecondaryWords)) {
                    this.ALISA.answer.sayHello(player);
                    this.ALISA.cooldownsHandler.helloByeCooldown.trigger();
                    return false;
                }
                if (this.containsWord(message, this.byeSecondaryWords)) {
                    this.ALISA.answer.sayBye(player);
                    this.ALISA.cooldownsHandler.helloByeCooldown.trigger();
                    return false;
                }
            }
        }
        return false;
    }

    private boolean containsWord(final String message, final ArrayList<String> words) {
        for (final String s : words) {
            if (message.contains(s)) {
                return true;
            }
        }
        return false;
    }
}

