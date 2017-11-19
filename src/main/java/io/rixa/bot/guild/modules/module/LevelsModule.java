package io.rixa.bot.guild.modules.module;

import io.rixa.bot.guild.modules.RixaModule;
import lombok.Getter;
import lombok.Setter;

public class LevelsModule implements RixaModule {

    @Getter private String name, description;
    @Getter @Setter boolean enabled;

    public LevelsModule(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public void load() {

    }

    @Override
    public void save() {

    }

    @Override
    public void reload() {

    }
}
