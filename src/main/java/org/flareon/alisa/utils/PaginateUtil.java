package org.flareon.alisa.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.util.ChatPaginator;
import org.flareon.alisa.FLAlisa;

import static java.lang.String.format;

public class PaginateUtil extends ChatPaginator {
    private final FLAlisa ALISA;
    private final String lines;
    private CommandSender commandSender;
    public PaginateUtil(String lines) {
        this.ALISA = FLAlisa.getInstance();
        this.lines = lines;
    }

    public void setCommandSender(CommandSender commandSender) {
        this.commandSender = commandSender;
    }

    public void paginate(final String pageNumber) {
        if(isDigit(pageNumber)) {
            paginate(Integer.parseInt(pageNumber));
        }
    }

    public void paginate(final int pageNumber) {
        ChatPaginator.ChatPage chatPage = ChatPaginator.paginate(lines, pageNumber);
        System.out.println(chatPage.getTotalPages() + " " + pageNumber);
        ALISA.say(format("\n-----------Команды-(%s из %s)----------",
                ColorUtil.wrap(String.valueOf(chatPage.getPageNumber()), ChatColor.GOLD),
                ColorUtil.wrap(String.valueOf(chatPage.getTotalPages()), ChatColor.GOLD)
        ), commandSender);
        for (String line : chatPage.getLines() ) {
            commandSender.sendMessage(line);
        }
    }

    private boolean isDigit(String s) throws NumberFormatException {
        try {Integer.parseInt(s); return true;}
        catch (NumberFormatException e) {return false;}
    }
}
