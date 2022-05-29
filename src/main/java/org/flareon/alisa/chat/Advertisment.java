package org.flareon.alisa.chat;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.flareon.alisa.FLAlisa;
import org.flareon.alisa.utils.ChatUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Advertisment {
    private final ArrayList<BaseComponent[]> advertisments = new ArrayList<>();
    private int step = 0;
    private final FLAlisa ALISA;
    private int TASK_ID = -1;

    public Advertisment() {
        ALISA = FLAlisa.getInstance();

        for (String adv : ALISA.advertisments.getList("advertisments")) {
            adv = adv.replaceAll("&", "§");
            if(ChatUtil.hasPattern(adv, "LINK")) {
                advertisments.add(ChatUtil.tagFinder(adv, "link,name"));
            } else if (ChatUtil.hasPattern(adv, "BUTTON")) {
                advertisments.add(ChatUtil.tagFinder(adv, "button,name"));
            } else {
                advertisments.add(new ComponentBuilder().append(ChatUtil.ALISA_TAG).append(ChatUtil.text(adv)).create());
            }
        }
        Collections.shuffle(this.advertisments);


        if(TASK_ID == -1) {
            TASK_ID = Bukkit.getScheduler().scheduleSyncRepeatingTask(ALISA, this::sayAdvertisment, 0L, 20L * ALISA.config.getLong("cooldown.advertisment"));
        }
    }

    public void sayAdvertisment() {
        ALISA.broadcast(advertisments.get(step));
        ++step;
        if(step >= advertisments.size()) {
            step = 0;
        }
    }

    private String findAndCut(final Pattern pattern, final String str, final int limiter) {
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            return str.substring(matcher.start() + limiter,matcher.end() - limiter - 1);
        }
        return str;
    }

    private String getByTags(final String patterns, final String str) {
        Pattern pattern = Pattern.compile(patternBuilder(patterns), Pattern.CASE_INSENSITIVE);
        return findAndCut(pattern, str, 6);
    }
    private String patternBuilder(final String pattern) {
        final StringBuilder sb = new StringBuilder();
        for (String s : pattern.split(",")) {
            sb.append("<").append(s).append(">(.+?)</").append(s).append(">");
        }
        return sb.toString();
    }

}
