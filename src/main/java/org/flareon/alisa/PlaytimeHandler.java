package org.flareon.alisa;

//import me.PCPSells.PlayTimeMain;

import org.flareon.alisa.utils.TimeUtil;

import java.util.HashMap;
import java.util.logging.Logger;

@Deprecated
public class PlaytimeHandler {
    private final Config config;
    private final Logger logger;
    private final FLAlisa ALISA;

    //    private final PlayTimeMain pluginPlayTime;
    public PlaytimeHandler(Config config) {
        this.config = config;
        this.ALISA = FLAlisa.getInstance();
        this.logger = ALISA.getLogger();
        if (needNewPlaytimeReport()) {
            pushNewPlaytimeReport();
            this.logger.info("A new Playtime log has been generated");
        }
//        this.pluginPlayTime = (PlayTimeMain) Bukkit.getPluginManager().getPlugin("PlayTime");
    }

//    public long getPlayTime(final UUID uuid) {
//        return this.pluginPlayTime.playerJoinTime.get(uuid);
//    }

    private void pushNewPlaytimeReport() {
        final PlaytimeReport oldReport = (PlaytimeReport) this.config.getObject("report2");
        this.config.set("report1", oldReport);
        this.config.set("report2", this.newPlaytimeReport());
    }

    private PlaytimeReport newPlaytimeReport() {
        final HashMap<String, Long> entries = new HashMap<>();
        for (final ModeratorsEntry me : this.ALISA.moderatorsHandler.groups) {
            for (final String playerName : me.playerNames) {
//                final long tempPlaytime = getPlayTime(Bukkit.getPlayer(playerName).getUniqueId());
//                entries.put(playerName, tempPlaytime);
            }
        }
        return new PlaytimeReport(System.currentTimeMillis(), entries);
    }

    private boolean needNewPlaytimeReport() {
        final PlaytimeReport oldReport = (PlaytimeReport) this.config.getObject("report2");
        if (TimeUtil.getDayOfWeek() != 2) {
            // Не тот день для обновления PlayTime логов, не создаём
            this.logger.info("Not the day to update the PlayTime logs, we do not create");
            return false;
        }
        if (oldReport == null) {
            // Старый лог не найден - создаём
            this.logger.info("The old log was not found - we create");
            return true;
        }
        if (System.currentTimeMillis() - oldReport.time > 172800000L) {
            // Логи устарели, пересоздаём
            this.logger.info("The logs are outdated, we will recreate them");
            return true;
        }
        // Последний раз логи были созданы 2 дня назад, не создаём.
        this.logger.info("The logs were last created 2 days ago, we are not creating them.");
        return false;
    }
}
