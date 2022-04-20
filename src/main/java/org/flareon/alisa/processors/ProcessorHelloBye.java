package org.flareon.alisa.processors;

import org.bukkit.entity.*;
import org.flareon.alisa.FLAlisa;
import org.flareon.alisa.utils.FileUtil;

import java.util.regex.*;
import java.util.*;

public class ProcessorHelloBye implements IProcessor
{
    private final FLAlisa ALISA;
    private final Pattern toAll;
    private final ArrayList<String> helloSecondaryWords;
    private final ArrayList<String> byeSecondaryWords;

    @Override
    public boolean processMessage(final Player player, String message) {
        if (this.ALISA.cooldownsHandler.helloByeCooldown.isExpired()) {
            message = message.toLowerCase();
            final Matcher m = this.toAll.matcher(message);
            if (message.contains("всем") && !m.find()) {
                if (this.containsWord(message, this.helloSecondaryWords)) {
                    this.ALISA.sayHello(player);
                    this.ALISA.cooldownsHandler.helloByeCooldown.trigger();
                    return false;
                }
                if (this.containsWord(message, this.byeSecondaryWords)) {
                    this.ALISA.sayBye(player);
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

    public ProcessorHelloBye(final FLAlisa ALISA) {
        this.ALISA = ALISA;
        this.toAll = Pattern.compile("\\Sвсем");
        this.helloSecondaryWords = FileUtil.readProjectFileLines("hello-secondary-words.txt");
        this.byeSecondaryWords = FileUtil.readProjectFileLines("bye-secondary-words.txt");
    }
}

