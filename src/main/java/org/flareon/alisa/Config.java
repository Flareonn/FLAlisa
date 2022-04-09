package org.shy.alisa;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Config {
    private FLAlisa ALISA;
    private FileConfiguration config;
    private File file;
    protected static File rulesFile;

    public Config(final FLAlisa ALISA) {
        this.ALISA = ALISA;
        this.config = ALISA.getConfig();
        rulesFile = createFile("rules.json");
    }

    public Config(final String fileName) {
        this.ALISA = FLAlisa.getInstance();
        this.config = createConfigFile(fileName);
    }

    private FileConfiguration createConfigFile(String fileName) {
        FileConfiguration fileConfiguration;
        File file = new File(this.ALISA.getDataFolder(), fileName);
        this.file = file;
        if(!file.exists()) {
            this.ALISA.saveResource(fileName, true);
        }
        fileConfiguration = new YamlConfiguration();
        try {
            fileConfiguration.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        return fileConfiguration;
    }

    private File createFile(String filename) {
        File file = new File(ALISA.getDataFolder(), filename);
        if(!file.exists()) {
            ALISA.saveResource(filename, true);
        }
        return file;
    }

    public File getRulesFile() {return rulesFile;}

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

    public void setCustom(final String key, final Object value) {
        this.config.set(key, value);
        try {
            this.config.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Object getObject(final String key) {
        return this.config.get(key);
    }

    public boolean exists(final String key) {
        return this.config.contains(key);
    }

    public List<?> getList(String path, List<?> def) {
        return this.config.getList(path, def);
    }
}
