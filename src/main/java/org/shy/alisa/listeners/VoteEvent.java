package org.shy.alisa.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.shy.alisa.Main;

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
    private static TypeVote typeVote = null;
    private World world;


    private static final HashMap<TypeVote, String> sayStartVoting = new HashMap<TypeVote, String>() {{
        put(TypeVote.SUN, format("Началось голосование за смену погоды! Проголосуйте, введя '%s\u00A7l/yes\u00A7r%s' или '%s\u00A7l/no\u00A7r%s'", ChatColor.GREEN, ChatColor.YELLOW, ChatColor.RED, ChatColor.YELLOW));
        put(TypeVote.DAY, format("Началось голосование за смену времени суток! Проголосуйте, введя '%s\u00A7l/yes\u00A7r%s' или '%s\u00A7l/no\u00A7r%s'", ChatColor.GREEN, ChatColor.YELLOW, ChatColor.RED, ChatColor.YELLOW));
    }};

    private static final HashMap<TypeVote, Long> cooldownsGlobalDuration = new HashMap<TypeVote, Long>() {{
        put(TypeVote.SUN, ALISA.getConfig().getLong("votesun-global"));
        put(TypeVote.DAY, ALISA.getConfig().getLong("voteday-global"));
    }};

    private static final HashMap<TypeVote, Long> cooldownsPlayerDuration = new HashMap<TypeVote, Long>() {{
        put(TypeVote.SUN, ALISA.getConfig().getLong("votesun-personal"));
        put(TypeVote.DAY, ALISA.getConfig().getLong("voteday-personal"));
    }};

    // Таблица инициаторов, не путать с массивом голосующих
    private static final HashMap<String, ArrayList<TypeVote>> canPlayerVote = new HashMap<>();

    private static final HashMap<TypeVote, Boolean> canGlobalVote = new HashMap<TypeVote, Boolean>() {{
        put(TypeVote.SUN, true);
        put(TypeVote.DAY, true);
    }};

    public enum TypeVote {
        SUN,
        DAY,
    }

    public VoteEvent(TypeVote type) {
        voteNo = 0;
        voteYes = 0;
        typeVote = type;
        playersVotes = new ArrayList<>();
        world = Bukkit.getServer().getWorld("world");
        registerEvent(this);
    }

    public VoteEvent(TypeVote type, Player player) {
        if(isActive()) {
            ALISA.say("Голосование уже запущено!", player);
        } else if(checkCooldowns(player, type)) {
            new VoteEvent(type);
            // Таймер до следующего использования персонально. Сложение здесь - потому что начинается в самом начале работы голосования

            final String playerName = player.getName();
            ArrayList<TypeVote> cooldownsIniciator = canPlayerVote.get(playerName);
            cooldownsIniciator.add(type);
            canPlayerVote.put(playerName, cooldownsIniciator);
            ALISA.getServer().getScheduler().scheduleSyncDelayedTask(ALISA, () -> canPlayerVote.get(playerName).remove(type), cooldownsPlayerDuration.get(type) + ALISA.getConfig().getLong("duration"));
        }
    }

    private void registerEvent(Listener listener) {
        active = true;
        ALISA.getServer().getPluginManager().registerEvents(listener, ALISA);
        ALISA.say(sayStartVoting.get(typeVote));
        // Длительность голосования
        ALISA.getServer().getScheduler().scheduleSyncDelayedTask(ALISA, () -> unregisterEvent(listener), ALISA.getConfig().getLong("duration"));
    }

    private static boolean isWin() {
        final int sumVotes = voteNo + voteYes;
        final int advantage = voteYes - voteNo;

        final double voteYesInPercent = voteYes / (1.0 * sumVotes);

        final boolean isWinRatio = voteYesInPercent > ALISA.getConfig().getDouble("success-ratio");
        final boolean isWinAdvantage =  advantage > ALISA.getConfig().getInt("success-advantage");

        if (isWinRatio && isWinAdvantage) {
            Bukkit.getLogger().info("The vote win. Number of those who voted: Yes(" + voteYes + ") | No(" + voteNo + ")\nPercentage advantage: " + voteYesInPercent + ". Absolute advantage: " + advantage);
        } else {
            Bukkit.getLogger().info("The vote failed. Number of those who voted: Yes(" + voteYes + ") | No(" + voteNo + ")\nPercentage advantage: " + voteYesInPercent + ". Absolute advantage: " + advantage);
        }

        return isWinRatio && isWinAdvantage;
    }

    public static void setVote(boolean yesOrNo, Player player) {
        if(!isActive()) {
            ALISA.say("На данный момент нет никакого голосования!", player);
        } else if(isVoted(player)) {
            ALISA.say("Вы уже голосовали!", player);
        } else {
            playersVotes.add(player.getName());
            if (yesOrNo) {
                voteYes++;
                ALISA.say(format("Вы проголосовали %s\u00A7lза%s!", ChatColor.GREEN, ChatColor.YELLOW), player);
            } else {
                voteNo++;
                ALISA.say(format("Вы проголосовали %s\u00A7lпротив%s!", ChatColor.RED, ChatColor.YELLOW), player);
            }
        }
    }

    public static boolean isVoted(Player player) {
        return playersVotes.contains(player.getName());
    }

    private void unregisterEvent(Listener listener) {
        active = false;
        HandlerList.unregisterAll(listener);

        if(isWin()) {
            ALISA.say(format("Голосование увенчалось %s\u00A7lуспехом\u00A7r%s, смена...", ChatColor.GREEN, ChatColor.YELLOW));
            switch (typeVote) {
                case SUN:
                    world.setStorm(false);
                    world.setThundering(false);
                    break;
                case DAY:
                    world.setTime(0);
                    break;
            }
        } else {
            ALISA.say(format("Голосование %s\u00A7lпровалено\u00A7r%s, попробуйте позже", ChatColor.RED, ChatColor.YELLOW));
        }
        // Таймер, который со временем разрешит снова голосовать всем
        ALISA.getServer().getScheduler().scheduleSyncDelayedTask(ALISA, () -> canGlobalVote.put(typeVote, true), cooldownsGlobalDuration.get(typeVote));
        // Запрет на голосование глобально
        canGlobalVote.put(typeVote, false);
    }

    public static boolean isActive() {
        return active;
    }

    public static boolean checkCooldowns(Player player, TypeVote type) {
        // Добавить инициатора в таблицу, если его там еще нет.
        final String playerName = player.getName();
        if(!canPlayerVote.containsKey(playerName)) {
            canPlayerVote.put(playerName, new ArrayList<>());
        }

        boolean isCanVote = canGlobalVote.get(type) && !canPlayerVote.get(playerName).contains(type);
        if(!isCanVote) {
            ALISA.say("Вы не можете так часто использовать голосование!", player);
        }
        return isCanVote;
    }
}