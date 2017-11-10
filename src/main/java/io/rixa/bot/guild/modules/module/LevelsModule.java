package io.rixa.bot.guild.modules.module;

import io.rixa.bot.guild.modules.RixaModule;
import lombok.Getter;
import lombok.Setter;

public class LevelsModule implements RixaModule {

    @Getter @Setter private String name, description;

    public LevelsModule(String name, String description) {
        setName(name);
        setDescription(description);
    }

    @Override
    public void load() {

    }

    @Override
    public void save() {

    }
}
