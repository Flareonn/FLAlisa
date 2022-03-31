package org.shy.alisa;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.shy.alisa.listeners.VoteEvent;

import java.util.UUID;

import static java.lang.String.format;

class AlisaCommandBot implements CommandExecutor {
    public static final Main ALISA = Main.getInstance();
    public static Player iniciator;
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player) {
            iniciator = (Player) commandSender;
        }
        if(strings.length == 0) {
            commandSender.sendMessage(
                    format("%s--------- %s[ADMIN]%s: %sАЛИСА%s -----------\n", ChatColor.YELLOW, ChatColor.DARK_RED, ChatColor.YELLOW, ChatColor.AQUA, ChatColor.YELLOW) +
                        format("%s/alisa:%s Все комманды администратора\n", ChatColor.GOLD, ChatColor.WHITE) +
                        format("%s/alisa read:%s Посмотреть значение в конфиге\n", ChatColor.GOLD, ChatColor.WHITE) +
                        format("%s/alisa set:%s Изменить значение в конфиге\n", ChatColor.GOLD, ChatColor.WHITE) +
                        format("%s/alisa reloadconfig:%s Перезагрузка конфига\n", ChatColor.GOLD, ChatColor.WHITE) +
                        format("%s/alisa getname:%s Получить Ник игрока по UUID\n", ChatColor.GOLD, ChatColor.WHITE) +
                        format("%s/alisa getuuid:%s Получить UUID игрока по Нику\n", ChatColor.GOLD, ChatColor.WHITE)
                    );
        } else {
            Player player;
            switch (strings[0]) {
                case "read":
                    ALISA.say(format("Значение в конфиге поля '%s': %s", ChatColor.LIGHT_PURPLE + strings[1] + ChatColor.YELLOW, ChatColor.GREEN + "\u00A7l" + ALISA.getConfig().getString(strings[1])), iniciator);
                    break;
                case "set":
                    String configValue = strings[2];
                    ALISA.getConfig().set(strings[1], isDigit(configValue) ? Integer.parseInt(configValue) : configValue);
                    ALISA.saveConfig();
                    ALISA.say(format("Вы установили поле '%s' со значением: %s", ChatColor.LIGHT_PURPLE + strings[1] + ChatColor.YELLOW, ChatColor.DARK_PURPLE + "\u00A7l" + configValue), iniciator);
                    break;
                case "reloadconfig":
                    ALISA.reloadConfig();
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
                        ALISA.say(format("Ник игрока: %s", player.getName()), iniciator);
                        ALISA.say(format("UUID: %s", player.getUniqueId()), iniciator);
                    } else {
                        ALISA.say("Игрок с таким именем не найден", iniciator);
                    }

                    break;
                case "tospawn":
                    // WIP
                    player = Bukkit.getPlayer(strings[1]);
                    if(player != null) {
                        Bukkit.getServer().dispatchCommand(player, "spawn");
                        ALISA.say("Игрок будет отправлен на спавн", iniciator);
                    } else {
                        ALISA.say("Игрок с таким именем не найден", iniciator);
                    }
                    break;
                default:
                    ALISA.say("Такой команды не существует. Список моих возможностей: -> /alisa", iniciator);
                    break;
            }
        }
        return true;
    }

    private static boolean isDigit(String s) throws NumberFormatException {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

class AlisaCommandColors implements CommandExecutor {
    public static final Main ALISA = Main.getInstance();
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
    public static final Main ALISA = Main.getInstance();
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
            ALISA.say("Сейчас ясная погода!", (Player) commandSender);
        }
        return true;
    }
}

class AlisaCommandVoteday implements CommandExecutor {
    public static final Main ALISA = Main.getInstance();

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
            ALISA.say("Сейчас день!", (Player) commandSender);
        }
        return true;
    }

    private boolean isDay() {
        long time = Bukkit.getServer().getWorld("world").getTime();
        return time < 12300 || time > 23850;
    }
}

abstract class VoteCommand implements CommandExecutor {
    protected void voteUp(boolean isYes, Player player) {
        VoteEvent.setVote(isYes, player);
    }
}