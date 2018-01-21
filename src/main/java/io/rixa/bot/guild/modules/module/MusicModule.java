package io.rixa.bot.guild.modules.module;

import io.rixa.bot.Rixa;
import io.rixa.bot.data.storage.DatabaseAdapter;
import io.rixa.bot.data.storage.enums.Statements;
import io.rixa.bot.guild.RixaGuild;
import io.rixa.bot.guild.manager.GuildManager;
import io.rixa.bot.guild.modules.RixaModule;
import io.rixa.bot.guild.modules.module.music.MusicManager;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.core.entities.Role;

import java.util.concurrent.ExecutorService;

public class MusicModule implements RixaModule {

    @Getter private String name, description;
    @Getter @Setter private Role musicRole;
    @Getter @Setter boolean enabled;
    @Getter @Setter private MusicManager musicManager;
    @Getter private RixaGuild guild;

    public MusicModule(String name, String description, RixaGuild guild) {
        this.name = name;
        this.description = description;
        this.enabled = true;
        this.guild = guild;
        load();
    }

    @Override
    public void load() {
        if (!(DatabaseAdapter.getInstance().exists("music", "guild_id", guild.getId()))) {
            insert();
            this.enabled = false;
            return;
        }
        setEnabled(DatabaseAdapter.getInstance().get().queryForObject("SELECT `enabled` FROM `music` WHERE `guild_id` = ?",
                        new Object[]{guild.getId()}, (resultSet, i) -> resultSet.getBoolean("enabled")));
        reload();
    }

    @Override
    public void save() {
        // Check & Set if enabled;
        DatabaseAdapter.getInstance().get().update("UPDATE `music` SET `enabled` = ? WHERE `guild_id` = ?", enabled, guild.getId());
        if (musicRole != null)
        DatabaseAdapter.getInstance().get().update("UPDATE `music` SET `music_role` = ? WHERE `guild_id` = ?", musicRole.getId(), guild.getId());
    }

    @Override
    public void reload() {
        if (!isEnabled()) return;
        DatabaseAdapter.getInstance().get().queryForObject(Statements.SELECT_ALL_FROM_TABLE.getStatement("{table_name}", "music"),
                new Object[] { guild.getId() }, (resultSet, i) -> {
                    if (!resultSet.getString("music_role").equalsIgnoreCase("default_value")
                    && guild.getGuild().getRoleById(resultSet.getString("music_role")) != null) {
                        this.musicRole = guild.getGuild().getRoleById(resultSet.getString("music_role"));
                    }
                    return 0;
                });
    }

    private void insert() {
        DatabaseAdapter.getInstance().get().update("INSERT INTO `music` (`guild_id`, `music_role`, `enabled`) VALUES (?, ?, ?);",
                guild.getId(), "default_value", false);
    }
}
