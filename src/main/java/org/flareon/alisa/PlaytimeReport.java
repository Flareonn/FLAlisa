package org.flareon.alisa;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Deprecated
public class PlaytimeReport implements ConfigurationSerializable {
    protected long time;
    protected HashMap<String, Long> entries;

    public PlaytimeReport(final long time, final HashMap<String, Long> entries) {
        this.time = time;
        this.entries = entries;
    }

    @Override
    public Map<String, Object> serialize() {
        final LinkedHashMap map = new LinkedHashMap();
        map.put("time", this.time);
        map.put("entries", this.entries);
        return map;
    }

    public static PlaytimeReport deserialize(final Map<String, Object> args) {
        long time = -1L;
        HashMap<String, Long> entries = new HashMap<>();
        if (args.containsKey("time")) {
            time = (long) args.get("time");
        }
        if (args.containsKey("entries")) {
            entries = (HashMap<String, Long>) args.get("entries");
        }
        return new PlaytimeReport(time, entries);
    }
}
