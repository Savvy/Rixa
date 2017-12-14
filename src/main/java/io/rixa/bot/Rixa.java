package io.rixa.bot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.rixa.bot.commands.cmds.admin.PMCommand;
import io.rixa.bot.commands.cmds.general.*;
import io.rixa.bot.commands.cmds.moderator.ClearCommand;
import io.rixa.bot.commands.cmds.moderator.MuteCommand;
import io.rixa.bot.commands.cmds.other.ShutdownCommand;
import io.rixa.bot.commands.handler.CommandHandler;
import io.rixa.bot.commands.perms.RixaPermission;
import io.rixa.bot.data.config.Configuration;
import io.rixa.bot.data.storage.DatabaseAdapter;
import io.rixa.bot.events.BotJoinListener;
import io.rixa.bot.events.MessageListener;
import io.rixa.bot.events.ReadyListener;
import io.rixa.bot.events.UserListener;
import io.rixa.bot.guild.RixaGuild;
import io.rixa.bot.guild.manager.GuildManager;
import io.rixa.bot.guild.modules.module.MusicModule;
import io.rixa.bot.utils.FileUtils;
import lombok.Getter;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.AnnotatedEventManager;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class Rixa {

    private static Rixa instance;
    @Getter
    private CommandHandler commandHandler;
    @Getter
    private Configuration configuration;
    @Getter
    private ObjectMapper objectMapper;
    @Getter
    private List<JDA> shardList;
    @Getter
    private File defaultPath;
    @Getter
    private Logger logger;
    private static long timeUp;

    private Rixa() {
        instance = this;
        logger = Logger.getLogger(Rixa.class.getCanonicalName());
        objectMapper = new ObjectMapper(new YAMLFactory());
        defaultPath = new File("Rixa/");
        commandHandler = new CommandHandler();
        shardList = new ArrayList<>();
        defaultPath.mkdirs();
        loadConfiguration();
        registerCommands();
        loadJDA();
    }

    public long getTimeUp() {
        return timeUp;
    }

    private void loadJDA() {
        System.out.println("SHARDS: " + configuration.getShards()); // Remove this
        JDABuilder jda = new JDABuilder(AccountType.BOT)
                .setToken(configuration.getToken())
                .setGame(Game.of(configuration.getBotGame()))
                .setEventManager(new AnnotatedEventManager())
                .addEventListener(new ReadyListener(), new BotJoinListener(), new MessageListener(),
                        new UserListener())
                .setAutoReconnect(true)
                .setAudioEnabled(true)
                .setEnableShutdownHook(false)
                .setStatus(OnlineStatus.ONLINE);
        for (int i = 0; i < configuration.getShards(); i++) {
            try {
                getLogger().info("Loading Shard #" + (i + 1) + "!");
                getShardList().add(jda.useSharding(i, configuration.getShards()).buildBlocking());
                getLogger().info("Shard #" + (i + 1) + " has been loaded");
                Thread.sleep(5000);
            } catch (InterruptedException | RateLimitedException | LoginException e) {
                e.printStackTrace();
            }
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            getShardList().forEach(JDA::shutdown);
            getShardList().forEach(jdaInstance -> {
                jdaInstance.getGuilds().forEach((Guild guild) -> {
                    RixaGuild rixaGuild = GuildManager.getInstance().getGuild(guild);
                    MusicModule musicModule = (MusicModule) rixaGuild.getModule("Music");
                    musicModule.getMusicManager().getScheduler().getQueue().clear();
                    musicModule.getMusicManager().getScheduler().getPlayer().destroy();
                    guild.getAudioManager().setSendingHandler(null);
                    rixaGuild.save();
                });
                jdaInstance.shutdown();
            });
        }));
        timeUp = System.currentTimeMillis();
    }

    private void registerCommands() {
        this.commandHandler.registerCommands(
                new AdviceCommand("advice", RixaPermission.NONE, "Receive advice from the great beyond..."),
                new ClearCommand("clear", RixaPermission.NONE, "Clear Chat!", Arrays.asList("deletemessages", "cmessages")),
                new FeaturesCommand("features", RixaPermission.NONE, "List Rixa's official features!"),
                new HelpCommand("help", RixaPermission.NONE, "Review commands and its usages!"),
                new InfoCommand("info", RixaPermission.NONE, "Review information about a user or Rixa!"),
                new MinecraftCommand("minecraft", RixaPermission.NONE, "See minecraft server info", Collections.singletonList("mc")),
                new ModulesCommand("modules", RixaPermission.NONE, "List both enabled & disabled features of Rixa for this server!"),
                new MusicCommand("music", RixaPermission.NONE, "Listen to music right from discord!"),
                new MuteCommand("mute", RixaPermission.NONE, "Mute those pesky children!"),
                new PingCommand("ping", RixaPermission.NONE, "Check Rixa's ping!"),
                new PMCommand("pm", RixaPermission.NONE, "Private Message all users with a specific role!"),
                new QuoteCommand("quote", RixaPermission.NONE, "Receive a quote from some of the greatest authors!"),
                new RoleMemberList("listmembers", RixaPermission.NONE, "List all users with a specific role!"),
                new ServerInfoCommand("serverinfo", RixaPermission.NONE, "Review information about the server!"),
                new ShutdownCommand("shutdown", RixaPermission.NONE, "Shutdown Rixa!"),
                new UrbanDictionaryCommand("ud", RixaPermission.NONE, "Look up urban definitions!"),
                new YoutubeCommand("youtube", RixaPermission.NONE, "Search for music on youtube!"));
    }

    private void loadConfiguration() {
        try {
            if (FileUtils.saveResource("config.yml", false)) {
                logger.info("Shutting down Rixa. Please edit configuration");
                System.exit(0);
            }
            File file = new File(defaultPath.getPath() + "/config.yml");
            configuration = objectMapper.readValue(file, Configuration.class);
            logger.info("Configuration successfully loaded.");
            DatabaseAdapter.getInstance().check();
        } catch (IOException e) {
            logger.severe("Could not properly load configuration file!.");
            e.printStackTrace();
        }
    }

    public Guild getGuildById(String id) {
        Guild guild = null;
        for (JDA jda : Rixa.getInstance().getShardList()) {
            System.out.println(jda.getShardInfo().toString());
            System.out.println("JDA GUILDS:" + jda.getGuilds().size());
            if (jda.getGuilds().size() == 0 || jda.getGuildById(id) == null) continue;
            guild = jda.getGuildById(id);
            break;
        }
        if (guild != null) return guild;
        throw new NullPointerException("Guild not found.");
    }

    public static Rixa getInstance() {
        return (instance == null) ? new Rixa() : instance;
    }

    public void close() {
        shardList.forEach(JDA::shutdown);
    }
}