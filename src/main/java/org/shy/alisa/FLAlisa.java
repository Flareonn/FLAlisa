package org.shy.alisa;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.shy.alisa.cooldown.CooldownsHandler;
import org.shy.alisa.utils.ChatUtil;
import org.shy.alisa.utils.ColorUtil;

import java.io.File;
import java.util.Map;

import static java.lang.String.format;

public class FLAlisa extends JavaPlugin {
    protected static FLAlisa instance;
    protected static File rulesFile;
    public Config config;
    public CooldownsHandler cooldownsHandler;

    public static FLAlisa getInstance() {return instance;}
    public static File getRulesFile() {return rulesFile;}
    @Override
    public void onEnable() {
        Bukkit.getLogger().info("Plugin " + this.getName() + " is enabled right now!");

        registerConfig();
        registerCommands();
        registerEvents();
    }

    private void registerCommands() {
        this.getCommand("server").setExecutor(new CommandServer());
        this.getCommand("aseen").setExecutor(new CommandAseen());
        this.getCommand("alisa").setExecutor(new CommandBot(initListCommands(true)));
        this.getCommand("ahelp").setExecutor(new CommandHelp(initListCommands(false)));

        this.getCommand("inf").setExecutor(new CommandRules());
//        this.getCommand("colors").setExecutor(new CommandColors());
        this.getCommand("votesun").setExecutor(new CommandVotesun());
        this.getCommand("voteday").setExecutor(new CommandVoteday());
        this.getCommand("yes").setExecutor(new VoteCommand(true));
        this.getCommand("no").setExecutor(new VoteCommand(false));
    }

    public void registerConfig() {
        instance = this;
        this.saveDefaultConfig();
        this.config = new Config(this);

        rulesFile = new File(getDataFolder(), "rules.json");
        if(!rulesFile.exists()) {
            saveResource(rulesFile.getName(), true);
        }
        initUtils();
        this.cooldownsHandler = new CooldownsHandler(config);
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
        new ColorUtil(this.config);
        new ChatUtil(this.config);
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