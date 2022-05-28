package org.flareon.alisa;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.flareon.alisa.chat.Answer;
import org.flareon.alisa.chat.MessageHandler;
import org.flareon.alisa.chat.Supervision;
import org.flareon.alisa.listeners.ChatEvent;
import org.flareon.alisa.listeners.JoinEvent;;
import org.flareon.alisa.utils.ChatUtil;
import org.flareon.alisa.utils.ColorUtil;
import org.flareon.alisa.cooldown.CooldownsHandler;
import org.flareon.alisa.utils.FileUtil;
import org.flareon.alisa.utils.ModeratorsUtil;

import java.util.*;

import static java.lang.String.format;

public class FLAlisa extends JavaPlugin {
    protected static FLAlisa instance;
    public Config config;
    public Config moderators;
    public Config stats;
    public CooldownsHandler cooldownsHandler;
    public ModeratorsHandler moderatorsHandler;
    public ModeratorsUtil moderatorsUtil;
    public MessageHandler messageHandler;
    public HashSet<String> knownPlayerNames;
    public Supervision supervision;
    public Answer answer;
//    public PlaytimeHandler playtimeHandler;
    public Statistics statistics;
    static {
        ConfigurationSerialization.registerClass(ModeratorsEntry.class, "ModeratorsEntry");
        ConfigurationSerialization.registerClass(Statistics.class, "Statistics");
//        ConfigurationSerialization.registerClass(PlaytimeReport.class, "PlaytimeReport");
    }

    public static FLAlisa getInstance() {return instance;}
    @Override
    public void onEnable() {
        Bukkit.getLogger().info("Plugin " + this.getName() + " is enabled right now!");
        instance = this;

        knownPlayerNames = new HashSet<>();

        registerConfig();
        registerCommands();
        registerEvents();
        initStats();
    }

    @Override
    public void onDisable() {
        saveStats();
    }

    private void saveStats() {
        stats.setCustom("statistics", this.statistics);
    }

    private void registerCommands() {
        this.getCommand("server").setExecutor(new CommandServer());
        this.getCommand("aseen").setExecutor(new CommandAseen());
        this.getCommand("mods").setExecutor(new CommandMods());
        this.getCommand("alisa").setExecutor(new CommandBot(initListCommands(true)));
        this.getCommand("ahelp").setExecutor(new CommandHelp(initListCommands(false)));
        this.getCommand("amouth").setExecutor(new CommandMouth());

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
        this.stats = new Config("stats.yml");

        initUtils();

        this.cooldownsHandler = new CooldownsHandler(config);
//        this.playtimeHandler = new PlaytimeHandler(config);
        this.moderatorsHandler = new ModeratorsHandler(instance);
        this.moderatorsUtil = new ModeratorsUtil(instance);

        // Chat modules
        this.messageHandler = new MessageHandler(instance);
        this.supervision = new Supervision();
        this.answer = new Answer();
    }

    public void registerEvents() {
        PluginManager pm = Bukkit.getServer().getPluginManager();
        pm.registerEvents(new JoinEvent(), this);
        pm.registerEvents(new ChatEvent(), this);
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

    private void initStats() {
        final Statistics st = (Statistics) stats.getObject("statistics");
        if (st != null) {
            getLogger().info("[FL:ALISA] Инициализация статистики...");
            this.statistics = st;
        }
        else {
            getLogger().info("[FL:ALISA] Файл статистики не найден, создание нового...");
            this.statistics = new Statistics(0, 0, 0, 0, 0, 0, 0, 0);
        }
    }

    public void addKnownPlayer(final String playerName) {
        this.knownPlayerNames.add(playerName);
    }

    public void broadcast(String words) {
        Bukkit.broadcastMessage(ColorUtil.getAlisaTag() + words);
    }

    public void say(String words, Player player) {
        ++statistics.chatMessages;
        player.sendMessage(ColorUtil.getAlisaTag() + words);
    }

    public void say(String words, CommandSender commandSender) {
        ++statistics.chatMessages;
        commandSender.sendMessage(ColorUtil.getAlisaTag() + words);
    }

    public void broadcast(BaseComponent[] component) {
        ++statistics.chatMessages;
        Bukkit.spigot().broadcast(component);
    }

    public void sayUnknownCommand(String words, CommandSender commandSender) {
        say(String.format("Используй команду правильно! %s", words), commandSender);
    }

    public void executeCommand(final String command) {
        new BukkitRunnable() {
            public void run() {
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
            }
        }.runTaskLater(instance, 1L);
    }

    public void debug(final String msg) {
        this.getLogger().info("(debug): " + msg);
    }
}