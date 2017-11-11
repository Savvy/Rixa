package io.rixa.bot.guild.manager;

import io.rixa.bot.data.storage.DatabaseAdapter;
import io.rixa.bot.guild.RixaGuild;
import net.dv8tion.jda.core.entities.Guild;

import java.util.HashMap;
import java.util.Map;

public class GuildManager {

    private Map<String, RixaGuild> rixaGuildMap = new HashMap<>();

    private GuildManager() {
        instance = this;
    }
    private static GuildManager instance;

    public static GuildManager getInstance() {
        return (instance == null) ? new GuildManager() : instance;
    }

    public void addGuild(Guild guild) {
        if (!(DatabaseAdapter.getInstance().exists("core", "guild_id", guild.getId()))) {
            insert(guild);
        }
        RixaGuild rixaGuild = (RixaGuild) DatabaseAdapter.getInstance().get().queryForObject(
                "SELECT * FROM `core` WHERE `guild_name` = ?", new Object[] { guild.getId() }, new GuildMapper());
        rixaGuildMap.put(guild.getId(), rixaGuild);
    }

    private void insert(Guild guild) {
        DatabaseAdapter.getInstance().get().update
                ("INSERT INTO `core` (`guild_id`, `guild_name`, `description`, `keywords`) VALUES (?, ?, ?, ?)",
                        guild.getId(), guild.getName(), "Description not set.", "No Keywords Found.");
    }
}
