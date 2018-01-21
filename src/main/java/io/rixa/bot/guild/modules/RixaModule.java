package io.rixa.bot.guild.modules;

import io.rixa.bot.guild.RixaGuild;

public interface RixaModule {

    String getName();
    String getDescription();
    boolean isEnabled();
    void setEnabled(boolean b);
    void load();
    void save();
    void reload();
    RixaGuild getGuild();
}
