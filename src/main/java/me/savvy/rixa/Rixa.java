package me.savvy.rixa;

import me.savvy.rixa.commands.admin.BatchMoveCommand;
import me.savvy.rixa.commands.general.HelpCommand;
import me.savvy.rixa.commands.general.InfoCommand;
import me.savvy.rixa.commands.general.PingCommand;
import me.savvy.rixa.commands.general.ServerInfoCommand;
import me.savvy.rixa.commands.handlers.CommandExec;
import me.savvy.rixa.commands.handlers.CommandHandler;
import me.savvy.rixa.commands.mod.DeleteMessagesCommand;
import me.savvy.rixa.commands.mod.PurgeMessagesCommand;
import me.savvy.rixa.database.DatabaseManager;
import me.savvy.rixa.events.BotEvent;
import me.savvy.rixa.events.MessageEvent;
import me.savvy.rixa.modules.reactions.handlers.ReactionManager;
import me.savvy.rixa.modules.reactions.react.HelpReaction;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.AnnotatedEventManager;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by Timber on 5/7/2017.
 */
public class Rixa {

    private static List<JDA> shardsList;
    private static DatabaseManager dbManager;
    private static long timeUp;
    private static Rixa instance; // String search = event.getMessage().getContent().substring(event.getMessage().getContent().indexOf(" ") + 1);
    private static Map<String, String> config;

    public static void main(String[] args) {
        instance = new Rixa();
        shardsList = new LinkedList<>();
        config = new HashMap<>();
        populateConfiguration();
        load();
    }

    private static void register(CommandExec commandExecs[]) {
        for (CommandExec command: commandExecs) {
            CommandHandler.registerCommand(command);
        }
    }

    public static Rixa getInstance() {
        return instance;
    }

    public long getTimeUp() {
        return timeUp;
    }

    public Logger getLogger() {
        return Logger.getLogger("Rixa");
    }

    private static void load() {
        if(!config.containsKey("TOKEN")) {
           getInstance().getLogger().severe("Could not find \"TOKEN\" in config.text! Shutting down...");
            System.exit(0);
        }
        dbManager = new DatabaseManager(config.get("SQL_HOSTNAME"), config.get("SQL_PORT"), config.get("SQL_DATABASE"), config.get("SQL_USER"), config.get("SQL_PASSWORD"));
        dbManager.createTable();
        try {
            int shards = 3;
            for(int i = 0; i < shards; i++) {
                Logger.getLogger("Rixa").info("Loading shard #" + i);
                JDABuilder jda = new JDABuilder(AccountType.BOT)
                        .setToken(config.get("TOKEN"))
                        .setEventManager(new AnnotatedEventManager())
                        .addEventListener(new MessageEvent())
                        .addEventListener(new BotEvent());
                        if(config.containsKey("GAME")) {
                            jda.setGame(Game.of(config.get("GAME")));
                        }
                        jda.setAutoReconnect(true)
                        .setStatus(OnlineStatus.ONLINE)
                        .setAudioEnabled(true)
                        .useSharding(i, shards);
                shardsList.add(jda.buildBlocking());
                getInstance().getLogger().info("Shard #" + i + " has been loaded");
            }
        } catch (LoginException | InterruptedException | RateLimitedException e) {
            e.printStackTrace();
        }
        timeUp = System.currentTimeMillis();
        register(new CommandExec[] {
                new InfoCommand(), new ServerInfoCommand(), new HelpCommand(),
                new DeleteMessagesCommand(), new PingCommand(), new PurgeMessagesCommand(),
                new BatchMoveCommand() });
        ReactionManager.registerReaction(new HelpReaction());
    }

    public DatabaseManager getDbManager() {
        return dbManager;
    }

    public void setDbManager(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    private static void populateConfiguration()  {
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(new File("config.txt"));
        } catch (FileNotFoundException e) {
            getInstance().getLogger().severe("Could not find file \"config.text\"! Shutting down...");
            System.exit(0);
        }
        BufferedReader br = new BufferedReader(fileReader);
        String line = null;
        try {
            while ((line = br.readLine()) != null) {
                String[] s = line.split(":");
                config.put(s[0], s[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
