package org.flareon.alisa.utils;

import org.bukkit.command.CommandSender;
import org.flareon.alisa.FLAlisa;

public class ModeratorsUtil {
    private final FLAlisa ALISA;
    public ModeratorsUtil(final FLAlisa ALISA) {
        this.ALISA = ALISA;
    }

    public void modsCommandHandler(final String[] strings, final CommandSender commandSender) {
        if(strings.length == 2) {
            ALISA.sayUnknownCommand(sayTrueUsage(strings), commandSender);
            return;
        }
        switch (strings[1].toLowerCase()) {
            case "add":
                commandAdd(strings, commandSender);
                break;
            case "remove":
                commandRemove(strings, commandSender);
                break;
            case "list":
                ALISA.say(ALISA.moderatorsHandler.getAllModsListString(), commandSender);
                break;
            case "creategroup":
                commandCreateGroup(strings, commandSender);
                break;
            case "editgroup":
                commandEditGroup(strings, commandSender);
                break;
            case "removegroup":
                commandRemoveGroup(strings, commandSender);
                break;
            default:
                ALISA.say("Такой команды не существует. Список моих возможностей: -> /alisa", commandSender);
                break;
        }
    }

    private void commandAdd(final String[] strings, final CommandSender commandSender) {
        final int ID = isDigit(strings[2]);
        if(ID != Integer.MIN_VALUE) {
            final String playerName = strings[3];
            ALISA.say(ALISA.moderatorsHandler.addPlayerToGroup(ID, playerName), commandSender);
        } else {
            ALISA.sayUnknownCommand(sayTrueUsage(strings), commandSender);
        }
    }

    private void commandRemove(final String[] strings, final CommandSender commandSender) {
        final int ID = isDigit(strings[2]);
        if(ID != Integer.MIN_VALUE && strings.length > 3) {
            final String playerName = strings[3];
            ALISA.say(ALISA.moderatorsHandler.removePlayerFromGroup(ID, playerName), commandSender);
        } else {
            ALISA.sayUnknownCommand(sayTrueUsage(strings), commandSender);
        }
    }

    private void commandCreateGroup(final String[] strings, final CommandSender commandSender) {
        final int ID = isDigit(strings[2]);
        if(ID != Integer.MIN_VALUE && strings.length > 5) {
            final String groupName = strings[3];
            final String prefixColor = strings[4];
            final String nameColor = strings[5];
            ALISA.say(ALISA.moderatorsHandler.addGroup(groupName, ID, prefixColor, nameColor), commandSender);
        } else {
            ALISA.sayUnknownCommand(sayTrueUsage(strings), commandSender);
        }
    }

    private void commandEditGroup(final String[] strings, final CommandSender commandSender) {
        final int ID = isDigit(strings[2]);
        System.out.println(strings.length);
        if(ID != Integer.MIN_VALUE && strings.length > 5) {
            final String groupName = strings[3];
            final String prefixColor = strings[4];
            final String nameColor = strings[5];
            ALISA.say(ALISA.moderatorsHandler.editGroup(groupName, ID, prefixColor, nameColor), commandSender);
        } else {
            ALISA.sayUnknownCommand(sayTrueUsage(strings), commandSender);
        }
    }

    private void commandRemoveGroup(final String[] strings, final CommandSender commandSender) {
        final int ID = isDigit(strings[2]);
        if(ID != Integer.MIN_VALUE) {
            ALISA.say(ALISA.moderatorsHandler.removeGroup(ID), commandSender);
        } else {
            ALISA.sayUnknownCommand(sayTrueUsage(strings), commandSender);
        }
    }

    private String sayTrueUsage(final String[] strings) {
        return "\n" + ALISA.getCommand("alisa " + strings[0] + " " + strings[1]).getUsage();
    }

    private int isDigit(final String string) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return Integer.MIN_VALUE;
        }
    }

}