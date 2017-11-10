package io.rixa;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.rixa.data.config.Configuration;
import io.rixa.utils.FileUtils;
import lombok.Getter;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.AnnotatedEventManager;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Rixa {

    private static Rixa instance;
    @Getter private Configuration configuration;
    @Getter private ObjectMapper objectMapper;
    @Getter private List<JDA> shardList;
    @Getter private File defaultPath;
    @Getter private Logger logger;
    @Getter private JDA jda;

    private Rixa() {
        instance = this;
        logger = Logger.getLogger(Rixa.class.getCanonicalName());
        objectMapper = new ObjectMapper(new YAMLFactory());
        defaultPath = new File("Rixa");
        shardList = new ArrayList<>();
        defaultPath.mkdirs();
        loadConfiguration();
        loadJDA();
        registerCommands();
    }

    private void loadJDA() {
        for (int i = 0; i < configuration.getShards(); i++) {
            getLogger().info("Loading Shard #" + (i + 1) + "!");
            try {
                jda = new JDABuilder(AccountType.BOT)
                        .setToken(configuration.getToken())
                        .setGame(Game.of(configuration.getBotGame()))
                        .setEventManager(new AnnotatedEventManager())
                        .setAutoReconnect(true)
                        .setStatus(OnlineStatus.ONLINE)
                        .setAudioEnabled(true)
                        .setEnableShutdownHook(false)
                        .useSharding(i, configuration.getShards())
                        .buildBlocking();
                getShardList().add(jda);
                getLogger().info("Shard #" + (i + 1) + " has been loaded");
                Thread.sleep(5000);
            } catch (LoginException | RateLimitedException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> getShardList().forEach(JDA::shutdown)));
    }

    private void registerCommands() {}

    private void loadConfiguration() {
        try {
            FileUtils.saveResource("config.yml", false);
            File file = new File(defaultPath.getPath() + "/config.yml");
            configuration = objectMapper.readValue(file, Configuration.class);
            logger.info("Configuration successfully loaded.");
        } catch (IOException e) {
            logger.severe("Could not properly load configuration file!.");
            e.printStackTrace();
        }
    }

    public static Rixa getInstance() {
        return (instance == null) ? new Rixa() : instance;
    }

    public static void main(String[] args) {
        Rixa.getInstance();
    }
}
