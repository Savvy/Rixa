package io.rixa.bot.guild;

import io.rixa.bot.guild.manager.IGuild;
import io.rixa.bot.guild.modules.RixaModule;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.core.entities.Guild;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RixaGuild implements IGuild {

    @Getter private final Map<String, RixaModule> modules;
    @Getter @Setter private List<String> keywords;
    @Getter @Setter private String description;
    @Getter private final String id;
    @Getter private Guild guild;

    public RixaGuild(Guild guild) {
        this.guild = guild;
        id = guild.getId();
        modules = new HashMap<>();
        keywords = new ArrayList<>();
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
