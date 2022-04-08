package org.shy.alisa;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Set;

public class Config {
    private final FLAlisa ALISA;
    private final FileConfiguration config;

    public Config(final FLAlisa ALISA) {
        this.ALISA = ALISA;
        this.config = ALISA.getConfig();
        System.out.println("CONFIG INIT");
    }

    public String getString(final String key) {
        return this.config.getString(key);
    }

    public float getFloat(final String key) {
        return (float)this.config.getDouble(key);
    }

    public int getInt(final String key) {
        return this.config.getInt(key);
    }

    public boolean getBoolean(final String key) {
        return this.config.getBoolean(key, false);
    }

    public long getLong(final String key) {return this.config.getLong(key);}

    public ArrayList<String> getList(final String key) {
        return (ArrayList<String>)this.config.getStringList(key);
    }

    public Set<String> getKeys() {
        return this.config.getKeys(false);
    }

    public void set(final String key, final Object value) {
        this.config.set(key, value);
        this.ALISA.saveConfig();
    }

    public Object getObject(final String key) {
        return this.config.get(key);
    }

    public boolean exists(final String key) {
        return this.config.contains(key);
    }
}
