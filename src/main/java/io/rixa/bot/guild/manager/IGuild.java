package io.rixa.bot.guild.manager;

import io.rixa.bot.guild.modules.RixaModule;

public interface IGuild {

    String getId();
    void load();
    void save();
    RixaModule getModule(String id);
    RixaModule registerModule(String id, RixaModule module);
    boolean isRegistered(String id);
}
