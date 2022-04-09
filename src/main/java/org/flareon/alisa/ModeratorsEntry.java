package org.flareon.alisa;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.flareon.alisa.utils.ColorUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

@SerializableAs("ModeratorsEntry")
public class ModeratorsEntry implements Comparable<ModeratorsEntry>, ConfigurationSerializable {
    public int ID;
    public String groupName;
    public String prefixColor;
    public String playerNameColor;
    public ArrayList<String> playerNames;
    @Override
    public int compareTo(ModeratorsEntry o) {
        return o.ID - this.ID;
    }

    public ModeratorsEntry(final int ID, final String groupName, final String prefixColor, final String playerNameColor) {
        this.playerNames = new ArrayList<>();
        this.ID = ID;
        this.groupName = groupName;
        this.prefixColor = prefixColor;
        this.playerNameColor = playerNameColor;
    }

    protected ModeratorsEntry(final int ID, final String groupName, final ArrayList<String> playerNames, final String prefixColor, final String playerNameColor) {
        this.playerNames = new ArrayList<>();
        this.ID = ID;
        this.groupName = groupName;
        this.prefixColor = prefixColor;
        this.playerNameColor = playerNameColor;
        this.playerNames = playerNames;
    }

    private String getPlayerListString() {
        final ArrayList<String> coloredMods = new ArrayList<>();
        for (final String name : this.playerNames) {
            coloredMods.add(String.format("%s", ColorUtil.wrap(name, this.playerNameColor)));
        }
        return String.join(", ", coloredMods);
    }

    public String getModListString() {
        return String.format("[%s] [%s]: %s", ColorUtil.wrap(String.valueOf(this.ID), ChatColor.GOLD), ColorUtil.wrap(this.groupName, this.prefixColor), this.getPlayerListString());
    }

    public String addPlayerByName(final String playerName) {
        if(!this.hasPlayer(playerName)) {
            this.playerNames.add(playerName);
            return String.format("Игрок %s %s добавлен в группу", ColorUtil.wrap(playerName, ChatColor.GOLD), ColorUtil.success("успешно"));
        }
        return String.format("Игрок %s %s находится в группе", ColorUtil.wrap(playerName, ChatColor.GOLD), ColorUtil.fail("уже"));
    }

    public void removePlayerByName(final String playerName) {
        if(this.hasPlayer(playerName)) {
            this.removeIgnoreCase(playerName, this.playerNames);
        }
    }

    private void removeIgnoreCase(final String playerName, final ArrayList<String> arr) {
        String toRemove = null;
        for (final String s : arr) {
            if (s.equalsIgnoreCase(playerName)) {
                toRemove = s;
                break;
            }
        }
        if (toRemove != null) {
            arr.remove(toRemove);
        }
    }

    public boolean hasPlayer(final String playerName) {
        for (String name : this.playerNames) {
            if(name.equalsIgnoreCase(playerName)) {
                return true;
            }
        }
        return false;
    }

    private boolean isPlayerNameOnline(final String playerName) {
        final Player p = Bukkit.getPlayer(playerName);
        return (p != null && p.isOnline());
    }

    public String getFormattedGroupName() {
        return String.format("%s[%s]", ColorUtil.text, ColorUtil.wrap(this.groupName, this.prefixColor));
    }

    public String getOnlinePlayersString() {
        final StringBuilder sb = new StringBuilder();
        final ArrayList<String> response = new ArrayList<>();
        for (String playerName : this.playerNames) {
            if (!FLAlisa.getInstance().moderatorsHandler.isModeratorHidden(playerName) && this.isPlayerNameOnline(playerName)) {
                response.add(String.format("%s %s %s", ColorUtil.success("Онлайн"), this.getFormattedGroupName(), ColorUtil.wrap(playerName, this.playerNameColor)));
            }
        }
        sb.append(String.join("\n", response));
        return sb.toString();
    }

    @Override
    public Map<String, Object> serialize() {
        LinkedHashMap map = new LinkedHashMap();
        map.put("groupName", this.groupName);
        map.put("prefixColor", this.prefixColor);
        map.put("playerNameColor", this.playerNameColor);
        map.put("ID", this.ID);
        map.put("playerNames", this.playerNames);
        return map;
    }

    public static ModeratorsEntry deserialize(final Map<String, Object> map) {
        String groupName = "";
        String prefixColor = "";
        String playernameColor = "";
        ArrayList<String> playerNames = new ArrayList<>();
        int ID = 0;
        if (map.containsKey("groupName")) {
            groupName = map.get("groupName").toString();
        }
        if (map.containsKey("prefixColor")) {
            prefixColor = map.get("prefixColor").toString();
        }
        if (map.containsKey("playerNameColor")) {
            playernameColor = map.get("playerNameColor").toString();
        }
        if (map.containsKey("playerNames")) {
            playerNames = (ArrayList<String>) map.get("playerNames");
        }
        if (map.containsKey("ID")) {
            ID = (int) map.get("ID");
        }
        return new ModeratorsEntry(ID, groupName, playerNames, prefixColor, playernameColor);
    }
}
