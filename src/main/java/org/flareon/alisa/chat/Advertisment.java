package org.flareon.alisa.chat;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.flareon.alisa.FLAlisa;
import org.flareon.alisa.utils.ChatUtil;

import java.util.ArrayList;
import java.util.Collections;

public class Advertisment {
    private final ArrayList<BaseComponent[]> advertisments = new ArrayList<>();
    private int step = 0;
    private final FLAlisa ALISA;
    private int TASK_ID = -1;

    public Advertisment() {
        ALISA = FLAlisa.getInstance();
        ArrayList<String> tags = new ArrayList<String>() {{
            add("button");
            add("link");
            add("");
        }};


        for (String adv : ALISA.advertisments.getList("advertisments")) {
            adv = adv.replaceAll("&", "§");
            for (String tag : tags) {
                if (ChatUtil.hasPattern(adv, tag)) {
                    advertisments.add(ChatUtil.buildWithTags(adv, tag));
                    break;
                }
            }
        }

        Collections.shuffle(this.advertisments);


        if (TASK_ID == -1) {
            TASK_ID = Bukkit.getScheduler().scheduleSyncRepeatingTask(ALISA, this::sayAdvertisment, 0L, 20L * ALISA.config.getLong("cooldown.advertisment"));
        }
    }

    public void sayAdvertisment() {
        ALISA.broadcast(advertisments.get(step));
        ++step;
        if (step >= advertisments.size()) {
            step = 0;
        }
    }

}
