[33mcommit 69eac9d4e6f1e89383367b2276671cd42f0f334c[m[33m ([m[1;36mHEAD -> [m[1;32mmain[m[33m)[m
Author: Flareonn <appavel2011@yandex.ru>
Date:   Wed Apr 13 00:54:18 2022 +0500

    Переименовал метод вещания на весь сервер

[1mdiff --git a/src/main/java/org/flareon/alisa/CommandKit.java b/src/main/java/org/flareon/alisa/CommandKit.java[m
[1mindex ef0f4f5..38d44e3 100644[m
[1m--- a/src/main/java/org/flareon/alisa/CommandKit.java[m
[1m+++ b/src/main/java/org/flareon/alisa/CommandKit.java[m
[36m@@ -30,7 +30,7 @@[m [mclass CommandMods implements CommandExecutor {[m
 [m
     @Override[m
     public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {[m
[31m-        ALISA.say(ALISA.moderatorsHandler.getOnlineModsString(), commandSender);[m
[32m+[m[32m        ALISA.broadcast(ALISA.moderatorsHandler.getOnlineModsString(), commandSender);[m
         return true;[m
     }[m
 }[m
[36m@@ -45,9 +45,9 @@[m [mclass CommandAseen implements CommandExecutor {[m
     public boolean onCommand(final CommandSender commandSender, final Command command, final String s, final String[] strings) {[m
         if (commandSender instanceof Player) {[m
             if (strings.length >= 1) {[m
[31m-                this.ALISA.say(this.getSeenString(strings[0]), commandSender);[m
[32m+[m[32m                this.ALISA.broadcast(this.getSeenString(strings[0]), commandSender);[m
             } else {[m
[31m-                this.ALISA.say("Похоже, вы забыли указать имя", commandSender);[m
[32m+[m[32m                this.ALISA.broadcast("Похоже, вы забыли указать имя", commandSender);[m
             }[m
         }[m
         return true;[m
[36m@@ -85,7 +85,7 @@[m [mclass CommandServer implements CommandExecutor {[m
     @Override[m
     public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {[m
         final TimeUtil time = new TimeUtil((long) timeToRestart * 60 * 1000 - ManagementFactory.getRuntimeMXBean().getUptime());[m
[31m-        ALISA.say("До рестарта:" + time.getLog(), commandSender);[m
[32m+[m[32m        ALISA.broadcast("До рестарта:" + time.getLog(), commandSender);[m
         return true;[m
     }[m
 }[m
[36m@@ -153,7 +153,7 @@[m [mclass CommandBot implements CommandExecutor, TabCompleter {[m
                         break;[m
                     case "reloadconfig":[m
                         ALISA.registerConfig();[m
[31m-                        ALISA.say("Конфиг перезагружен!", commandSender);[m
[32m+[m[32m                        ALISA.broadcast("Конфиг перезагружен!", commandSender);[m
                         break;[m
                     case "getname":[m
                         commandGetName(strings, commandSender);[m
[36m@@ -174,12 +174,12 @@[m [mclass CommandBot implements CommandExecutor, TabCompleter {[m
                         ALISA.moderatorsUtil.modsCommandHandler(strings, commandSender);[m
                         break;[m
                     default:[m
[31m-                        ALISA.say("Такой команды не существует. Список моих возможностей: -> /alisa", commandSender);[m
[32m+[m[32m                        ALISA.broadcast("Такой команды не существует. Список моих возможностей: -> /alisa", commandSender);[m
                         break;[m
                 }[m
             }[m
         } else {[m
[31m-            ALISA.say(String.format("%s Вы не обладаете правами администратора!", ColorUtil.fail("[Ошибка доступа]")), commandSender);[m
[32m+[m[32m            ALISA.broadcast(String.format("%s Вы не обладаете правами администратора!", ColorUtil.fail("[Ошибка доступа]")), commandSender);[m
         }[m
         return true;[m
     }[m
[36m@@ -193,9 +193,9 @@[m [mclass CommandBot implements CommandExecutor, TabCompleter {[m
             }[m
 [m
             if(configValue != null) {[m
[31m-                ALISA.say(format("Значение в конфиге поля '%s': %s", ColorUtil.wrap(configKey, ChatColor.LIGHT_PURPLE), ColorUtil.success(configValue.toString())), commandSender);[m
[32m+[m[32m                ALISA.broadcast(format("Значение в конфиге поля '%s': %s", ColorUtil.wrap(configKey, ChatColor.LIGHT_PURPLE), ColorUtil.success(configValue.toString())), commandSender);[m
             } else {[m
[31m-                ALISA.say(format("Поле с таким именем %s", ColorUtil.fail("не найдено")), commandSender);[m
[32m+[m[32m                ALISA.broadcast(format("Поле с таким именем %s", ColorUtil.fail("не найдено")), commandSender);[m
             }[m
         } else {[m
             ALISA.sayUnknownCommand("\n" + ALISA.getCommand("alisa " + strings[0]).getUsage() , commandSender);[m
[36m@@ -205,16 +205,16 @@[m [mclass CommandBot implements CommandExecutor, TabCompleter {[m
     private void commandSet(final String[] strings, final CommandSender commandSender) {[m
         switch (strings.length) {[m
             case 1:[m
[31m-                ALISA.say("Введите имя поля, значение которому вы хотите указать!", commandSender);[m
[32m+[m[32m                ALISA.broadcast("Введите имя поля, значение которому вы хотите указать!", commandSender);[m
                 break;[m
             case 2:[m
[31m-                ALISA.say("Введите значение поля!", commandSender);[m
[32m+[m[32m                ALISA.broadcast("Введите значение поля!", commandSender);[m
                 break;[m
             case 3:[m
                 final String configKey = strings[1];[m
                 final String configValue = strings[2];[m
                 ALISA.config.set(configKey, isDigit(configValue) ? Integer.parseInt(configValue) : configValue);[m
[31m-                ALISA.say(format("Вы установили поле %s со значением: %s", ColorUtil.wrap(configKey, ChatColor.LIGHT_PURPLE), ColorUtil.wrap(configValue, ChatColor.DARK_PURPLE, ChatColor.BOLD)), commandSender);[m
[32m+[m[32m                ALISA.broadcast(format("Вы установили поле %s со значением: %s", ColorUtil.wrap(configKey, ChatColor.LIGHT_PURPLE), ColorUtil.wrap(configValue, ChatColor.DARK_PURPLE, ChatColor.BOLD)), commandSender);[m
                 break;[m
             default:[m
                 ALISA.sayUnknownCommand("\n" + ALISA.getCommand("alisa " + strings[0]).getUsage() , commandSender);[m
[36m@@ -224,20 +224,20 @@[m [mclass CommandBot implements CommandExecutor, TabCompleter {[m
 [m
     private void commandGetUUID(final String[] strings, final CommandSender commandSender) {[m
         if(strings.length != 2) {[m
[31m-            ALISA.say(String.format("%s, укажите ник!", ColorUtil.fail("Ошибка")), commandSender);[m
[32m+[m[32m            ALISA.broadcast(String.format("%s, укажите ник!", ColorUtil.fail("Ошибка")), commandSender);[m
             return;[m
         }[m
         final OfflinePlayer op = Bukkit.getOfflinePlayer(strings[1]);[m
         if(op != null) {[m
[31m-            ALISA.say(format("UUID: %s", ColorUtil.wrap(op.getUniqueId().toString(), ChatColor.GOLD)), commandSender);[m
[32m+[m[32m            ALISA.broadcast(format("UUID: %s", ColorUtil.wrap(op.getUniqueId().toString(), ChatColor.GOLD)), commandSender);[m
         } else {[m
[31m-            ALISA.say(format("Игрок с таким именем %s найден", ColorUtil.fail("не")), commandSender);[m
[32m+[m[32m            ALISA.broadcast(format("Игрок с таким именем %s найден", ColorUtil.fail("не")), commandSender);[m
         }[m
     }[m
 [m
     private void commandGetName(final String[] strings, final CommandSender commandSender) {[m
         if(strings.length != 2) {[m
[31m-            ALISA.say(String.format("%s, укажите UUID!", ColorUtil.fail("Ошибка")), commandSender);[m
[32m+[m[32m            ALISA.broadcast(String.format("%s, укажите UUID!", ColorUtil.fail("Ошибка")), commandSender);[m
             return;[m
         }[m
 [m
[36m@@ -247,19 +247,19 @@[m [mclass CommandBot implements CommandExecutor, TabCompleter {[m
             UUID uuid = UUID.fromString(uuidInString);[m
             op = Bukkit.getOfflinePlayer(uuid);[m
         } catch (Exception e) {[m
[31m-            ALISA.say(format("%s, неверный UUID", ColorUtil.fail("Ошибка")), commandSender);[m
[32m+[m[32m            ALISA.broadcast(format("%s, неверный UUID", ColorUtil.fail("Ошибка")), commandSender);[m
             return;[m
         }[m
         if(op != null && op.getName() != null) {[m
[31m-            ALISA.say(format("Игрок с этим UUID: %s", ColorUtil.wrap(op.getName(), ChatColor.GOLD)), commandSender);[m
[32m+[m[32m            ALISA.broadcast(format("Игрок с этим UUID: %s", ColorUtil.wrap(op.getName(), ChatColor.GOLD)), commandSender);[m
         } else {[m
[31m-            ALISA.say(format("Игрок с этим UUID %s найден", ColorUtil.fail("не")), commandSender);[m
[32m+[m[32m            ALISA.broadcast(format("Игрок с этим UUID %s найден", ColorUtil.fail("не")), commandSender);[m
         }[m
     }[m
 [m
     private void commandToSpawn(final String[] strings, final CommandSender commandSender) {[m
         if(strings.length != 2) {[m
[31m-            ALISA.say(String.format("%s, укажите ник!", ColorUtil.fail("Ошибка")), commandSender);[m
[32m+[m[32m            ALISA.broadcast(String.format("%s, укажите ник!", ColorUtil.fail("Ошибка")), commandSender);[m
             return;[m
         }[m
 [m
[36m@@ -270,18 +270,18 @@[m [mclass CommandBot implements CommandExecutor, TabCompleter {[m
             if (op != null) {[m
                 toSpawnPlayerNames.add(playerName);[m
                 ALISA.config.set("tospawn-playernames", toSpawnPlayerNames);[m
[31m-                ALISA.say("Игрок будет отправлен на спавн", commandSender);[m
[32m+[m[32m                ALISA.broadcast("Игрок будет отправлен на спавн", commandSender);[m
             } else {[m
[31m-                ALISA.say(String.format("Игрок с таким именем %s найден", ColorUtil.fail("не")), commandSender);[m
[32m+[m[32m                ALISA.broadcast(String.format("Игрок с таким именем %s найден", ColorUtil.fail("не")), commandSender);[m
             }[m
         } else {[m
[31m-            ALISA.say(String.format("Игрок %s находится в списке на телепортацию", ColorUtil.fail("уже")), commandSender);[m
[32m+[m[32m            ALISA.broadcast(String.format("Игрок %s находится в списке на телепортацию", ColorUtil.fail("уже")), commandSender);[m
         }[m
     }[m
 [m
     private void commandTp(final String[] strings, final CommandSender commandSender) {[m
         if(strings.length != 2) {[m
[31m-            ALISA.say(String.format("%s, укажите ник!", ColorUtil.fail("Ошибка")), commandSender);[m
[32m+[m[32m            ALISA.broadcast(String.format("%s, укажите ник!", ColorUtil.fail("Ошибка")), commandSender);[m
             return;[m
         }[m
 [m
[36m@@ -289,9 +289,9 @@[m [mclass CommandBot implements CommandExecutor, TabCompleter {[m
         final Player iniciator = (Player) commandSender;[m
         if(player != null) {[m
             iniciator.teleport(player.getLocation());[m
[31m-            ALISA.say(format("Игрок %s, телепортирую...", ColorUtil.success("найден")), commandSender);[m
[32m+[m[32m            ALISA.broadcast(format("Игрок %s, телепортирую...", ColorUtil.success("найден")), commandSender);[m
         } else {[m
[31m-            ALISA.say(String.format("Игрок %s найден", ColorUtil.fail("не")), commandSender);[m
[32m+[m[32m            ALISA.broadcast(String.format("Игрок %s найден", ColorUtil.fail("не")), commandSender);[m
         }[m
     }[m
 [m
[36m@@ -345,7 +345,7 @@[m [mclass CommandColors implements CommandExecutor {[m
 [m
         if (commandSender instanceof Player) {[m
             Player player = (Player) commandSender;[m
[31m-            ALISA.say("Вы можете наблюдать доступные цвета и модификаторы:\n"[m
[32m+[m[32m            ALISA.broadcast("Вы можете наблюдать доступные цвета и модификаторы:\n"[m
                     +"-- Цвета:\n" + ChatColor.BLACK + "0 " + ChatColor.DARK_BLUE + "1 " + ChatColor.DARK_GREEN + "2 " +[m
                     ChatColor.DARK_AQUA + "3 " + ChatColor.DARK_RED + "4 " + ChatColor.DARK_PURPLE + "5 " +[m
                     ChatColor.GOLD + "6 " + ChatColor.GRAY + "7 " + ChatColor.DARK_GRAY + "8 " +[m
[36m@@ -370,7 +370,7 @@[m [mclass CommandVotesun implements CommandExecutor {[m
             new VoteEvent(VoteEvent.TypeVote.SUN, commandSender);[m
         }[m
         else {[m
[31m-            ALISA.say("Сейчас ясная погода!", commandSender);[m
[32m+[m[32m            ALISA.broadcast("Сейчас ясная погода!", commandSender);[m
         }[m
         return true;[m
     }[m
[36m@@ -382,7 +382,7 @@[m [mclass CommandVoteday implements CommandExecutor {[m
     @Override[m
     public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {[m
         if(isDay()) {[m
[31m-            ALISA.say("Сейчас день!", commandSender);[m
[32m+[m[32m            ALISA.broadcast("Сейчас день!", commandSender);[m
         }[m
         else {[m
             new VoteEvent(VoteEvent.TypeVote.DAY, commandSender);[m
[36m@@ -416,11 +416,11 @@[m [mclass CommandRules implements CommandExecutor, TabCompleter {[m
             JsonObject rule = rules.getAsJsonObject(pointRule);[m
 [m
             if (rule == null) {[m
[31m-                ALISA.say("Такого правила нет в сводках", commandSender);[m
[32m+[m[32m                ALISA.broadcast("Такого правила нет в сводках", commandSender);[m
             } else {[m
                 String punishment = safelyGetFromJson("punishment", rule);[m
                 String content = safelyGetFromJson("content", rule);[m
[31m-                ALISA.say([m
[32m+[m[32m                ALISA.broadcast([m
                         format("%s\n%s\n" +[m
                                 (!punishment.equals("") ? ChatColor.RED + "" + ChatColor.UNDERLINE + "Наказание:" + ChatColor.RESET + "" + ChatColor.RED + " %s" : ""),[m
                                 "----------------" + pointRule + "----------------",[m
[36m@@ -487,7 +487,7 @@[m [mclass CommandMouth implements CommandExecutor {[m
             ALISA.broadcast(String.join(" ", strings));[m
             return true;[m
         }[m
[31m-        ALISA.say(String.format("%s Вы не обладаете правами администратора!", ColorUtil.fail("[Ошибка доступа]")), commandSender);[m
[32m+[m[32m        ALISA.broadcast(String.format("%s Вы не обладаете правами администратора!", ColorUtil.fail("[Ошибка доступа]")), commandSender);[m
         return true;[m
     }[m
 }[m
\ No newline at end of file[m
[1mdiff --git a/src/main/java/org/flareon/alisa/FLAlisa.java b/src/main/java/org/flareon/alisa/FLAlisa.java[m
[1mindex 3ca6edc..a01a817 100644[m
[1m--- a/src/main/java/org/flareon/alisa/FLAlisa.java[m
[1m+++ b/src/main/java/org/flareon/alisa/FLAlisa.java[m
[36m@@ -99,19 +99,19 @@[m [mpublic class FLAlisa extends JavaPlugin {[m
         Bukkit.broadcastMessage(ColorUtil.getAlisaTag() + words);[m
     }[m
 [m
[31m-    public void say(String words, Player player) {[m
[32m+[m[32m    public void broadcast(String words, Player player) {[m
         player.sendMessage(ColorUtil.getAlisaTag() + words);[m
     }[m
 [m
[31m-    public void say(String words, CommandSender commandSender) {[m
[32m+[m[32m    public void broadcast(String words, CommandSender commandSender) {[m
         commandSender.sendMessage(ColorUtil.getAlisaTag() + words);[m
     }[m
 [m
[31m-    public void say(BaseComponent[] component) {[m
[32m+[m[32m    public void broadcast(BaseComponent[] component) {[m
         Bukkit.spigot().broadcast(component);[m
     }[m
 [m
     public void sayUnknownCommand(String words, CommandSender commandSender) {[m
[31m-        say(String.format("Используй команду правильно! %s", words), commandSender);[m
[32m+[m[32m        broadcast(String.format("Используй команду правильно! %s", words), commandSender);[m
     }[m
 }[m
\ No newline at end of file[m
[1mdiff --git a/src/main/java/org/flareon/alisa/ModeratorsHandler.java b/src/main/java/org/flareon/alisa/ModeratorsHandler.java[m
[1mindex e41f604..db21876 100644[m
[1m--- a/src/main/java/org/flareon/alisa/ModeratorsHandler.java[m
[1m+++ b/src/main/java/org/flareon/alisa/ModeratorsHandler.java[m
[36m@@ -90,13 +90,13 @@[m [mpublic class ModeratorsHandler {[m
         if(this.isModerator(playerName)) {[m
             if(this.hiddenModerators.contains(playerName)) {[m
                 this.hiddenModerators.remove(playerName);[m
[31m-                this.ALISA.say(String.format("Теперь тебя %s в списке онлайн модераторов", ColorUtil.success("видно")), commandSender);[m
[32m+[m[32m                this.ALISA.broadcast(String.format("Теперь тебя %s в списке онлайн модераторов", ColorUtil.success("видно")), commandSender);[m
             } else {[m
                 this.hiddenModerators.add(playerName);[m
[31m-                this.ALISA.say(String.format("Теперь тебя %s в списке онлайн модераторов", ColorUtil.fail("не видно")), commandSender);[m
[32m+[m[32m                this.ALISA.broadcast(String.format("Теперь тебя %s в списке онлайн модераторов", ColorUtil.fail("не видно")), commandSender);[m
             }[m
         } else {[m
[31m-            this.ALISA.say(String.format("Тебя %s в этой группе", ColorUtil.fail("нет")), commandSender);[m
[32m+[m[32m            this.ALISA.broadcast(String.format("Тебя %s в этой группе", ColorUtil.fail("нет")), commandSender);[m
         }[m
     }[m
 [m
[1mdiff --git a/src/main/java/org/flareon/alisa/listeners/JoinEvent.java b/src/main/java/org/flareon/alisa/listeners/JoinEvent.java[m
[1mindex ad36926..7b9c641 100644[m
[1m--- a/src/main/java/org/flareon/alisa/listeners/JoinEvent.java[m
[1m+++ b/src/main/java/org/flareon/alisa/listeners/JoinEvent.java[m
[36m@@ -23,7 +23,7 @@[m [mpublic class JoinEvent implements Listener {[m
             joinedPlayer.teleport(joinedPlayer.getWorld().getSpawnLocation().add(0.0, 0.5, 0.0));[m
             toSpawnPlayerNames.remove(playerName);[m
             this.ALISA.config.set("tospawn-playernames", toSpawnPlayerNames);[m
[31m-            this.ALISA.say("Вы были отправлены на спавн", joinedPlayer);[m
[32m+[m[32m            this.ALISA.broadcast("Вы были отправлены на спавн", joinedPlayer);[m
         }[m
     }[m
 }[m
[1mdiff --git a/src/main/java/org/flareon/alisa/listeners/VoteEvent.java b/src/main/java/org/flareon/alisa/listeners/VoteEvent.java[m
[1mindex 25f9586..3ec1ff3 100644[m
[1m--