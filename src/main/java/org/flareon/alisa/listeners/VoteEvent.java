package org.flareon.alisa.listeners;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.flareon.alisa.FLAlisa;
import org.flareon.alisa.utils.ChatUtil;
import org.flareon.alisa.utils.ColorUtil;

import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.String.format;

public class VoteEvent {
    private static final FLAlisa ALISA = FLAlisa.getInstance();

    // Список проголосовавших
    private static ArrayList<String> playersVotes = new ArrayList<>();

    private static boolean active = false;
    private static int voteYes = 0;
    private static int voteNo = 0;
    private World world;

    private static final HashMap<TypeVote, BaseComponent[]> sayStartVote = new HashMap<TypeVote, BaseComponent[]>() {{
       put(TypeVote.SUN, new ComponentBuilder()
                .append(ChatUtil.ALISA_TAG).append(ChatUtil.text("Началось голосование за смену погоды! Голосуйте: ")).append(ChatUtil.YES_NO).create());
       put(TypeVote.DAY, new ComponentBuilder()
                .append(ChatUtil.ALISA_TAG).append(ChatUtil.text("Началось голосование за смену времени суток! Голосуйте: ")).append(ChatUtil.YES_NO).create());
    }};

    public enum TypeVote {
        SUN,
        DAY,
    }

    public VoteEvent(TypeVote type, CommandSender commandSender) {
        if(isActive()) {
            ALISA.say("Голосование уже запущено!", commandSender);
        } else if(checkCooldowns(commandSender, type)) {
            voteNo = 0;
            voteYes = 0;
            playersVotes = new ArrayList<>();
            world = Bukkit.getServer().getWorld("world");
            registerEvent(type);
            VoteEvent.setVote(true, commandSender);
        }
    }

    private void registerEvent(TypeVote type) {
        active = true;
        ALISA.broadcast(sayStartVote.get(type));
        ++ALISA.statistics.totalVotesStarted;
        // Длительность голосования
        ALISA.getServer().getScheduler().runTaskLater(ALISA, () -> unregisterEvent(type), 20L * ALISA.config.getLong("duration"));
    }

    private static boolean isWin() {
        final int sumVotes = voteNo + voteYes;
        final int advantage = voteYes - voteNo;

        final double voteYesInPercent = voteYes / (1.0 * sumVotes);

        final boolean isWinRatio = voteYesInPercent > ALISA.config.getFloat("success-ratio");
        final boolean isWinAdvantage = advantage > ALISA.config.getInt("success-advantage");

        if (isWinRatio && isWinAdvantage) {
            ++ALISA.statistics.successfulVotes;
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
                ALISA.say(String.format("Вы проголосовали %s!", ColorUtil.success("за")), commandSender);
            } else {
                voteNo++;
                ALISA.say(format("Вы проголосовали %s!", ColorUtil.fail("против")), commandSender);
            }
        }
    }

    public static boolean isVoted(String playerName) {
        return playersVotes.contains(playerName);
    }

    private void unregisterEvent(TypeVote type) {
        active = false;

        if(isWin()) {
            ALISA.broadcast(format("Голосование увенчалось %s, смена...", ColorUtil.success("успехом")));
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
            ALISA.broadcast(format("Голосование %s, попробуйте позже", ColorUtil.fail("провалено")));
        }
    }

    public static boolean isActive() {
        return active;
    }

    public static boolean checkCooldowns(CommandSender commandSender, TypeVote type) {
        final String playerName = commandSender.getName();
        // Блок вывода КД в чат
        boolean globalCooldown = false;
        boolean personalCooldown = false;
        switch (type) {
            case SUN:
                if (!ALISA.cooldownsHandler.votesunPersonalCooldowns.isExpired(playerName)) {
                    ALISA.say(format("%s: Вы сможете запустить голосование через %s сек.",
                            ColorUtil.wrap("[Персонально]", ChatColor.GOLD),
                            ColorUtil.fail(String.valueOf(ALISA.cooldownsHandler.votesunPersonalCooldowns.getSecondsLeft(playerName)))
                    ), commandSender);
                    personalCooldown = false;
                } else if(!ALISA.cooldownsHandler.votesunGlobalCooldown.isExpired()){
                    ALISA.say(format("%s: Вы сможете запустить голосование через %s сек.",
                            ColorUtil.wrap("[Глобально]", ChatColor.GOLD),
                            ColorUtil.fail(String.valueOf(ALISA.cooldownsHandler.votesunGlobalCooldown.getSecondsLeft()))
                    ), commandSender);
                    globalCooldown = false;
                } else {
                    personalCooldown = true;
                    globalCooldown = true;
                    ALISA.cooldownsHandler.votesunPersonalCooldowns.trigger(commandSender.getName());
                    ALISA.cooldownsHandler.votesunGlobalCooldown.trigger();
                }
                break;
            case DAY:
                if (!ALISA.cooldownsHandler.votedayPersonalCooldowns.isExpired(playerName)) {
                    ALISA.say(format("%s: Вы сможете запустить голосование через %s сек.",
                            ColorUtil.wrap("[Персонально]", ChatColor.GOLD),
                            ColorUtil.fail(String.valueOf(ALISA.cooldownsHandler.votedayPersonalCooldowns.getSecondsLeft(playerName)))
                    ), commandSender);
                    personalCooldown = false;
                } else if(!ALISA.cooldownsHandler.votedayGlobalCooldown.isExpired()){
                    ALISA.say(format("%s: Вы сможете запустить голосование через %s сек.",
                            ColorUtil.wrap("[Глобально]", ChatColor.GOLD),
                            ColorUtil.fail(String.valueOf(ALISA.cooldownsHandler.votedayGlobalCooldown.getSecondsLeft()))
                    ), commandSender);
                    globalCooldown = false;
                } else {
                    personalCooldown = true;
                    globalCooldown = true;
                    ALISA.cooldownsHandler.votedayPersonalCooldowns.trigger(commandSender.getName());
                    ALISA.cooldownsHandler.votedayGlobalCooldown.trigger();
                }
                break;
            default:
                personalCooldown = false;
                globalCooldown = false;
        }
        return personalCooldown && globalCooldown;
    }
}