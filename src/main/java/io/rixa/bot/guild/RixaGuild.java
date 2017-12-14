package io.rixa.bot.guild;

import io.rixa.bot.guild.manager.IGuild;
import io.rixa.bot.guild.modules.RixaModule;
import io.rixa.bot.guild.modules.module.ConversationModule;
import io.rixa.bot.guild.modules.module.MusicModule;
import io.rixa.bot.guild.settings.Settings;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.core.entities.Guild;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RixaGuild implements IGuild {

    @Getter private final Map<String, RixaModule> modules;
    @Getter private List<String> confirmationUsers;
    @Getter @Setter private List<String> keywords;
    @Getter @Setter private String description;
    @Getter private final String id;
    @Getter private Guild guild;
    @Getter private Settings settings;

    public RixaGuild(Guild guild) {
        this.guild = guild;
        id = guild.getId();
        modules = new HashMap<>();
        keywords = new ArrayList<>();
        confirmationUsers = new ArrayList<>();
        load();
    }

    @Override
    public void load() {
        registerModules(
                new ConversationModule("Conversation", "Have a conversation with Rixa!", this),
                new MusicModule("Music", "Listen to music from within discord!", this)
        );
        settings = new Settings(this);
    }

    @Override
    public void save() { }

    @Override
    public RixaModule getModule(String id) {
        return modules.get(id);
    }

    private void registerModules(RixaModule... modules) {
        for (RixaModule module : modules) {
            registerModule(module);
        }
    }
    @Override
    public RixaModule registerModule(RixaModule module) {
        if (!(isRegistered(module.getName()))) modules.put(module.getName(), module);
        return module;
    }

    @Override
    public boolean isRegistered(String id) {
        return modules.containsKey(id);
    }
}
