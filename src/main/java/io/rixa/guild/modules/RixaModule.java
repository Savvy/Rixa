package io.rixa.guild.modules;

public interface RixaModule {

    String getName();
    String getDescription();
    void load();
    void save();
}
