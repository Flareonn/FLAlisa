package org.shy.alisa.listeners;

import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitScheduler;
import org.shy.alisa.Main;
import org.shy.alisa.utils.ColorUtil;

import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.String.format;

public class VoteEvent implements Listener {
    private static final Main ALISA = Main.getInstance();

    // Список проголосовавших
    private static ArrayList<String> playersVotes = new ArrayList<>();

    private static boolean active = false;
    private static int voteYes = 0;
    private static int voteNo = 0;
    private World world;

    private static final HashMap<TypeVote, Long> cooldownsGlobalDuration = new HashMap<TypeVote, Long>() {{
        put(TypeVote.SUN, ALISA.getConfig().getLong("votesun-global"));
        put(TypeVote.DAY, ALISA.getConfig().getLong("voteday-global"));
    }};
    private static final HashMap<TypeVote, Long> cooldownsPlayerDuration = new HashMap<TypeVote, Long>() {{
        put(TypeVote.SUN, ALISA.getConfig().getLong("votesun-personal"));
        put(TypeVote.DAY, ALISA.getConfig().getLong("voteday-personal"));
    }};

    private static final long voteDuration = ALISA.getConfig().getLong("duration");

    private static final HashMap<String, HashMap<TypeVote, Long>> cooldownPlayers = new HashMap<>();
    private static final HashMap<TypeVote, Long> cooldownGlobal = new HashMap<>();

    // Таблица инициаторов, не путать с массивом голосующих
    private static final HashMap<TypeVote, ArrayList<String>> banPlayerVote = new HashMap<TypeVote, ArrayList<String>>() {{
        put(TypeVote.SUN, new ArrayList<>());
        put(TypeVote.DAY, new ArrayList<>());
    }};
    private static final HashMap<TypeVote, Boolean> canGlobalVote = new HashMap<TypeVote, Boolean>() {{
        put(TypeVote.SUN, true);
        put(TypeVote.DAY, true);
    }};

    public enum TypeVote {
        SUN,
        DAY,
    }

    private static final BukkitScheduler scheduler = Main.getInstance().getServer().getScheduler();

    public VoteEvent(TypeVote type, CommandSender commandSender) {
        if(isActive()) {
            ALISA.say("Голосование уже запущено!", commandSender);
        } else if(checkCooldowns(commandSender, type)) {
            voteNo = 0;
            voteYes = 0;
            playersVotes = new ArrayList<>();
            world = Bukkit.getServer().getWorld("world");
            registerEvent(this, commandSender.getName(), type);
        }
    }

    private void registerEvent(Listener listener, String playerName, TypeVote type) {
        active = true;
        ALISA.getServer().getPluginManager().registerEvents(listener, ALISA);
        final TextComponent yesMessage = new TextComponent("/yes");
        final TextComponent noMessage = new TextComponent("/no");
        yesMessage.setColor(ChatColor.GREEN.asBungee());
        noMessage.setColor(ChatColor.RED.asBungee());
        yesMessage.setBold(true);
        noMessage.setBold(true);
        yesMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/yes"));
        noMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/no"));

        switch (type) {
            case SUN: {
                final BaseComponent[] message = new ComponentBuilder("[").color(ChatColor.GRAY.asBungee())
                        .append("Помощница").color(ChatColor.RED.asBungee())
                        .append("] ").color(ChatColor.GRAY.asBungee())
                        .append("Алиса: ").color(ChatColor.LIGHT_PURPLE.asBungee())
                        .append("Началось голосование за смену времени суток! Проголосуйте, введя '").color(ChatColor.YELLOW.asBungee())
                        .append(yesMessage).append("'").bold(false).color(ChatColor.YELLOW.asBungee()).append(" или '").append(noMessage).append("'").color(ChatColor.YELLOW.asBungee()).bold(false)
                        .create();
                ALISA.say(message);
            }
            case DAY: {
                final BaseComponent[] message = new ComponentBuilder("[").color(ChatColor.GRAY.asBungee())
                        .append("Помощница").color(ChatColor.RED.asBungee())
                        .append("] ").color(ChatColor.GRAY.asBungee())
                        .append("Алиса: ").color(ChatColor.LIGHT_PURPLE.asBungee())
                        .append("Началось голосование за смену погоды! Проголосуйте, введя '").color(ChatColor.YELLOW.asBungee())
                        .append(yesMessage).append("'").bold(false).color(ChatColor.YELLOW.asBungee()).append(" или '").append(noMessage).append("'").color(ChatColor.YELLOW.asBungee()).bold(false)
                        .create();
                ALISA.say(message);
            }
        }
        // Длительность голосования
        scheduler.runTaskLater(ALISA, () -> unregisterEvent(listener, playerName, type), 20L * voteDuration);
    }

    private static boolean isWin() {
        final int sumVotes = voteNo + voteYes;
        final int advantage = voteYes - voteNo;

        final double voteYesInPercent = voteYes / (1.0 * sumVotes);

        final boolean isWinRatio = voteYesInPercent > ALISA.getConfig().getDouble("success-ratio");
        final boolean isWinAdvantage = advantage > ALISA.getConfig().getInt("success-advantage");

        if (isWinRatio && isWinAdvantage) {
            Bukkit.getLogger().info("The vote win. Number of those who voted: Yes(" + voteYes + ") | No(" + voteNo + ")\nPercentage advantage: " + voteYesInPercent + ". Absolute advantage: " + advantage);
        } else {
            Bukkit.getLogger().info("The vote failed. Number of those who voted: Yes(" + voteYes + ") | No(" + voteNo + ")\nPercentage advantage: " + voteYesInPercent + ". Absolute advantage: " + advantage);
        }

        return isWinRatio && isWinAdvantage;
    }

    public static void setVote(boolean yesOrNo, CommandSender commandSender) {
        if(!isActive()) {
            ALISA.say("На данный момент нет никакого голосования!", commandSender);
        } else if(isVoted(commandSender.getName())) {
            ALISA.say("Вы уже голосовали!", commandSender);
        } else {
            playersVotes.add(commandSender.getName());
            if (yesOrNo) {
                voteYes++;
                ALISA.say(format("Вы проголосовали %s!", ColorUtil.success("за")), commandSender);
            } else {
                voteNo++;
                ALISA.say(format("Вы проголосовали %s!", ColorUtil.fail("против")), commandSender);
            }
        }
    }

    public static boolean isVoted(String playerName) {
        return playersVotes.contains(playerName);
    }

    private void unregisterEvent(Listener listener, String playerName, TypeVote type) {
        active = false;
        HandlerList.unregisterAll(listener);

        if(isWin()) {
            ALISA.say(format("Голосование увенчалось %s, смена...", ColorUtil.success("успехом")));
            switch (type) {
                case SUN:
                    world.setStorm(false);
                    world.setThundering(false);
                    break;
                case DAY:
                    world.setTime(0);
                    break;
            }
        } else {
            ALISA.say(format("Голосование %s, попробуйте позже", ColorUtil.fail("провалено")));
        }
        // Добавляем игрока в списки КД
        final long currentTime = System.currentTimeMillis();
        HashMap<TypeVote, Long> hashMap;
        if(cooldownPlayers.containsKey(playerName)) {
            hashMap = cooldownPlayers.get(playerName);
            hashMap.put(type, currentTime);
        } else {
            hashMap = new HashMap<TypeVote, Long>() {{
                put(type, currentTime);
            }};
        }
        cooldownPlayers.put(playerName, hashMap);
        cooldownGlobal.put(type, currentTime);

        // Запрет на голосование глобально
        canGlobalVote.put(type, false);
        // Запрет на голосование игроку
        banPlayerVote.get(type).add(playerName);

        // Таймер, который со временем разрешит снова голосовать всем
        scheduler.runTaskLater(ALISA, () -> {
            cooldownGlobal.remove(type);
            canGlobalVote.put(type, true);
        }, 20L * cooldownsGlobalDuration.get(type));
        // Таймер, который со временем разрешит снова голосовать игроку
        scheduler.runTaskLater(ALISA, () -> {
            cooldownPlayers.get(playerName).remove(type);
            banPlayerVote.get(type).remove(playerName);
        }, 20L * cooldownsPlayerDuration.get(type));
    }

    public static boolean isActive() {
        return active;
    }

    public static boolean checkCooldowns(CommandSender commandSender, TypeVote type) {
        final String playerName = commandSender.getName();
        // Блок вывода КД в чат
        if(cooldownPlayers.containsKey(playerName) && cooldownPlayers.get(playerName).containsKey(type)) {
            final String currentCooldown = String.valueOf(calculcateCooldown(cooldownsPlayerDuration.get(type), cooldownPlayers.get(playerName).get(type)));
            ALISA.say(format("%s: Вы сможете запустить голосование через %s сек.", ColorUtil.wrap("[Персонально]", ChatColor.GOLD) ,ColorUtil.fail(currentCooldown)), commandSender);
        } else if(cooldownGlobal.containsKey(type)) {
            final String currentCooldown = String.valueOf(calculcateCooldown(cooldownsGlobalDuration.get(type), cooldownGlobal.get(type)));
            ALISA.say(format("%s: Вы сможете запустить голосование через %s сек.", ColorUtil.wrap("[Глобально]", ChatColor.GOLD), ColorUtil.fail(currentCooldown)), commandSender);
        }

        boolean isCanByPersonal = !banPlayerVote.get(type).contains(playerName);
        boolean isCanByGlobal = canGlobalVote.get(type);
//        Команды отладки
//        Bukkit.getLogger().info("КД: " + cooldownPlayers.toString());
//        Bukkit.getLogger().info("ГКД: " + cooldownGlobal.toString());
//        Bukkit.getLogger().info("Глобально можно?: " + isCanByGlobal);
//        Bukkit.getLogger().info("Локально можно?: " + isCanByPersonal);

        return isCanByGlobal && isCanByPersonal;
    }

    private static long calculcateCooldown(Long cooldownDuration, Long cooldownBy) {
        return (cooldownDuration - (System.currentTimeMillis() - cooldownBy) / (20L * 60L)) - voteDuration;
    }
}