package me.savvy.rixa.modules.music;

import lombok.Getter;
import lombok.Setter;
import me.savvy.rixa.Rixa;
import me.savvy.rixa.data.database.sql.SQLBuilder;
import me.savvy.rixa.guild.RixaGuild;
import me.savvy.rixa.modules.RixaModule;
import me.savvy.rixa.utils.DatabaseUtils;
import net.dv8tion.jda.core.entities.Guild;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Timber on 5/23/2017.
 */
public class MusicModule implements RixaModule {
    private SQLBuilder db;
    @Getter
    @Setter
    private boolean enabled;
    @Getter
    @Setter
    private String musicRole;
    @Getter
    private Guild guild;

    public MusicModule(RixaGuild rixaGuild) {
        this.guild = rixaGuild.getGuild();
        load();
    }

    @Override
    public void load() {
        try {
            this.enabled = false;
            this.musicRole = "default_value";
            db = Rixa.getDatabase();
            if (!DatabaseUtils.checkExists("music", guild)) {
                PreparedStatement statement = db.getPreparedStatement("INSERT INTO `music` (`guild_id`, `music_role`, `enabled`) VALUES (?, 'default_value', '0');");
                statement.setString(1, guild.getId());
                statement.executeUpdate();
            }
            PreparedStatement statement = db.getPreparedStatement("SELECT * FROM `music` WHERE `guild_id` = ?");
            statement.setString(1, guild.getId());
            ResultSet set = statement.executeQuery();
            if (set.next()) {
                setMusicRole(set.getString("music_role"));
                setEnabled(set.getBoolean("enabled"));
            }
            set.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save() {
        DatabaseUtils.update("music", "enabled", "guild_id", enabled, guild.getId());
        DatabaseUtils.update("music", "music_role", "guild_id", musicRole, guild.getId());
    }

    @Override
    public String getName() {
        return "Music";
    }

    @Override
    public String getDescription() {
        return "Listen to music in your voice channel.";
    }

    public boolean isRoleRequired() {
        return (musicRole != null && !musicRole.equalsIgnoreCase("default_value"));
    }
}
