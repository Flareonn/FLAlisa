package org.flareon.alisa.utils;

import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.ChatColor;
import org.flareon.alisa.Config;

import java.util.HashMap;

public class ChatUtil {
    private static final TextComponent cleaner = text("");
    public static TextComponent ALISA_TAG;
    public static TextComponent YES;
    public static TextComponent NO;
    public static BaseComponent[] YES_NO;

    public static final HashMap<String, String> hexColor = new HashMap<String, String>() {{
        put("0", "#000000"); put("1", "#0000AA"); put("2", "#00AA00"); put("3", "#00AAAA");
        put("4", "#AA0000"); put("5", "#AA00AA"); put("6", "#FFAA00"); put("7", "#AAAAAA");
        put("8", "#3F3F3F"); put("9", "#5555FF"); put("a", "#55FF55"); put("b", "#55FFFF");
        put("c", "#FF5555"); put("d", "#FF55FF"); put("e", "#FFFF55"); put("f", "#FFFFFF");
        put("g", "#DDD605");
    }};
    private static ChatColor prefixColor;
    private static ChatColor nameColor;
    private static ChatColor textColor;
    private static ChatColor bracketColor;

    public ChatUtil(final Config config) {
        registerColorConfig(config);

        ALISA_TAG = text("[", bracketColor);
        ALISA_TAG.addExtra(text("Помощница", prefixColor));
        ALISA_TAG.addExtra(text("]", bracketColor));
        ALISA_TAG.addExtra(text(String.format(" %s: ", config.getString("name")), nameColor));

        YES = textCommand(text("'Да'", ChatColor.GREEN), "/yes");
        YES.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Проголосовать 'За'").color(YES.getColor()).create()));
        NO = textCommand(text("'Нет'", ChatColor.RED), "/no");
        NO.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Проголосовать 'Против'").color(NO.getColor()).create()));

        YES_NO = new ComponentBuilder(YES).append(" ").append(NO).create();
    }

    private static void registerColorConfig(Config config) {
        prefixColor = toHex(config.getString("chat-colors.prefix"));
        nameColor = toHex(config.getString("chat-colors.name"));
        textColor = toHex(config.getString("chat-colors.text"));
        bracketColor = toHex(config.getString("chat-colors.bracket"));
    }

    public static ChatColor toHex(String legacyColorCode) {
        return ChatColor.of(hexColor.get(legacyColorCode));
    }

    public static TextComponent text(String text) {
        final TextComponent textComponent = new TextComponent(text);
        textComponent.setColor(textColor);
        textComponent.setBold(false);
        return textComponent;
    }
    public static TextComponent text(String text, ChatColor chatColor) {
        final TextComponent textComponent = new TextComponent(text);
        textComponent.setColor(chatColor);
        return textComponent;
    }
    public static TextComponent textCommand(TextComponent textComponent, String command) {
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        textComponent.addExtra(cleaner);
        return textComponent;
    }

    public static TextComponent textLink(TextComponent textComponent, String link) {
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Открыть ссылку").color(ChatColor.BLUE).create()));
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, link));
        textComponent.addExtra(cleaner);
        return textComponent;
    }

    public static TextComponent linkBuilder(final String link, final String name) {
        return ChatUtil.textLink(ChatUtil.text("[" + name + "]", ChatColor.GOLD), link);
    }
}
