package org.flareon.alisa;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.flareon.alisa.listeners.JoinEvent;
import org.flareon.alisa.utils.ChatUtil;
import org.flareon.alisa.utils.ColorUtil;
import org.flareon.alisa.cooldown.CooldownsHandler;

import java.util.Map;

import static java.lang.String.format;

public class FLAlisa extends JavaPlugin {
    protected static FLAlisa instance;
    public Config config;
    public Config moderators;
    public CooldownsHandler cooldownsHandler;
    public ModeratorsHandler moderatorsHandler;
    static {
        ConfigurationSerialization.registerClass(ModeratorsEntry.class, "ModeratorsEntry");
    }

    public static FLAlisa getInstance() {return instance;}
    @Override
    public void onEnable() {
        Bukkit.getLogger().info("Plugin " + this.getName() + " is enabled right now!");
        instance = this;
        registerConfig();
        registerCommands();
        registerEvents();
    }

    private void registerCommands() {
        this.getCommand("server").setExecutor(new CommandServer());
        this.getCommand("aseen").setExecutor(new CommandAseen());
        this.getCommand("mods").setExecutor(new CommandMods());
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
        this.saveDefaultConfig();
        this.config = new Config(this);
        this.moderators = new Config("moderators.yml");

        initUtils();

        this.cooldownsHandler = new CooldownsHandler(config);
        this.moderatorsHandler = new ModeratorsHandler(instance);
    }

    public void registerEvents() {
        PluginManager pm = Bukkit.getServer().getPluginManager();
        pm.registerEvents(new JoinEvent(), this);
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