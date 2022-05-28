package org.flareon.alisa.chat;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.flareon.alisa.FLAlisa;
import org.flareon.alisa.utils.ChatUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Advertisment {
    private final ArrayList<String> advertisments;
    private int step = 0;
    private final FLAlisa ALISA;
    private int TASK_ID = -1;

    public Advertisment() {
        ALISA = FLAlisa.getInstance();

        advertisments = ALISA.advertisments.getList("advertisments");
        Collections.shuffle(this.advertisments);

        if(TASK_ID == -1) {
            TASK_ID = Bukkit.getScheduler().scheduleSyncRepeatingTask(ALISA, this::sayAdvertisment, 0L, 20L * ALISA.config.getLong("cooldown.advertisment"));
        }
    }

    public void sayAdvertisment() {

        final String advertisment = advertisments.get(step).replaceAll("&", "§");

        if(advertisment.contains("LINK:::")) {
            final Pattern linkPattern = Pattern.compile("LINK:::.+:::LINK");
            final Pattern linkNamePattern = Pattern.compile("NAME:::.+:::NAME");
            final String link = findAndCut(linkPattern, advertisment, 7);
            final String linkName = findAndCut(linkNamePattern, advertisment, 7);

            final String text = advertisment.replaceAll("LINK:::.+:::LINK.*NAME:::.+:::NAME", "");
            ALISA.broadcast(new ComponentBuilder().append(ChatUtil.ALISA_TAG).append(ChatUtil.text(text)).append(ChatUtil.linkBuilder(link, linkName)).create());
        } else {
            ALISA.broadcast(advertisment);
        }

        ++step;
        if(step >= advertisments.size()) {
            step = 0;
        }
    }

    private String findAndCut(final Pattern pattern, final String str, final int limiter) {
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            return str.substring(matcher.start() + limiter,matcher.end() - limiter);
        }
        return str;
    }
}
