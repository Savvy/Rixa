package me.savvy.rixa;

import lombok.Getter;
import lombok.Setter;
import me.savvy.rixa.commands.admin.AddRoleCommand;
import me.savvy.rixa.commands.admin.BatchMoveCommand;
import me.savvy.rixa.commands.admin.ConfigCommand;
import me.savvy.rixa.commands.admin.RemoveRoleCommand;
import me.savvy.rixa.commands.general.*;
import me.savvy.rixa.commands.handlers.CommandExec;
import me.savvy.rixa.commands.handlers.CommandHandler;
import me.savvy.rixa.commands.mod.DeleteMessagesCommand;
import me.savvy.rixa.commands.mod.MuteCommand;
import me.savvy.rixa.commands.mod.PurgeMessagesCommand;
import me.savvy.rixa.commands.mod.RaidModeCommand;
import me.savvy.rixa.commands.owner.OwnerCommand;
import me.savvy.rixa.data.database.sql.SQLBuilder;
import me.savvy.rixa.data.database.sql.other.DatabaseTables;
import me.savvy.rixa.data.filemanager.ConfigManager;
import me.savvy.rixa.data.filemanager.LanguageManager;
import me.savvy.rixa.events.*;
import me.savvy.rixa.events.Shutdown;
import me.savvy.rixa.modules.reactions.handlers.React;
import me.savvy.rixa.modules.reactions.handlers.ReactionManager;
import me.savvy.rixa.modules.reactions.react.ConfigReaction;
import me.savvy.rixa.modules.reactions.react.HelpReaction;
import me.savvy.rixa.modules.reactions.react.LeaderboardReaction;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.AnnotatedEventManager;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Logger;

/**
 * Created by Timber on 5/7/2017.
 * Edited by Majr on 9/22/2017
 */
public class Rixa {

    @Getter
    private static long timeUp;
    @Getter
    private static Rixa instance;
    @Getter
    private static List<JDA> shardsList;
    @Getter
    private static ConfigManager config;
    @Getter
    @Setter
    private static SQLBuilder database;
    @Getter
    @Setter
    private LanguageManager languageManager;
    @Getter
    @Setter
    private ScheduledExecutorService executorService;

    // String search = event.getMessage().getContent().substring(event.getMessage().getContent().indexOf(" ") + 1);
    public static void main(String[] args) {
        instance = new Rixa();
        shardsList = new LinkedList<>();
        config = new ConfigManager(new File("Rixa/config.json"));
        load();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                getShardsList().forEach(JDA::shutdown);
            }
        });
    }

    private static void load() {
        getInstance().setExecutorService(Executors.newSingleThreadScheduledExecutor());

        database = new SQLBuilder(
                config.getJsonObject().getJSONObject("sql").getString("userName"),
                config.getJsonObject().getJSONObject("sql").getString("password"),
                config.getJsonObject().getJSONObject("sql").getString("portNumber"),
                config.getJsonObject().getJSONObject("sql").getString("databaseName"),
                config.getJsonObject().getJSONObject("sql").getString("hostName"));
        Arrays.stream(DatabaseTables.values()).forEach(databaseTables -> {
            getInstance().getLogger().info("Checking database table (creating if needed): " + databaseTables.toString());
            try {
                database.executeUpdate(databaseTables.getQuery());
                getInstance().getLogger().info("Done checking " + databaseTables.toString());
            } catch (SQLException e) {
                getInstance().getLogger().severe("Could not create table: " + databaseTables.toString());
                e.printStackTrace();
            }
        });

        getInstance().setLanguageManager(new LanguageManager(new File("Rixa/languages/language.json")));
        try {
            int shards = 5;
            for (int i = 0; i < shards; i++) {
                Logger.getLogger("Rixa").info("Loading shard #" + i);
                JDABuilder jda = new JDABuilder(AccountType.BOT)
                        .setToken(config.getJsonObject().getString("secretToken"))
                        .setEventManager(new AnnotatedEventManager())
                        .addEventListener(new MessageEvent())
                        .addEventListener(new BotEvent())
                        .addEventListener(new MemberEvent())
                        .addEventListener(new VoiceChannel())
                        .addEventListener(new Shutdown())
                        .setGame(Game.of(config.getJsonObject().getString("botGame")))
                        .setAutoReconnect(true)
                        .setStatus(OnlineStatus.ONLINE)
                        .setAudioEnabled(true)
                        .useSharding(i, shards);
                shardsList.add(jda.buildAsync());
                getInstance().getLogger().info("Shard #" + i + " has been loaded");
                Thread.sleep(5000);
            }
        } catch (LoginException | InterruptedException | RateLimitedException e) {
            e.printStackTrace();
        }

        //Guilds.getGuilds().values().parallelStream().forEach((rixaGuild) -> rixaGuild.load());

        timeUp = System.currentTimeMillis();
        register(new InfoCommand(), new ServerInfoCommand(), new HelpCommand(),
                new DeleteMessagesCommand(), new PingCommand(), new PurgeMessagesCommand(),
                new BatchMoveCommand(), new MuteCommand(), new MusicCommand(),
                new ConfigCommand(), new UrbanDictionaryCommand(), new YoutubeCommand(),
                new AddRoleCommand(), new RemoveRoleCommand(), new LevelsCommand(),
                new LeaderboardCommand(), new RaidModeCommand(), new OwnerCommand(), new ColorCommand());
        register(new HelpReaction(), new ConfigReaction(), new LeaderboardReaction());
    }

    private static void register(CommandExec... commandExecs) {
        for (CommandExec command : commandExecs) {
            CommandHandler.registerCommand(command);
        }
    }

    private static void register(React... react) {
        for (React reaction : react) {
            ReactionManager.registerReaction(reaction);
        }
    }

    public Logger getLogger() {
        return Logger.getLogger("Rixa");
    }

    public void close() {
        try {
            database.closeConnection();
            Thread.sleep(200);
            getShardsList().forEach(JDA::shutdown);
            Thread.sleep(200);
            System.exit(0);
        } catch (InterruptedException ex) {
            getLogger().severe("Could not shutdown Rixa instance.");
        }
    }
}
