package org.shy.alisa;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.shy.alisa.listeners.VoteEvent;

import java.util.EventListener;
import java.util.Objects;


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