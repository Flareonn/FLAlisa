package org.flareon.alisa.utils;

import org.bukkit.ChatColor;
import org.flareon.alisa.Config;

public class ColorUtil {
    public static String prefix;
    public static String name;
    public static String text;
    public static String bracket;
    public static String success;
    public static String warning;

    private static String ALISA_TAG;

    public ColorUtil(final Config config) {
        registerColorConfig(config);
        ALISA_TAG = String.format("%s%s%s %s:%s ", ColorUtil.wrap("[", bracket), ColorUtil.wrap(config.getString("prefix"), prefix), ColorUtil.wrap("]", bracket), ColorUtil.wrap("Алиса", name), text);
    }

    private static void registerColorConfig(Config config) {
        prefix = translateColorCode(config.getString("chat-colors.prefix"));
        name = translateColorCode(config.getString("chat-colors.name"));
        text = translateColorCode(config.getString("chat-colors.text"));
        bracket = translateColorCode(config.getString("chat-colors.bracket"));
        warning = translateColorCode(config.getString("chat-colors.warning"));
        success = translateColorCode(config.getString("chat-colors.success"));
    }

    public static String getAlisaTag() {
        return ALISA_TAG;
    }

    private static String translateColorCode(String color) {
        return color.startsWith("§") ? color : ChatColor.translateAlternateColorCodes('&', "&" + color);
    }

    public static String success(String word) {
        return wrap(word, translateColorCode(success) + ChatColor.BOLD);
    }

    public static String fail(String word) {
        return wrap(word, translateColorCode(warning) + ChatColor.BOLD);
    }

    public static String wrap(String word, String color) {
        return translateColorCode(color) + word + ChatColor.RESET + text;
    }

    public static String wrap(String word, ChatColor color) {
        return color + word + ChatColor.RESET + text;
    }

    public static String wrap(String word, ChatColor color, ChatColor modifiers) {
        return color + "" + modifiers + word + ChatColor.RESET + text;
    }
}
