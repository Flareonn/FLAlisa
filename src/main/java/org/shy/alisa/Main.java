package org.shy.alisa;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.shy.alisa.utils.ColorUtil;

import java.io.File;

public class Main extends JavaPlugin {
    private static Main instance;
    private static File rulesFile;

    public static Main getInstance() {
        return instance;
    }
    public static File getRulesFile() {return rulesFile;}
    @Override
    public void onEnable() {
        saveDefaultConfig();
        rulesFile = new File(getDataFolder(), "rules.json");
        if(!rulesFile.exists()) {
            saveResource(rulesFile.getName(), true);
        }
        instance = this;
        // Initialize Utils
        new ColorUtil(getConfig());

        Bukkit.getLogger().info("Plugin " + this.getName() + " is enabled right now!");

        this.getCommand("alisa").setExecutor(new AlisaCommandBot());
        this.getCommand("ahelp").setExecutor(new AlisaCommandHelp());

        this.getCommand("inf").setExecutor(new AlisaCommandRules());
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

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        new ColorUtil(getConfig());
    }

    public void registerEvents() {
//        PluginManager pm = Bukkit.getServer().getPluginManager();
//        pm.registerEvents(new JoinEvent(), this);
//        pm.registerEvents(new ChatEvent(), this);
    }

    public void say(String words) {
        Bukkit.broadcastMessage(ColorUtil.getAlisaTag() + words);
    }

    public void say(String words, Player player) {
        player.sendMessage(ColorUtil.getAlisaTag() + words);
    }

    public void say(String words, CommandSender commandSender) {
        commandSender.sendMessage(ColorUtil.getAlisaTag() + words);
    }

    public void sayUnknownCommand(String words, CommandSender commandSender) {
        say(String.format("Используй команду правильно!%s ", words), commandSender);
    }
}