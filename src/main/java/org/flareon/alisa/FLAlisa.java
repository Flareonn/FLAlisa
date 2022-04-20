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
import org.flareon.alisa.chat.MessageHandler;
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
    public CooldownsHandler cooldownsHandler;
    public ModeratorsHandler moderatorsHandler;
    public ModeratorsUtil moderatorsUtil;
    public MessageHandler messageHandler;
    public HashSet<String> knownPlayerNames;
    public HashMap<PunishmentType, String> punishments;
    public HashMap<AnswerReason, Answer> answers;
    private Random rand;
    private ArrayList<String> helloAnswers;
    private ArrayList<String> byeAnswers;
//    public PlaytimeHandler playtimeHandler;
    static {
        ConfigurationSerialization.registerClass(ModeratorsEntry.class, "ModeratorsEntry");
//        ConfigurationSerialization.registerClass(PlaytimeReport.class, "PlaytimeReport");
    }

    public static FLAlisa getInstance() {return instance;}
    @Override
    public void onEnable() {
        Bukkit.getLogger().info("Plugin " + this.getName() + " is enabled right now!");
        instance = this;

        rand = new Random();
        knownPlayerNames = new HashSet<>();

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

        initUtils();

        this.cooldownsHandler = new CooldownsHandler(config);
//        this.playtimeHandler = new PlaytimeHandler(config);
        this.moderatorsHandler = new ModeratorsHandler(instance);
        this.moderatorsUtil = new ModeratorsUtil(instance);
        this.messageHandler = new MessageHandler(instance);
        this.answers = new HashMap<>();
        this.punishments = new HashMap<>();
        this.helloAnswers = FileUtil.readProjectFileLines("hello-answers.txt");
        this.byeAnswers = FileUtil.readProjectFileLines("bye-answers.txt");
        this.createAnswers();
        this.createPunishments();
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

    public void addKnownPlayer(final String playerName) {
        this.knownPlayerNames.add(playerName);
    }

    public void broadcast(String words) {
        Bukkit.broadcastMessage(ColorUtil.getAlisaTag() + words);
    }

    public void say(String words, Player player) {
        player.sendMessage(ColorUtil.getAlisaTag() + words);
    }

    public void say(String words, CommandSender commandSender) {
        commandSender.sendMessage(ColorUtil.getAlisaTag() + words);
    }

    public void sayHello(final Player player) {
        say(helloAnswers.get(rand.nextInt(helloAnswers.size())), player);
    }

    public void sayBye(final Player player) {
        say(byeAnswers.get(rand.nextInt(byeAnswers.size())), player);
    }

    public void broadcast(BaseComponent[] component) {
        Bukkit.spigot().broadcast(component);
    }

    public void sayUnknownCommand(String words, CommandSender commandSender) {
        say(String.format("Используй команду правильно! %s", words), commandSender);
    }

    public enum PunishmentType
    {
        MUTE,
        WARN,
        BAN;
    }

    public enum AnswerReason
    {
        CAPS,
        PROFANITY,
        FLOOD,
        ADVERTISEMENT,
        WARN,
    }

    private void createAnswers() {
        this.answers.put(AnswerReason.CAPS, new Answer("answers-caps.txt", this));
        this.answers.put(AnswerReason.FLOOD, new Answer("answers-flood.txt", this));
        this.answers.put(AnswerReason.ADVERTISEMENT, new Answer("answers-advertisement.txt", this));
        this.answers.put(AnswerReason.PROFANITY, new Answer("answers-profanity.txt", this));
        this.answers.put(AnswerReason.WARN, new Answer("answers-warn.txt", this));
    }

    private void createPunishments() {
        this.punishments.put(PunishmentType.MUTE, config.getString("mute-command"));
        this.punishments.put(PunishmentType.WARN, config.getString("warn-command"));
    }

    private void executeCommand(final String command) {
        new BukkitRunnable() {
            public void run() {
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
            }
        }.runTaskLater(instance, 1L);
    }

    private String getPunishmentCommand(final PunishmentType punishmentType) {
        return punishments.get(punishmentType);
    }

    private void mute(final String playerName, final int durationSeconds, final String reason) {
        new BukkitRunnable() {
            public void run() {
                executeCommand(String.format(getPunishmentCommand(PunishmentType.MUTE), playerName, durationSeconds + " sec", reason));
//                final Statistics statistics = Alisa.this.statistics;
//                ++statistics.mutes;
//                final Statistics statistics2 = Alisa.this.statistics;
//                statistics2.mutesDuration += durationSeconds;
            }
        }.runTaskLater(instance, 1L);
    }

    private void warn(final String playerName, final String reason) {
        new BukkitRunnable() {
            public void run() {
                executeCommand(String.format(getPunishmentCommand(PunishmentType.WARN), playerName, reason));
//                final Statistics statistics = Alisa.this.statistics;
//                ++statistics.warns;
            }
        }.runTaskLater(instance, 1L);
    }

    public void punish(final Player player, final int durationSeconds, final String reason, AnswerReason answerReason) {
        final String playerName = player.getName();
        if (cooldownsHandler.warnCooldowns.isExpired(playerName)) {
            warn(playerName, reason);
            answerReason = AnswerReason.WARN;
        } else {
            mute(playerName, durationSeconds, reason);
        }
        say(format("%s, ", ColorUtil.wrap(playerName, ChatColor.GOLD)) + this.answers.get(answerReason).getRandomAnswer(playerName), player);
        cooldownsHandler.warnCooldowns.trigger(playerName);
    }

    private class Answer {
        ArrayList<String> answerStrings;
        FLAlisa ALISA;

        private Answer(final String answersFilePath, final FLAlisa ALISA) {
            this.ALISA = ALISA;
            this.answerStrings = FileUtil.readProjectFileLines(answersFilePath);
        }

        protected String getRandomAnswer(final String playerName) {
            return String.format(answerStrings.get(rand.nextInt(answerStrings.size())), "#c2" + playerName + "#c1");
        }
    }

    public void debug(final String msg) {
        this.getLogger().info("(debug): " + msg);
    }
}