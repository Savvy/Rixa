package io.rixa.bot.data.config;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

public class Configuration {

    @Getter @Setter private Map<String, String> sqlCredentials;
    @Getter @Setter private List<String> botAdmins;
    @Getter @Setter private String token, botGame;
    @Getter @Setter private int shards;

    public Configuration() {}
}
