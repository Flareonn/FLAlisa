package org.shy.alisa;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.shy.alisa.utils.ChatUtil;
import org.shy.alisa.utils.ColorUtil;

import java.io.File;
import java.util.Map;

import static java.lang.String.format;

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
        initUtils();


        Bukkit.getLogger().info("Plugin " + this.getName() + " is enabled right now!");

        this.getCommand("alisa").setExecutor(new AlisaCommandBot(initListCommands(true)));
        this.getCommand("ahelp").setExecutor(new AlisaCommandHelp(initListCommands(false)));

        this.getCommand("inf").setExecutor(new AlisaCommandRules());
//        this.getCommand("colors").setExecutor(new AlisaCommandColors());
        this.getCommand("votesun").setExecutor(new AlisaCommandVotesun());
        this.getCommand("voteday").setExecutor(new AlisaCommandVoteday());
        this.getCommand("yes").setExecutor(new VoteCommand(true));
        this.getCommand("no").setExecutor(new VoteCommand(false));

        registerEvents();
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        initUtils();
    }

    public void registerEvents() {
//        PluginManager pm = Bukkit.getServer().getPluginManager();
//        pm.registerEvents(new JoinEvent(), this);
//        pm.registerEvents(new ChatEvent(), this);
    }

    private String initListCommands(boolean isOp) {
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String, Map<String, Object>> mapEntry : getDescription().getCommands().entrySet()) {
            final boolean isCommandOp = (mapEntry.getValue().get("default")).equals("op");
            final String command = format("%s: %s\n", ChatColor.GOLD + "/" + mapEntry.getKey(), ChatColor.WHITE + "" + mapEntry.getValue().get("description"));

            if(isOp && isCommandOp) {
                sb.append(command);
            } else if(!isOp && !isCommandOp) {
                sb.append(command);
            }
        }
        return String.valueOf(sb);
    }
    private void initUtils() {
        new ColorUtil(getConfig());
        new ChatUtil(getConfig());
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

    public void say(BaseComponent[] component) {
        Bukkit.spigot().broadcast(component);
    }

    public void sayUnknownCommand(String words, CommandSender commandSender) {
        say(String.format("Используй команду правильно!%s ", words), commandSender);
    }
}