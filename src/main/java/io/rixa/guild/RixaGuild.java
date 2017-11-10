package io.rixa.guild;

import io.rixa.guild.manager.IGuild;
import io.rixa.guild.modules.RixaModule;
import lombok.Getter;
import net.dv8tion.jda.core.entities.Guild;

import java.util.HashMap;
import java.util.Map;

public class RixaGuild implements IGuild {

    @Getter private final String id;
    @Getter private final Map<String, RixaModule> modules;

    public RixaGuild(Guild guild) {
        id = guild.getId();
        modules = new HashMap<>();
        load();
    }

    @Override
    public void load() {}

    @Override
    public void save() { }

    @Override
    public RixaModule getModule(String id) {
        return modules.get(id);
    }

    @Override
    public RixaModule registerModule(String id, RixaModule module) {
        if (!(isRegistered(id))) modules.put(id, module);
        return module;
    }

    @Override
    public boolean isRegistered(String id) {
        return modules.containsKey(id);
    }
}
