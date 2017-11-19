package io.rixa.bot.guild.modules;

public interface RixaModule {

    String getName();
    String getDescription();
    boolean isEnabled();
    void load();
    void save();
    void reload();
}
