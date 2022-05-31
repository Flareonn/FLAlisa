package org.flareon.alisa;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.matcher.NodeMatcher;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.flareon.alisa.utils.ColorUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class ModeratorsHandler {
    public ArrayList<ModeratorsEntry> groups;
    protected ArrayList<String> hiddenModerators;
    FLAlisa ALISA;

    public ModeratorsHandler(final FLAlisa ALISA) {
        this.groups = new ArrayList<>();
        this.hiddenModerators = new ArrayList<>();
        this.ALISA = ALISA;
        this.loadModeratorsConfig();
    }

    private String createGroup(final ModeratorsEntry moderatorsEntry) {
        this.groups.add(moderatorsEntry);
        Collections.sort(this.groups);
        this.saveModeratorsConfig();
        return String.format("Группа: [%s]: %s %s создана!", ColorUtil.wrap(String.valueOf(moderatorsEntry.ID), ChatColor.GOLD), ColorUtil.wrap(moderatorsEntry.groupName, moderatorsEntry.prefixColor), ColorUtil.success("успешно"));
    }

    public String removeGroup(final int ID) {
        final ModeratorsEntry toRemove = this.getGroupByID(ID);
        if (toRemove != null) {
            this.groups.remove(toRemove);
            this.saveModeratorsConfig();
            return String.format("Группа с ID %s была %s удалена", ColorUtil.wrap(String.valueOf(ID), ChatColor.GOLD), ColorUtil.success("успешно"));
        }
        return String.format("Группа с ID %s %s найдена!", ColorUtil.wrap(String.valueOf(ID), ChatColor.GOLD), ColorUtil.fail("не"));
    }

    public String editGroup(final String newGroupName, final int ID, final String newPrefixColor, final String newNameColor) {
        final ModeratorsEntry me = this.getGroupByID(ID);
        if (me != null) {
            me.groupName = newGroupName;
            me.prefixColor = newPrefixColor;
            me.playerNameColor = newNameColor;
            this.saveModeratorsConfig();
            Collections.sort(this.groups);
            return String.format("Группа с ID %s была %s обновлена", ColorUtil.wrap(String.valueOf(ID), ChatColor.GOLD), ColorUtil.success("успешно"));
        }
        return String.format("Группа с ID %s %s найдена!", ColorUtil.wrap(String.valueOf(ID), ChatColor.GOLD), ColorUtil.fail("не"));
    }

    private void removePlayerFromAllGroupsExceptOneSilent(final String playerName, final int exceptionGroupID) {
        for (final ModeratorsEntry group : this.groups) {
            if (group.ID != exceptionGroupID) {
                group.removePlayerByName(playerName);
            }
        }
    }

    public String addGroup(final String groupName, final int ID, final String prefixColor, final String nameColor) {
        if (this.getGroupByID(ID) == null && !this.groupWithThisNameExists(groupName)) {
            final ModeratorsEntry me = new ModeratorsEntry(ID, groupName, prefixColor, nameColor);
            return this.createGroup(me);
        }
        return String.format("Группа с именем %s или ID %s уже существует!", ColorUtil.wrap(groupName, ChatColor.GOLD), ColorUtil.wrap(String.valueOf(ID), ChatColor.GOLD));
    }

    public String getAllModsListString() {
        final StringBuilder sb = new StringBuilder("Список модераторов по группам:");
        for (ModeratorsEntry group : this.groups) {
            sb.append("\n").append(group.getModListString());
        }
        return sb.toString();
    }

    public boolean isModerator(final String playerName) {
        for (final ModeratorsEntry me : this.groups) {
            for (final String name : me.playerNames) {
                if (name.equalsIgnoreCase(playerName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void toggleDetect(final CommandSender commandSender) {
        final String playerName = commandSender.getName();
        if (this.isModerator(playerName)) {
            if (hiddenModerators.contains(playerName)) {
                hiddenModerators.remove(playerName);
                ALISA.say(String.format("Теперь тебя %s в списке онлайн модераторов", ColorUtil.success("видно")), commandSender);
            } else {
                hiddenModerators.add(playerName);
                ALISA.say(String.format("Теперь тебя %s в списке онлайн модераторов", ColorUtil.fail("не видно")), commandSender);
            }
        } else {
            ALISA.say(String.format("Тебя %s в этой группе", ColorUtil.fail("нет")), commandSender);
        }
    }

    public String addPlayerToGroup(final int ID, final String playerName) {
        final OfflinePlayer op = Bukkit.getOfflinePlayer(playerName);
        if (op == null || op.getName() == null) {
            return String.format("Игрок %s %s найден", ColorUtil.wrap(playerName, ChatColor.GOLD), ColorUtil.fail("не"));
        }
        final String opName = op.getName();
        final ModeratorsEntry group = this.getGroupByID(ID);
        if (group != null) {
            final String response = group.addPlayerByName(opName);
            this.saveModeratorsConfig();
            return response;
        }
        return String.format("Группа с ID %s %s найдена!", ColorUtil.wrap(String.valueOf(ID), ChatColor.GOLD), ColorUtil.fail("не"));
    }

    public String removePlayerFromGroup(final int ID, final String playerName) {
        for (ModeratorsEntry group : groups) {
            if (group.ID == ID) {
                group.removePlayerByName(playerName);
                this.saveModeratorsConfig();
                return String.format("Игрок %s %s удалён из группы: [%s] %s", ColorUtil.wrap(playerName, group.playerNameColor), ColorUtil.success("успешно"), ColorUtil.wrap(String.valueOf(group.ID), ChatColor.GOLD), ColorUtil.wrap(group.groupName, group.prefixColor));
            }
        }
        return String.format("Игрок %s %s входит в группу c ID: %s", ColorUtil.wrap(playerName, ChatColor.GOLD), ColorUtil.fail("не"), ColorUtil.wrap(String.valueOf(ID), ChatColor.GOLD));
    }

    public String getOnlineModsString() {
        final StringBuilder sb = new StringBuilder("Список модераторов онлайн:");
        ++ALISA.statistics.modsAndInfCommands;
        for (ModeratorsEntry group : this.groups) {
            String onlinePlayersString = group.getOnlinePlayersString();
            if (!onlinePlayersString.equals("")) {
                sb.append("\n").append(group.getOnlinePlayersString());
            }
        }
        return sb.toString();
    }

    public boolean isModeratorHidden(final String name) {
        for (final String hiddenName : this.hiddenModerators) {
            if (hiddenName.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }


    public ModeratorsEntry getGroupByID(final int ID) {
        for (final ModeratorsEntry me : this.groups) {
            if (me.ID == ID) {
                return me;
            }
        }
        return null;
    }

    private boolean groupWithThisNameExists(final String name) {
        for (final ModeratorsEntry me : this.groups) {
            if (me.groupName.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    private void saveModeratorsConfig() {
        this.ALISA.moderators.setCustom("moderators", groups);
    }

    private CompletableFuture<List<User>> getUsersInGroup(String groupName) {
        LuckPerms api = ALISA.LuckAPI;
        NodeMatcher<InheritanceNode> matcher = NodeMatcher.key(InheritanceNode.builder(groupName).build());
        return api.getUserManager().searchAll(matcher).thenComposeAsync(results -> {
            List<CompletableFuture<User>> users = new ArrayList<>();
            return CompletableFuture.allOf(
                    results.keySet().stream()
                            .map(uuid -> api.getUserManager().loadUser(uuid))
                            .peek(users::add)
                            .toArray(CompletableFuture[]::new)
            ).thenApply(x -> users.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList())
            );
        });
    }

    private void loadModeratorsConfig() {
        if (ALISA.LuckAPI != null) {
            int id = 1;
            for (String staff : ALISA.config.getList("staff")) {
                List<String> playerNames;

                final String[] code = staff.split(" ");
                final String luckyPermName = code[0];
                final String groupName = code[1];
                final String prefixColor = code[2];
                final String playerNameColor = code[3];

                try {
                    playerNames = getUsersInGroup(luckyPermName).get().stream()
                            .map(user -> Bukkit.getOfflinePlayer(user.getUniqueId()).getName())
                            .collect(Collectors.toList());
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
                this.groups.add(new ModeratorsEntry(id++, groupName, new ArrayList<>(playerNames), prefixColor, playerNameColor));
            }
            saveModeratorsConfig();
        } else {
            this.groups = (ArrayList<ModeratorsEntry>) this.ALISA.moderators.getObject("moderators");
        }
    }
}
