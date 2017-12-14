package io.rixa.bot.guild.manager;

public interface RixaSettings {

    String getPrefix();

    void load();
    void save();
}
