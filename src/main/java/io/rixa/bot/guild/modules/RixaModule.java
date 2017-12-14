package io.rixa.bot.guild.modules;

import io.rixa.bot.Rixa;
import io.rixa.bot.guild.RixaGuild;

public interface RixaModule {

    String getName();
    String getDescription();
    boolean isEnabled();
    void load();
    void save();
    void reload();
    RixaGuild getGuild();
}
