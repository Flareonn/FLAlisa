package org.flareon.alisa;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@SerializableAs("PlaytimeReport")
public class PlaytimeReport implements ConfigurationSerializable {
    public long time;
    public UUID uuid;

    public PlaytimeReport(final long time, final UUID uuid) {
        this.time = time;
        this.uuid = uuid;
    }

    public static PlaytimeReport deserialize(final Map<String, Object> args) {
        long time = -1L;
        UUID uuid = null;
        if (args.containsKey("time")) {
            time = Long.parseLong(args.get("time").toString());
        }
        if (args.containsKey("uuid")) {
            uuid = UUID.fromString(String.valueOf(args.get("uuid")));
        }
        return new PlaytimeReport(time, uuid);
    }

    @Override
    public Map<String, Object> serialize() {
        final LinkedHashMap map = new LinkedHashMap();
        map.put("time", this.time);
        map.put("uuid", this.uuid.toString());
        return map;
    }
}
