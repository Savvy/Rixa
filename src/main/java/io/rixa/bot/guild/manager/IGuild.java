package io.rixa.bot.guild.manager;

import io.rixa.bot.commands.perms.RixaPermission;
import io.rixa.bot.guild.modules.RixaModule;
import net.dv8tion.jda.core.entities.User;

import java.util.List;

public interface IGuild {

    String getId();
    void load();
    void save();
    RixaModule getModule(String id);
    RixaModule registerModule(RixaModule module);
    boolean isRegistered(String id);
    void setDescription(String description);
    void setKeywords(List<String> keywords);

    boolean hasPermission(User user, RixaPermission permission);

    void addPermission(String guildId, RixaPermission permission);
    void removePermission(String guildId, RixaPermission rixaPermission);
}
