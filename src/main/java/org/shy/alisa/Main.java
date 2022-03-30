package org.shy.alisa;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.shy.alisa.listeners.ChatEvent;
import org.shy.alisa.listeners.JoinEvent;

public class Main extends JavaPlugin {
    private final static String ALISA_TAG =  ChatColor.AQUA + "\u00A7l["+ChatColor.LIGHT_PURPLE+"\u00A7lАлиса" + ChatColor.AQUA + "\u00A7l]\u00A7r " + ChatColor.YELLOW;
    private static Main instance;

    public static Main getInstance() {
        return instance;
    }
    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;

        Bukkit.getLogger().info("Enabled " + this.getName());
        this.getCommand("colors").setExecutor(new AlisaCommandColors());
        this.getCommand("votesun").setExecutor(new AlisaCommandVotesun());
        this.getCommand("voteday").setExecutor(new AlisaCommandVoteday());
        this.getCommand("yes").setExecutor(new VoteCommand() {

            @Override
            public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
                if(!(commandSender instanceof Player)) {
                    commandSender.sendMessage("Only players can run this command !");
                    return false;
                }
                this.voteUp(true, (Player) commandSender);
                return true;
            }
        });
        this.getCommand("no").setExecutor(new VoteCommand() {
            @Override
            public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
                if(!(commandSender instanceof Player)) {
                    commandSender.sendMessage("Only players can run this command !");
                    return false;
                }
                this.voteUp(false, (Player) commandSender);
                return true;
            }
        });

        registerEvents();
    }

    public void registerEvents() {
        PluginManager pm = Bukkit.getServer().getPluginManager();
        pm.registerEvents(new JoinEvent(), this);
        pm.registerEvents(new ChatEvent(), this);
    }

    public void say(String words) {
        Bukkit.broadcastMessage(ALISA_TAG + words);
    }

    public void say(String words, Player player) {
        player.sendMessage(ALISA_TAG + words);
    }
}