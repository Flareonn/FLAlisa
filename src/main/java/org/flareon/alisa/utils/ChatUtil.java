package org.flareon.alisa.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.flareon.alisa.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ChatUtil {
    public static final HashMap<String, String> hexColor = new HashMap<String, String>() {{
        put("0", "#000000");
        put("1", "#0000AA");
        put("2", "#00AA00");
        put("3", "#00AAAA");
        put("4", "#AA0000");
        put("5", "#AA00AA");
        put("6", "#FFAA00");
        put("7", "#AAAAAA");
        put("8", "#3F3F3F");
        put("9", "#5555FF");
        put("a", "#55FF55");
        put("b", "#55FFFF");
        put("c", "#FF5555");
        put("d", "#FF55FF");
        put("e", "#FFFF55");
        put("f", "#FFFFFF");
        put("g", "#DDD605");
    }};
    public static TextComponent ALISA_TAG;
    public static TextComponent YES;
    public static TextComponent NO;
    public static BaseComponent[] YES_NO;
    private static ChatColor prefixColor;
    private static ChatColor nameColor;
    private static ChatColor textColor;
    private static final TextComponent cleaner = text("");
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
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Использовать команду").color(ChatColor.BLUE).create()));
        textComponent.addExtra(cleaner);
        return textComponent;
    }

    public static TextComponent textCommand(String text, String command) {
        return textCommand(new TextComponent(text), command);
    }

    public static TextComponent textLink(String text, String link) {
        TextComponent textComponent = text(text, ChatColor.GOLD);
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Открыть ссылку").color(ChatColor.BLUE).create()));
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, link));
        textComponent.addExtra(cleaner);
        return textComponent;
    }

    public static boolean hasPattern(final String s, final String pattern) {
        return pattern.isEmpty() || Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(s).find();
    }

    public static BaseComponent[] buildWithTags(final String s, final String tags) {
        ComponentBuilder componentBuilder = new ComponentBuilder();
        componentBuilder.append(ALISA_TAG);

        if (tags.isEmpty()) {
            return componentBuilder.append(text(s)).create();
        }

        List<String> parsed = parseByPattern(tagPatternBuilder(tags + ",name"), s);
        final String replaced = replaceTags(s, "%%%");

        String[] arrs = replaced.split("%%%");
        IntStream.range(0, arrs.length).forEach(idx -> {
            final String[] codeLink = parsed.get(idx).split("\\|\\|");
            componentBuilder.append(text(arrs[idx]));
            if (tags.contains("link")) {
                componentBuilder.append(textLink(codeLink[0], codeLink[1]));
            } else if (tags.contains("button")) {
                TextComponent command = textCommand(codeLink[0], codeLink[1]);
                command.setColor(ChatColor.AQUA);
                command.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, codeLink[1]));

                componentBuilder.append(command);
            }
        });

        return componentBuilder.create();
    }

    public static String tagPatternBuilder(final ArrayList<String> tags) {
        final StringBuilder pattern = new StringBuilder("(");
        tags.forEach(tag -> pattern.append("<").append(tag).append(">(.+?)</").append(tag).append(">"));
        pattern.append(")");

        return pattern.toString();
    }

    public static String tagPatternBuilder(final String tags) {
        return tagPatternBuilder((ArrayList<String>) Arrays.stream(tags.split(",")).collect(Collectors.toList()));
    }

    public static List<String> parseByPattern(final String pattern, final String s) {

        Matcher m = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(s);

        final List<String> list = new ArrayList<>();
        while (m.find()) {
            list.add(m.group(3).trim() + "||" + m.group(2).trim());
        }

        return list;
    }

    public static String replaceTags(final String s, final String s1) {
        return removeSpaces(s.replaceAll("(<(/?)(.+?)>(.+?)<(/?)(.+?)>)+", s1));
    }

    public static String removeSpaces(final String s) {
        return s.trim().replaceAll(" +", " ");
    }

}
