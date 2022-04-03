package org.shy.alisa.utils;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class ColorUtil {
    private static String prefixColor;
    private static String nameColor;
    private static String textColor;
    private static String bracketColor;
    private static String ALISA_TAG;


    public ColorUtil(FileConfiguration fileConfiguration) {
        prefixColor = translateColorCode(fileConfiguration.getString("chat-colors.prefix-color"));
        nameColor = translateColorCode(fileConfiguration.getString("chat-colors.name-color"));
        textColor = translateColorCode(fileConfiguration.getString("chat-colors.text-color"));
        bracketColor = translateColorCode(fileConfiguration.getString("chat-colors.bracket-color"));
        ALISA_TAG = String.format("%s[%sПомощница%s]%s Алиса:%s ", bracketColor, prefixColor, bracketColor, nameColor, textColor);
    }

    public static String getAlisaTag() {
        return ALISA_TAG;
    }

    private static String translateColorCode(String color) {
        return ChatColor.translateAlternateColorCodes('&', "&" + color);
    }

    public static String getBracketColor() {
        return bracketColor;
    }

    public static String getNameColor() {
        return nameColor;
    }

    public static String getPrefixColor() {
        return prefixColor;
    }

    public static String getTextColor() {
        return textColor;
    }

    public static String success(String word) {
        return wrap(word, ChatColor.GREEN, ChatColor.BOLD);
    }

    public static String fail(String word) {
        return wrap(word, ChatColor.RED, ChatColor.BOLD);
    }

    public static String wrap(String word, ChatColor color) {
        return color + word + ChatColor.RESET + getTextColor();
    }

    public static String wrap(String word, ChatColor color, ChatColor modifiers) {
        return color + "" + modifiers + word + ChatColor.RESET + getTextColor();
    }
}
