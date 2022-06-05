package org.flareon.alisa;

import org.bukkit.Bukkit;
import org.flareon.alisa.utils.TimeUtil;

import java.util.*;

public class PlaytimeHandler {
    private final FLAlisa ALISA = FLAlisa.getInstance();
    private final Config config;
    public HashMap<String, Long> playtimes;
    public HashMap<String, Long> globalPlaytimes;
    public HashMap<String, Long> firstPlaytimes = new HashMap<>();

    public PlaytimeHandler(Config config) {
        this.config = config;
        setDefaultConfig();

        playtimes = getMapPlaytimes("playtimes");
        globalPlaytimes = getMapPlaytimes("globalPlaytimes");

        // Start playtime for online mods
        ALISA.moderatorsHandler.getOnlineModsUUID().forEach(this::startPlaytime);
    }
    private HashMap<String, Long> getMapPlaytimes(final String path) {
        final HashMap<String, Long> hm = new HashMap<>();
        List<PlaytimeReport> list = (List<PlaytimeReport>) config.getObject(path);
        for (PlaytimeReport report : list) {
            hm.put(report.uuid.toString(), report.time);
        }
        return hm;
    }

    private List<PlaytimeReport> getListFromMap(final HashMap<String, Long> hm) {
        List<PlaytimeReport> list = new ArrayList<>();
        for (Map.Entry<String, Long> entry : hm.entrySet()) {
            list.add(new PlaytimeReport(entry.getValue(), UUID.fromString(entry.getKey())));
        }
        return list;
    }


    private void setDefaultConfig() {
        final long currentTime = System.currentTimeMillis();
        if (!config.exists("playtimes")) {
            config.setCustom("playtimes", new ArrayList<PlaytimeReport>());
        }
        if(!config.exists("globalPlaytimes")) {
            config.setCustom("globalPlaytimes", new ArrayList<PlaytimeReport>());
        }
        if(!config.exists("created") || new TimeUtil(currentTime - config.getLong("created")).getDays() > 7L) {
            config.setCustom("created", currentTime);
            final List<PlaytimeReport> onlineUUID = new ArrayList<>();
            ALISA.moderatorsHandler.getOnlineModsUUID().forEach(uuid -> onlineUUID.add(new PlaytimeReport(0L, uuid)));
            config.setCustom("playtimes", onlineUUID);
        }
    }

    public void startPlaytime(final UUID uuid) {
        firstPlaytimes.put(uuid.toString(), System.currentTimeMillis());
    }

    public void newPlaytime(final UUID uuid) {
        playtimes.put(uuid.toString(), 0L);
        if(!globalPlaytimes.containsKey(uuid.toString())) {
            globalPlaytimes.put(uuid.toString(), 0L);
        }
        startPlaytime(uuid);
    }

    public boolean hasPlaytime(final UUID uuid) {
        return playtimes.containsKey(uuid.toString()) && globalPlaytimes.containsKey(uuid.toString());
    }

    public long getPlaytime(final UUID uuid) {
        final String uuidString = uuid.toString();
        if (hasPlaytime(uuid)) {
            savePlayTime(uuid);
            return playtimes.get(uuidString);
        }
        return 0L;
    }

    public long getGlobalPlaytime(final UUID uuid) {
        final String uuidString = uuid.toString();
        if (hasPlaytime(uuid)) {
            savePlayTime(uuid);
            return globalPlaytimes.get(uuidString);
        }
        return 0L;
    }

    public void savePlayTime(final UUID uuid) {
        final String uuidString = uuid.toString();
        if (firstPlaytimes.containsKey(uuidString) && hasPlaytime(uuid)) {
            final long time = System.currentTimeMillis() - firstPlaytimes.get(uuidString);
            playtimes.put(uuidString, playtimes.get(uuidString) + time);
            globalPlaytimes.put(uuidString, globalPlaytimes.get(uuidString) + time);
            startPlaytime(uuid);
        }
    }

    private void saveAllPlayTimes() {
        ALISA.moderatorsHandler.getOnlineModsUUID().forEach(uuid -> {
            final String uuidString = uuid.toString();
            if (firstPlaytimes.containsKey(uuidString) && hasPlaytime(uuid)) {
                final long time = System.currentTimeMillis() - firstPlaytimes.get(uuidString);
                playtimes.put(uuidString, playtimes.get(uuidString) + time);
                globalPlaytimes.put(uuidString, globalPlaytimes.get(uuidString) + time);
            }
        });
    }

    public void save() {
        saveAllPlayTimes();

        config.setCustom("playtimes", getListFromMap(playtimes));
        config.setCustom("globalPlaytimes", getListFromMap(globalPlaytimes));
    }
}
