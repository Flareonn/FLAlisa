package org.shy.alisa.utils;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class AlisaColor {
    private static String prefixColor;
    private static String nameColor;
    private static String textColor;
    private static String bracketColor;

    public AlisaColor(FileConfiguration fileConfiguration) {
        prefixColor = fileConfiguration.getString("chat-colors.prefix-color");
        nameColor = fileConfiguration.getString("chat-colors.name-color");
        textColor = fileConfiguration.getString("chat-colors.text-color");
        bracketColor = fileConfiguration.getString("chat-colors.bracket-color");
    }

    public String getTag() {
        String bracketsColorCode = translateColorCode(bracketColor);
        return String.format("%s[%sПомощница%s]%s Алиса:%s ", bracketsColorCode, translateColorCode(prefixColor), bracketsColorCode, translateColorCode(nameColor), translateColorCode(textColor));
    }

    private String translateColorCode(String color) {
        return ChatColor.translateAlternateColorCodes('&', "&" + color);
    }
}
