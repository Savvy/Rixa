package me.savvy.rixa;

import lombok.Getter;
import lombok.Setter;
import me.savvy.rixa.commands.admin.*;
import me.savvy.rixa.commands.general.*;
import me.savvy.rixa.commands.handlers.CommandExec;
import me.savvy.rixa.commands.handlers.CommandHandler;
import me.savvy.rixa.commands.mod.DeleteMessagesCommand;
import me.savvy.rixa.commands.mod.MuteCommand;
import me.savvy.rixa.commands.mod.PurgeMessagesCommand;
import me.savvy.rixa.data.database.Data;
import me.savvy.rixa.data.database.DataType;
import me.savvy.rixa.data.database.sql.DatabaseManager;
import me.savvy.rixa.data.filemanager.ConfigManager;
import me.savvy.rixa.data.filemanager.LanguageManager;
import me.savvy.rixa.events.BotEvent;
import me.savvy.rixa.events.MemberEvent;
import me.savvy.rixa.events.MessageEvent;
import me.savvy.rixa.events.VoiceChannel;
import me.savvy.rixa.modules.reactions.handlers.React;
import me.savvy.rixa.modules.reactions.handlers.ReactionManager;
import me.savvy.rixa.modules.reactions.react.ConfigReaction;
import me.savvy.rixa.modules.reactions.react.HelpReaction;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Invite;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.AnnotatedEventManager;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Timber on 5/7/2017.
 */
public class Rixa {
    
    @Getter
    private static Data data;
    @Getter
    private static long timeUp;
    @Getter
    private static Rixa instance;
    @Getter
    private static List<JDA> shardsList;
    @Getter
    private static ConfigManager config;
    @Getter @Setter
    private static DatabaseManager dbManager;
    @Getter @Setter
    private LanguageManager languageManager;
    // String search = event.getMessage().getContent().substring(event.getMessage().getContent().indexOf(" ") + 1);
    public static void main(String[] args) {
        instance = new Rixa();
        shardsList = new LinkedList<>();
    //    config = new ConfigManager();
        config = new ConfigManager(new File("Rixa/config.json"));
        load();
    }

    private static void load() {
        dbManager = new DatabaseManager(
                String.valueOf(config.getJsonObject().getJSONObject("sql").getString("hostName")),
                String.valueOf(config.getJsonObject().getJSONObject("sql").getString("portNumber")),
                String.valueOf(config.getJsonObject().getJSONObject("sql").getString("databaseName")),
                String.valueOf(config.getJsonObject().getJSONObject("sql").getString("userName")),
                String.valueOf(config.getJsonObject().getJSONObject("sql").getString("password")));
        dbManager.createTable();
        getInstance().setLanguageManager(new LanguageManager(new File("Rixa/languages/language.json")));
        try {
            int shards = 5;
            for(int i = 0; i < shards; i++) {
                Logger.getLogger("Rixa").info("Loading shard #" + i);
                JDABuilder jda = new JDABuilder(AccountType.BOT)
                        .setToken(config.getJsonObject().getString("secretToken"))
                        .setEventManager(new AnnotatedEventManager())
                        .addEventListener(new MessageEvent())
                        .addEventListener(new BotEvent())
                        .addEventListener(new MemberEvent())
                        .addEventListener(new VoiceChannel())
                        .setGame(Game.of(config.getJsonObject().getString("botGame")))
                        .setAutoReconnect(true)
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
                new BatchMoveCommand(), new MuteCommand(), new MusicCommand(),
                new ConfigCommand(), new UrbanDictionaryCommand(), new YoutubeCommand(),
                new AddRoleCommand(), new RemoveRoleCommand(), new LevelsCommand(),
                new LeaderboardCommand()});
        register(new React[] {new HelpReaction(), new ConfigReaction() });
        data = new Data(DataType.SQL);
    }
    
    private static void register(CommandExec commandExecs[]) {
        for (CommandExec command: commandExecs) {
            CommandHandler.registerCommand(command);
        }
    }

    private static void register(React react[]) {
        for (React reaction: react) {
            ReactionManager.registerReaction(reaction);
        }
    }

    public Logger getLogger() {
        return Logger.getLogger("Rixa");
    }
    
}
