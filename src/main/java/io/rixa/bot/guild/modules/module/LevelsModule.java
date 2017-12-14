package io.rixa.bot.guild.modules.module;

import io.rixa.bot.guild.RixaGuild;
import io.rixa.bot.guild.modules.RixaModule;
import lombok.Getter;
import lombok.Setter;

public class LevelsModule implements RixaModule {

    @Getter private String name, description;
    @Getter @Setter boolean enabled;
    @Getter private RixaGuild guild;

    public LevelsModule(String name, String description, RixaGuild rixaGuild) {
        this.name = name;
        this.description = description;
        this.guild = rixaGuild;
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
