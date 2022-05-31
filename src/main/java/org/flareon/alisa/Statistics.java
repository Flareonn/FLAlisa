package org.flareon.alisa;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.LinkedHashMap;
import java.util.Map;

public class Statistics implements ConfigurationSerializable {
    public int chatMessages;
    public int answers;
    public int warns;
    public int mutes;
    public int mutesDuration;
    public int totalVotesStarted;
    public int successfulVotes;
    public int modsAndInfCommands;

    public Statistics(final int chatMessages, final int answers, final int warns, final int mutes, final int mutesDuration, final int totalVotesStarted, final int successfulVotes, final int modsAndInfCommands) {
        this.chatMessages = chatMessages;
        this.answers = answers;
        this.warns = warns;
        this.mutes = mutes;
        this.mutesDuration = mutesDuration;
        this.totalVotesStarted = totalVotesStarted;
        this.successfulVotes = successfulVotes;
        this.modsAndInfCommands = modsAndInfCommands;
    }

    public static Statistics deserialize(final Map<String, Object> args) {
        int chatMessages = 0;
        int answers = 0;
        int warns = 0;
        int mutes = 0;
        int mutesDuration = 0;
        int totalVotesStarted = 0;
        int successfulVotes = 0;
        int modsAndInfCommands = 0;
        int onlineSeconds = 0;
        if (args.containsKey("chatMessages")) {
            chatMessages = (int) args.get("chatMessages");
        }
        if (args.containsKey("answers")) {
            answers = (int) args.get("answers");
        }
        if (args.containsKey("warns")) {
            warns = (int) args.get("warns");
        }
        if (args.containsKey("mutes")) {
            mutes = (int) args.get("mutes");
        }
        if (args.containsKey("mutesDuration")) {
            mutesDuration = (int) args.get("mutesDuration");
        }
        if (args.containsKey("totalVotesStarted")) {
            totalVotesStarted = (int) args.get("totalVotesStarted");
        }
        if (args.containsKey("successfulVotes")) {
            successfulVotes = (int) args.get("successfulVotes");
        }
        if (args.containsKey("modsAndInfCommands")) {
            modsAndInfCommands = (int) args.get("modsAndInfCommands");
        }
        return new Statistics(chatMessages, answers, warns, mutes, mutesDuration, totalVotesStarted, successfulVotes, modsAndInfCommands);
    }

    public Map<String, Object> serialize() {
        final LinkedHashMap result = new LinkedHashMap();
        result.put("chatMessages", this.chatMessages);
        result.put("answers", this.answers);
        result.put("warns", this.warns);
        result.put("mutes", this.mutes);
        result.put("mutesDuration", this.mutesDuration);
        result.put("totalVotesStarted", this.totalVotesStarted);
        result.put("successfulVotes", this.successfulVotes);
        result.put("modsAndInfCommands", this.modsAndInfCommands);
        return (Map<String, Object>) result;
    }
}
