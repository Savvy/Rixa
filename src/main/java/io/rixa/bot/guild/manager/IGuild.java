package io.rixa.bot.guild.manager;

import io.rixa.bot.guild.modules.RixaModule;

import java.util.List;

public interface IGuild {

    String getId();
    void load();
    void save();
    RixaModule getModule(String id);
    RixaModule registerModule(String id, RixaModule module);
    boolean isRegistered(String id);
    void setDescription(String description);
    void setKeywords(List<String> keywords);
}
