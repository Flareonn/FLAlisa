package org.shy.alisa;

import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.help.HelpTopic;
import org.bukkit.util.ChatPaginator;
import org.shy.alisa.listeners.VoteEvent;
import org.shy.alisa.utils.ColorUtil;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.lang.String.format;

class AlisaCommandHelp implements CommandExecutor {
    private static final Main ALISA = Main.getInstance();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        final boolean isPlayerOp = commandSender.isOp();

        ALISA.say("------------Мои команды------------", commandSender);
        if(isPlayerOp) {
            ALISA.getDescription().getCommands().forEach((k, v)-> {
                commandSender.sendMessage(format("%s: %s", ChatColor.GOLD + k, ChatColor.WHITE + "" + v.get("description")));
            });
        } else {
            ALISA.getDescription().getCommands().forEach((k, v)-> {
                if(!String.valueOf(v.get("default")).equals("op")) {
                    commandSender.sendMessage(format("%s: %s", ChatColor.GOLD + k, ChatColor.WHITE + "" + v.get("description")));
                }
            });
        }

        return true;
    }
}

class AlisaCommandBot implements CommandExecutor, TabCompleter {
    private static final Main ALISA = Main.getInstance();
    private static final ArrayList<String> subCommands = new ArrayList<String>() {{
        add("read");
        add("set");
        add("reloadconfig");
//        add("getuuid");
//        add("getname");
//        add("tospawn");
    }};

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(strings.length == 0) {
            commandSender.sendMessage(
                    format("%s--------- %s[ADMIN]%s: %sАЛИСА%s -----------\n", ChatColor.YELLOW, ChatColor.DARK_RED, ChatColor.YELLOW, ChatColor.AQUA, ChatColor.YELLOW) +
                            format("%s/alisa: %s\n", ChatColor.GOLD, ChatColor.WHITE + ALISA.getCommand("alisa").getDescription()) +
                            format("%s/alisa read: %s\n", ChatColor.GOLD, ChatColor.WHITE + ALISA.getCommand("alisa read").getDescription()) +
                            format("%s/alisa set: %s\n", ChatColor.GOLD, ChatColor.WHITE + ALISA.getCommand("alisa set").getDescription()) +
                            format("%s/alisa reloadconfig: %s\n", ChatColor.GOLD, ChatColor.WHITE + ALISA.getCommand("alisa reloadconfig").getDescription()) +
                            format("%s/alisa getname: %s\n", ChatColor.GOLD, ChatColor.WHITE + ALISA.getCommand("alisa getname").getDescription()) +
                            format("%s/alisa getuuid: %s\n", ChatColor.GOLD, ChatColor.WHITE + ALISA.getCommand("alisa getuuid").getDescription())
            );
        } else {
            Player player;
            switch (strings[0]) {
                case "read":
                    commandRead(strings, commandSender);
                    break;
                case "set":
                    commandSet(strings, commandSender);
                    break;
                case "reloadconfig":
                    ALISA.reloadConfig();
                    ALISA.say("Конфиг перезагружен!", commandSender);
                    break;
                case "getuuid":
                case "getname":
                    // WIP
                    final UUID uuid = UUID.fromString(strings[1]);
                    if(uuid != null) {
                        player = Bukkit.getPlayer(uuid);
                    } else {
                        player = Bukkit.getPlayer(strings[1]);
                    }

                    if(player != null) {
                        ALISA.say(format("Ник игрока: %s", player.getName()), commandSender);
                        ALISA.say(format("UUID: %s", player.getUniqueId()), commandSender);
                    } else {
                        ALISA.say("Игрок с таким именем не найден", commandSender);
                    }

                    break;
                case "tospawn":
                    // WIP
                    player = Bukkit.getPlayer(strings[1]);
                    if(player != null) {
                        Bukkit.getServer().dispatchCommand(player, "spawn");
                        ALISA.say("Игрок будет отправлен на спавн", commandSender);
                    } else {
                        ALISA.say("Игрок с таким именем не найден", commandSender);
                    }
                    break;
                default:
                    ALISA.say("Такой команды не существует. Список моих возможностей: -> /alisa", commandSender);
                    break;
            }
        }
        return true;
    }

    private static void commandRead(String[] strings, CommandSender commandSender) {
        if(strings.length > 1) {
            String configKey = strings[1];
            Object configValue = ALISA.getConfig().get(configKey);
            if(configValue instanceof MemorySection) {
                configValue = ((MemorySection) configValue).getKeys(false);
            }

            if(configValue != null) {
                ALISA.say(format("Значение в конфиге поля '%s': %s", ColorUtil.wrap(configKey, ChatColor.LIGHT_PURPLE), ColorUtil.success(configValue.toString())), commandSender);
            } else {
                ALISA.say(format("Поле с таким именем %s", ColorUtil.fail("не найдено")), commandSender);
            }
        } else {
            ALISA.sayUnknownCommand("\n" + ALISA.getCommand("alisa " + strings[0]).getUsage() , commandSender);
        }
    }

    private static void commandSet(String[] strings, CommandSender commandSender) {
        switch (strings.length) {
            case 1:
                ALISA.say("Введите имя поля, значение которому вы хотите указать!", commandSender);
                break;
            case 2:
                ALISA.say("Введите значение поля!", commandSender);
                break;
            case 3:
                String configKey = strings[1];
                String configValue = strings[2];
                ALISA.getConfig().set(configKey, isDigit(configValue) ? Integer.parseInt(configValue) : configValue);
                ALISA.saveConfig();
                ALISA.say(format("Вы установили поле '%s' со значением: %s", ColorUtil.wrap(configKey, ChatColor.LIGHT_PURPLE), ColorUtil.wrap(configValue, ChatColor.DARK_PURPLE, ChatColor.BOLD)), commandSender);
                break;
            default:
                ALISA.sayUnknownCommand("\n" + ALISA.getCommand("alisa " + strings[0]).getUsage() , commandSender);
                break;
        }
    }

    private static boolean isDigit(String s) throws NumberFormatException {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> result = new ArrayList<>();
        switch(strings.length) {
            case 1:
                subCommands.forEach(w->{if(w.startsWith(strings[0])) result.add(w);});
                return result;
            case 2:
                if(strings[0].equals("read") || strings[0].equals("set")) {
                    ALISA.getConfig().getKeys(false).forEach(w -> {
                        if (w.startsWith(strings[1])) result.add(w);
                    });
                    return result;
                }
                break;
            default:
                break;
        }
        return null;
    }
}

class AlisaCommandColors implements CommandExecutor {
    private static final Main ALISA = Main.getInstance();
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            ALISA.say("Вы можете наблюдать доступные цвета и модификаторы:\n"
                    +"-- Цвета:\n" + ChatColor.BLACK + "0 " + ChatColor.DARK_BLUE + "1 " + ChatColor.DARK_GREEN + "2 " +
                    ChatColor.DARK_AQUA + "3 " + ChatColor.DARK_RED + "4 " + ChatColor.DARK_PURPLE + "5 " +
                    ChatColor.GOLD + "6 " + ChatColor.GRAY + "7 " + ChatColor.DARK_GRAY + "8 " +
                    ChatColor.BLUE + "9 " + ChatColor.GREEN + "a " + ChatColor.AQUA + "b " +
                    ChatColor.RED + "c " + ChatColor.LIGHT_PURPLE + "d " + ChatColor.YELLOW + "e " +
                    ChatColor.WHITE + "f\n" +
                    ChatColor.YELLOW + "-- Модификаторы: " + ChatColor.WHITE + "\n \u00A7ll\u00A7r \u00A7oo\u00A7r \u00A7nn\u00A7r \u00A7mm\u00A7r \u00A7kk\u00A7r(k)", player);
        }
        // If the player (or console) uses our command correct, we can return true
        return true;
    }
}

class AlisaCommandVotesun implements CommandExecutor {
    private static final Main ALISA = Main.getInstance();
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!Bukkit.getWorld("world").isClearWeather()) {
            if(commandSender instanceof Player) {
                new VoteEvent(VoteEvent.TypeVote.SUN, (Player) commandSender);
            } else {
                new VoteEvent(VoteEvent.TypeVote.SUN);
            }
        }
        else {
            ALISA.say("Сейчас ясная погода!", commandSender);
        }
        return true;
    }
}

class AlisaCommandVoteday implements CommandExecutor {
    private static final Main ALISA = Main.getInstance();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!isDay()) {
            if(commandSender instanceof Player) {
                new VoteEvent(VoteEvent.TypeVote.DAY, (Player) commandSender);
            } else {
                new VoteEvent(VoteEvent.TypeVote.DAY);
            }
        }
        else {
            ALISA.say("Сейчас день!", commandSender);
        }
        return true;
    }

    private boolean isDay() {
        long time = Bukkit.getServer().getWorld("world").getTime();
        return time < 12300 || time > 23850;
    }
}

class AlisaCommandRules implements CommandExecutor, TabCompleter {
    private static final Main ALISA = Main.getInstance();
    private static final ArrayList<String> rulesInPoints = new ArrayList<>();
    private static JsonObject rules;

    {
        try {
            rules = new JsonParser().parse(new FileReader(Main.getRulesFile())).getAsJsonObject().getAsJsonObject("rules");
            rules.getAsJsonObject().entrySet().forEach(w -> rulesInPoints.add(w.getKey()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(strings.length != 0) {
            String pointRule = strings[0];
            JsonObject rule = rules.getAsJsonObject(pointRule);

            if (rule == null) {
                ALISA.say("Такого правила нет в сводках", commandSender);
            } else {
                String punishment = safelyGetFromJson("punishment", rule);
                String content = safelyGetFromJson("content", rule);
                ALISA.say(
                        format("%s\n%s\n" +
                                        (!punishment.equals("") ? ChatColor.RED + "" + ChatColor.UNDERLINE + "Наказание:" + ChatColor.RESET + "" + ChatColor.RED + " %s" : ""),
                                "----------------" + pointRule + "----------------",
                                content,
                                punishment
                        ),
                        commandSender);
            }
        } else {
            ALISA.sayUnknownCommand("\n" + ALISA.getCommand("inf").getUsage(), commandSender);
        }
        return true;
    }

    private String safelyGetFromJson(String memberName, JsonObject jsonObject) {
        JsonElement member = jsonObject.get(memberName);
        if(member != null) {
            return new String(member.getAsString().getBytes(), StandardCharsets.UTF_8);
        } else {
            return "";
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if(strings.length == 1) {
            List<String> result = new ArrayList<>();
            rulesInPoints.forEach(w -> {
                if (w.toLowerCase().startsWith(strings[0])) result.add(w);
            });
            return result;
        }
        return null;
    }
}

abstract class VoteCommand implements CommandExecutor {
    protected void voteUp(boolean isYes, Player player) {
        VoteEvent.setVote(isYes, player);
    }
}