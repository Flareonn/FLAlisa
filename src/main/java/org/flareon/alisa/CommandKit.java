package org.flareon.alisa;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.flareon.alisa.listeners.VoteEvent;
import org.flareon.alisa.utils.ChatUtil;
import org.flareon.alisa.utils.ColorUtil;
import org.flareon.alisa.utils.PaginateUtil;
import org.flareon.alisa.utils.TimeUtil;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static java.lang.String.format;

class CommandMods implements CommandExecutor {
    private final FLAlisa ALISA;

    public CommandMods() {
        this.ALISA = FLAlisa.getInstance();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        ALISA.say(ALISA.moderatorsHandler.getOnlineModsString(), commandSender);
        return true;
    }
}

class CommandAseen implements CommandExecutor {
    private final FLAlisa ALISA;

    public CommandAseen() {
        this.ALISA = FLAlisa.getInstance();
    }

    public boolean onCommand(final CommandSender commandSender, final Command command, final String s, final String[] strings) {
        if (commandSender instanceof Player) {
            if (strings.length >= 1) {
                ALISA.say(this.getSeenString(strings[0]), commandSender);
            } else {
                ALISA.say("Похоже, вы забыли указать имя", commandSender);
            }
        }
        return true;
    }

    private String getSeenString(final String playerName) {
        final Player onlinePlayer = Bukkit.getPlayerExact(playerName);
        final String colorName = ColorUtil.wrap(playerName, ChatColor.GOLD);
        if (ALISA.moderatorsHandler.isModerator(playerName)) {
            return "Извини, но это секрет";
        } else if (onlinePlayer != null && onlinePlayer.isOnline()) {
            return format("Игрок: %s сейчас %s", colorName, ColorUtil.success("онлайн"));
        } else {
            final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
            if (offlinePlayer == null || !offlinePlayer.getName().equalsIgnoreCase(playerName)) {
                return format("Не могу найти игрока с именем %s", colorName);
            }

            final TimeUtil time = new TimeUtil(System.currentTimeMillis() - offlinePlayer.getLastPlayed());
            if (time.getDays() > 1000L) {
                return format("Не могу найти игрока с именем %s", colorName);
            }
            return format("Игрок: %s сейчас\n%s%s", colorName, ColorUtil.fail("Оффлайн"), time.getLog());
        }
    }
}

class CommandServer implements CommandExecutor {
    private final FLAlisa ALISA;
    private final int timeToRestart;

    public CommandServer() {
        ALISA = FLAlisa.getInstance();
        timeToRestart = ALISA.config.getInt("restart-period");
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        final TimeUtil time = new TimeUtil((long) timeToRestart * 60 * 1000 - ManagementFactory.getRuntimeMXBean().getUptime());
        ALISA.say("До рестарта:" + time.getLog(), commandSender);
        return true;
    }
}

class CommandHelp implements CommandExecutor {
    private final PaginateUtil paginateUtil;

    public CommandHelp(String commands) {
        this.paginateUtil = new PaginateUtil(commands);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        this.paginateUtil.setCommandSender(commandSender);
        this.paginateUtil.paginate(String.valueOf(strings.length == 1 ? strings[0] : 1));
        return true;
    }
}

class CommandBot implements CommandExecutor, TabCompleter {
    private static final ArrayList<String> subCommands = new ArrayList<String>() {{
        add("read");
        add("set");
        add("reloadconfig");
        add("mods");
        add("getuuid");
        add("getname");
        add("tospawn");
        add("stats");
    }};
    private static final ArrayList<String> modsSubCommands = new ArrayList<String>() {{
        add("add");
        add("list");
        add("remove");
        add("editgroup");
        add("creategroup");
        add("removegroup");
    }};
    private final FLAlisa ALISA;
    private final PaginateUtil paginateUtil;

    public CommandBot(String commands) {
        ALISA = FLAlisa.getInstance();
        this.paginateUtil = new PaginateUtil(commands);
    }

    private static boolean isDigit(final String s) throws NumberFormatException {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender.isOp()) {
            this.paginateUtil.setCommandSender(commandSender);

            // Если ничего не ввёл - использовать пагинатор
            if (strings.length == 0) {
                this.paginateUtil.paginate(1);
                // Если ввёл число - использовать пагинатор
            } else if (isDigit(strings[0])) {
                this.paginateUtil.paginate(strings[0]);
                return true;
                // Иначе по командам...
            } else {
                switch (strings[0].toLowerCase()) {
                    case "read":
                        commandRead(strings, commandSender);
                        break;
                    case "set":
                        commandSet(strings, commandSender);
                        break;
                    case "reloadconfig":
                        ALISA.registerConfig();
                        ALISA.say("Конфиг перезагружен!", commandSender);
                        break;
                    case "getname":
                        commandGetName(strings, commandSender);
                        break;
                    case "getuuid":
                        commandGetUUID(strings, commandSender);
                        break;
                    case "tospawn":
                        commandToSpawn(strings, commandSender);
                        break;
                    case "tp":
                        commandTp(strings, commandSender);
                        break;
                    case "toggledetect":
                        ALISA.moderatorsHandler.toggleDetect(commandSender);
                        break;
                    case "mods":
                        ALISA.moderatorsUtil.modsCommandHandler(strings, commandSender);
                        break;
                    case "stats":
                        commandStats(commandSender);
                        break;
                    default:
                        ALISA.say("Такой команды не существует. Список моих возможностей: -> /alisa", commandSender);
                        break;
                }
            }
        } else {
            ALISA.say(String.format("%s Вы не обладаете правами администратора!", ColorUtil.fail("[Ошибка доступа]")), commandSender);
        }
        return true;
    }

    private void commandRead(final String[] strings, final CommandSender commandSender) {
        if (strings.length > 1) {
            String configKey = strings[1];
            Object configValue = ALISA.config.getObject(configKey);
            if (configValue instanceof MemorySection) {
                configValue = ((MemorySection) configValue).getKeys(false);
            }

            if (configValue != null) {
                ALISA.say(format("Значение в конфиге поля '%s': %s", ColorUtil.wrap(configKey, ChatColor.LIGHT_PURPLE), ColorUtil.success(configValue.toString())), commandSender);
            } else {
                ALISA.say(format("Поле с таким именем %s", ColorUtil.fail("не найдено")), commandSender);
            }
        } else {
            ALISA.sayUnknownCommand("\n" + ALISA.getCommand("alisa " + strings[0]).getUsage(), commandSender);
        }
    }

    private void commandSet(final String[] strings, final CommandSender commandSender) {
        switch (strings.length) {
            case 1:
                ALISA.say("Введите имя поля, значение которому вы хотите указать!", commandSender);
                break;
            case 2:
                ALISA.say("Введите значение поля!", commandSender);
                break;
            default:
                final String configKey = strings[1];
                final StringBuilder sb = new StringBuilder();
                for (int i = 2; i < strings.length; i++) {
                    sb.append(strings[i]).append(' ');
                }
                final String configValue = sb.toString();
                ALISA.config.set(configKey, isDigit(configValue) ? Integer.parseInt(configValue) : configValue);
                ALISA.say(format("Вы установили поле %s со значением: %s", ColorUtil.wrap(configKey, ChatColor.LIGHT_PURPLE), ColorUtil.wrap(configValue, ChatColor.DARK_PURPLE, ChatColor.BOLD)), commandSender);
                break;
        }
    }

    private void commandGetUUID(final String[] strings, final CommandSender commandSender) {
        if (strings.length != 2) {
            ALISA.say(String.format("%s, укажите ник!", ColorUtil.fail("Ошибка")), commandSender);
            return;
        }
        final OfflinePlayer op = Bukkit.getOfflinePlayer(strings[1]);
        if (op != null) {
            ALISA.say(format("UUID: %s", ColorUtil.wrap(op.getUniqueId().toString(), ChatColor.GOLD)), commandSender);
        } else {
            ALISA.say(format("Игрок с таким именем %s найден", ColorUtil.fail("не")), commandSender);
        }
    }

    private void commandGetName(final String[] strings, final CommandSender commandSender) {
        if (strings.length != 2) {
            ALISA.say(String.format("%s, укажите UUID!", ColorUtil.fail("Ошибка")), commandSender);
            return;
        }

        final String uuidInString = strings[1];
        final OfflinePlayer op;
        try {
            UUID uuid = UUID.fromString(uuidInString);
            op = Bukkit.getOfflinePlayer(uuid);
        } catch (Exception e) {
            ALISA.say(format("%s, неверный UUID", ColorUtil.fail("Ошибка")), commandSender);
            return;
        }
        if (op != null && op.getName() != null) {
            ALISA.say(format("Игрок с этим UUID: %s", ColorUtil.wrap(op.getName(), ChatColor.GOLD)), commandSender);
        } else {
            ALISA.say(format("Игрок с этим UUID %s найден", ColorUtil.fail("не")), commandSender);
        }
    }

    private void commandToSpawn(final String[] strings, final CommandSender commandSender) {
        if (strings.length != 2) {
            ALISA.say(String.format("%s, укажите ник!", ColorUtil.fail("Ошибка")), commandSender);
            return;
        }

        final String playerName = strings[1];
        final ArrayList<String> toSpawnPlayerNames = this.ALISA.config.getList("tospawn-playernames");
        if (!toSpawnPlayerNames.contains(playerName)) {
            OfflinePlayer op = Bukkit.getOfflinePlayer(playerName);
            if (op != null) {
                toSpawnPlayerNames.add(playerName);
                ALISA.config.set("tospawn-playernames", toSpawnPlayerNames);
                ALISA.say("Игрок будет отправлен на спавн", commandSender);
            } else {
                ALISA.say(String.format("Игрок с таким именем %s найден", ColorUtil.fail("не")), commandSender);
            }
        } else {
            ALISA.say(String.format("Игрок %s находится в списке на телепортацию", ColorUtil.fail("уже")), commandSender);
        }
    }

    private void commandTp(final String[] strings, final CommandSender commandSender) {
        if (strings.length != 2) {
            ALISA.say(String.format("%s, укажите ник!", ColorUtil.fail("Ошибка")), commandSender);
            return;
        }

        final Player player = Bukkit.getPlayer(strings[1]);
        final Player iniciator = (Player) commandSender;
        if (player != null) {
            iniciator.teleport(player.getLocation());
            ALISA.say(format("Игрок %s, телепортирую...", ColorUtil.success("найден")), commandSender);
        } else {
            ALISA.say(String.format("Игрок %s найден", ColorUtil.fail("не")), commandSender);
        }
    }

    private void commandStats(final CommandSender commandSender) {
        final StringBuilder sb = new StringBuilder("\n---------------Статистика--------------\n");
        sb.append(String.format("%s %d\n", ColorUtil.wrap("Сообщений в чат:", ChatColor.GOLD), ALISA.statistics.chatMessages));
        sb.append(String.format("%s %d\n", ColorUtil.wrap("Ответов на вопросы:", ChatColor.GOLD), ALISA.statistics.answers));
        sb.append(String.format("%s %d\n", ColorUtil.wrap("Выдано варнов:", ChatColor.GOLD), ALISA.statistics.warns));
        sb.append(String.format("%s %d\n", ColorUtil.wrap("Выдано мутов:", ChatColor.GOLD), ALISA.statistics.mutes));
        sb.append(String.format("%s %.2f\n", ColorUtil.wrap("Общая длительность мутов в минутах:", ChatColor.GOLD), (ALISA.statistics.mutesDuration / 60.0f)));
        sb.append(String.format("%s %d\n", ColorUtil.wrap("Начато голосований:", ChatColor.GOLD), ALISA.statistics.totalVotesStarted));
        sb.append(String.format("%s %d\n", ColorUtil.wrap("Успешных голосований:", ChatColor.GOLD), ALISA.statistics.successfulVotes));
        sb.append(String.format("%s %d\n", ColorUtil.wrap("Команд /mods и /inf:", ChatColor.GOLD), ALISA.statistics.modsAndInfCommands));
        ALISA.say(String.valueOf(sb), commandSender);
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender.isOp()) {
            List<String> result = new ArrayList<>();
            switch (strings.length) {
                case 1:
                    subCommands.forEach(w -> {
                        if (w.contains(strings[0])) result.add(w);
                    });
                    return result;
                case 2:
                    if (strings[0].equalsIgnoreCase("read") || strings[0].equalsIgnoreCase("set")) {
                        ALISA.config.getKeys().forEach(w -> {
                            if (w.contains(strings[1])) result.add(w);
                        });
                        return result;
                    } else if (strings[0].equalsIgnoreCase("mods")) {
                        modsSubCommands.forEach(w -> {
                            if (w.contains(strings[1])) result.add(w);
                        });
                        return result;
                    }
                    break;
            }
        }
        return null;
    }
}

class CommandColors implements CommandExecutor {
    private static final FLAlisa ALISA = FLAlisa.getInstance();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        TextComponent message = new TextComponent("/yes");
        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/yes"));

        BaseComponent component = message;
        commandSender.spigot().sendMessage(component);

        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            ALISA.say("Вы можете наблюдать доступные цвета и модификаторы:\n"
                    + "-- Цвета:\n" + ChatColor.BLACK + "0 " + ChatColor.DARK_BLUE + "1 " + ChatColor.DARK_GREEN + "2 " +
                    ChatColor.DARK_AQUA + "3 " + ChatColor.DARK_RED + "4 " + ChatColor.DARK_PURPLE + "5 " +
                    ChatColor.GOLD + "6 " + ChatColor.GRAY + "7 " + ChatColor.DARK_GRAY + "8 " +
                    ChatColor.BLUE + "9 " + ChatColor.GREEN + "a " + ChatColor.AQUA + "b " +
                    ChatColor.RED + "c " + ChatColor.LIGHT_PURPLE + "d " + ChatColor.YELLOW + "e " +
                    ChatColor.WHITE + "f\n" +
                    ChatColor.YELLOW + "-- Модификаторы: " + ChatColor.WHITE + "\n \u00A7ll\u00A7r \u00A7oo\u00A7r \u00A7nn\u00A7r \u00A7mm\u00A7r \u00A7kk\u00A7r(k)", player);
        }
        // If the player (or console) uses our command correct, we can return true
        return true;
    }
}

class CommandVotesun implements CommandExecutor {
    private static FLAlisa ALISA;

    public CommandVotesun() {
        ALISA = FLAlisa.getInstance();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!Bukkit.getWorld("world").isClearWeather()) {
            new VoteEvent(VoteEvent.TypeVote.SUN, commandSender);
        } else {
            ALISA.say("Сейчас ясная погода!", commandSender);
        }
        return true;
    }
}

class CommandVoteday implements CommandExecutor {
    private static final FLAlisa ALISA = FLAlisa.getInstance();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (isDay()) {
            ALISA.say("Сейчас день!", commandSender);
        } else {
            new VoteEvent(VoteEvent.TypeVote.DAY, commandSender);
        }
        return true;
    }

    private boolean isDay() {
        long time = Bukkit.getServer().getWorld("world").getTime();
        return time < 12300 || time > 23850;
    }
}

class CommandRules implements CommandExecutor, TabCompleter {
    private static final FLAlisa ALISA = FLAlisa.getInstance();
    private static final ArrayList<String> rulesInPoints = new ArrayList<>();
    private static JsonObject rules;

    static {
        try {
            rules = new JsonParser().parse(new FileReader(ALISA.config.getRulesFile())).getAsJsonObject().getAsJsonObject("rules");
            rules.getAsJsonObject().entrySet().forEach(w -> rulesInPoints.add(w.getKey()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length != 0) {
            String pointRule = strings[0];
            JsonObject rule = rules.getAsJsonObject(pointRule);

            if (rule == null) {
                ALISA.say("Такого правила нет в сводках", commandSender);
            } else {
                String punishment = safelyGetFromJson("punishment", rule);
                String content = safelyGetFromJson("content", rule);
                ++ALISA.statistics.modsAndInfCommands;
                ALISA.say(
                        format("%s\n%s\n" +
                                        (!punishment.equals("") ? ChatColor.RED + "" + ChatColor.UNDERLINE + "Наказание:" + ChatColor.RESET + "" + ChatColor.RED + " %s" : ""),
                                "----------------" + pointRule + "----------------",
                                content,
                                punishment
                        ),
                        commandSender);
            }
        } else {
            ALISA.sayUnknownCommand("\n" + ALISA.getCommand("inf").getUsage(), commandSender);
        }
        return true;
    }

    private String safelyGetFromJson(String memberName, JsonObject jsonObject) {
        JsonElement member = jsonObject.get(memberName);
        if (member != null) {
            return new String(member.getAsString().getBytes(), StandardCharsets.UTF_8);
        } else {
            return "";
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 1) {
            List<String> result = new ArrayList<>();
            rulesInPoints.forEach(w -> {
                if (w.toLowerCase().startsWith(strings[0])) result.add(w);
            });
            return result;
        }
        return null;
    }
}

class VoteCommand implements CommandExecutor {
    private final boolean isYes;

    public VoteCommand(boolean isYes) {
        this.isYes = isYes;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Only players can run this command !");
            return false;
        }

        VoteEvent.setVote(isYes, commandSender);
        return true;
    }
}

class CommandMouth implements CommandExecutor {
    private final FLAlisa ALISA;

    public CommandMouth() {
        this.ALISA = FLAlisa.getInstance();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender.isOp()) {
            ALISA.broadcast(String.join(" ", strings));
            return true;
        }
        ALISA.say(String.format("%s Вы не обладаете правами администратора!", ColorUtil.fail("[Ошибка доступа]")), commandSender);
        return true;
    }
}

class CommandShare implements CommandExecutor, TabCompleter {
    private final FLAlisa ALISA;
    private final List<String> TYPE_SHARE = new ArrayList<String>() {{
        add("command");
        add("link");
    }};

    public CommandShare() {
        this.ALISA = FLAlisa.getInstance();
    }

    private String[] toTwoInputByPattern(final String pattern, final String[] args) throws Exception {
        List<String> listInput = Arrays.asList(args);
        listInput = listInput.subList(1, listInput.size());
        if (listInput.stream().noneMatch(s -> s.startsWith(pattern))) {
            throw new Exception("В вашей команде нет: \"" + pattern + "\"");
        }

        final String[] inputs = String.join(" ", listInput).split(pattern, 2);
        if (inputs[0].isEmpty()) {
            throw new Exception("Тут нет названия!");
        }
        inputs[1] = pattern + inputs[1].replaceAll(pattern, "");

        return inputs;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final String playerName = sender.getName();
        if (!ALISA.cooldownsHandler.shareCooldowns.isExpired(playerName)) {
            ALISA.say(
                    String.format("%s %s %s",
                            "Ты не можешь использовать эту команду еще:",
                            ColorUtil.fail(String.valueOf(ALISA.cooldownsHandler.shareCooldowns.getSecondsLeft(playerName))),
                            "секунд"),
                    sender
            );
            return true;
        }
        ComponentBuilder componentBuilder = new ComponentBuilder()
                .append(ChatUtil.ALISA_TAG)
                .append(ChatUtil.text(sender.getName(), net.md_5.bungee.api.ChatColor.GOLD))
                .append(ChatUtil.text(" поделился "));
        if (args.length > 2) {
            String[] input;
            switch (args[0].toLowerCase()) {
                case "command":
                    try {
                        input = toTwoInputByPattern("/", args);

                        final TextComponent componentCommand = ChatUtil.textCommand(input[0], input[1]);
                        componentCommand.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, input[1]));
                        componentCommand.setColor(net.md_5.bungee.api.ChatColor.GOLD);

                        componentBuilder.append("командой: ").append(componentCommand);
                    } catch (Exception e) {
                        ALISA.sayUnknownCommand(e.getMessage(), sender);
                        return true;
                    }
                    break;
                case "link":
                    try {
                        input = toTwoInputByPattern("http", args);
                        componentBuilder.append("ссылкой: ").append(ChatUtil.textLink(input[0], input[1]));
                    } catch (Exception e) {
                        ALISA.sayUnknownCommand(e.getMessage(), sender);
                        return true;
                    }
                    break;
            }
            ALISA.cooldownsHandler.shareCooldowns.trigger(playerName);
            ALISA.broadcast(componentBuilder.create());
        } else {
            ALISA.sayUnknownCommand("Введите дополнительные аргументы!", sender);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        switch (strings.length) {
            case 1:
                List<String> result = new ArrayList<>();
                TYPE_SHARE.forEach(w -> {
                    if (w.toLowerCase().startsWith(strings[0])) result.add(w);
                });
                return result;
            case 2:
                return new ArrayList<String>() {{
                    add("Название");
                }};
            case 3:
                switch (strings[0].toLowerCase()) {
                    case "link":
                        return new ArrayList<String>() {{
                            add("Ссылка");
                        }};
                    case "command":
                        return new ArrayList<String>() {{
                            add("Команда");
                        }};
                }
        }
        return null;
    }
}