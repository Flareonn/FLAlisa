package org.shy.alisa.utils;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;

public class ColorUtil {
    public static String prefix;
    public static String name;
    public static String text;
    public static String bracket;

    private static String ALISA_TAG;

    public ColorUtil(FileConfiguration fileConfiguration) {
        registerColorConfig(fileConfiguration);
        ALISA_TAG = String.format("%s[%sПомощница%s]%s Алиса:%s ", bracket, prefix, bracket, name, text);
    }

    private static void registerColorConfig(FileConfiguration fileConfiguration) {
        prefix = translateColorCode(fileConfiguration.getString("chat-colors.prefix-color"));
        name = translateColorCode(fileConfiguration.getString("chat-colors.name-color"));
        text = translateColorCode(fileConfiguration.getString("chat-colors.text-color"));
        bracket = translateColorCode(fileConfiguration.getString("chat-colors.bracket-color"));
    }

    public static String getAlisaTag() {
        return ALISA_TAG;
    }

    private static String translateColorCode(String color) {
        return ChatColor.translateAlternateColorCodes('&', "&" + color);
    }

    public static String success(String word) {
        return wrap(word, ChatColor.GREEN, ChatColor.BOLD);
    }

    public static String fail(String word) {
        return wrap(word, ChatColor.RED, ChatColor.BOLD);
    }

    public static String wrap(String word, ChatColor color) {
        return color + word + ChatColor.RESET + text;
    }

    public static String wrap(String word, ChatColor color, ChatColor modifiers) {
        return color + "" + modifiers + word + ChatColor.RESET + text;
    }
}
