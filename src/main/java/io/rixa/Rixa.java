package io.rixa;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.rixa.data.config.Configuration;
import io.rixa.utils.FileUtils;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class Rixa {

    private static Rixa instance;
    @Getter private Configuration configuration;
    @Getter private ObjectMapper objectMapper;
    @Getter private File defaultPath;
    @Getter private Logger logger;

    private Rixa() {
        instance = this;
        logger = Logger.getLogger(Rixa.class.getCanonicalName());
        objectMapper = new ObjectMapper(new YAMLFactory());
        defaultPath = new File("Rixa");
        defaultPath.mkdirs();
        loadConfiguration();
        registerCommands();
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
