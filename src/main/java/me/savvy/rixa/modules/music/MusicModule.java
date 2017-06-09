package me.savvy.rixa.modules.music;

import me.savvy.rixa.Rixa;
import me.savvy.rixa.data.database.sql.DatabaseManager;
import me.savvy.rixa.enums.Result;
import me.savvy.rixa.modules.RixaModule;
import net.dv8tion.jda.core.entities.Guild;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Timber on 5/23/2017.
 */
public class MusicModule implements RixaModule {
    private DatabaseManager db;
    private boolean enabled;
    private String musicRole;
    private Guild guild;

    public MusicModule(Guild guild) {
        this.guild = guild;
        this.enabled = false;
        this.musicRole = "default_value";
        db = Rixa.getInstance().getDbManager();
        load();
    }

    public void load() {
        System.out.println("Testing " + guild.getName());
        if(!checkExists()) {
            db.insert("INSERT INTO `music` (`guild_id`, `music_role`, `enabled`)" +
                    " VALUES ('" + guild.getId() + "', 'default_value', '0');");
            return;
        }
        try {
            PreparedStatement ps = db.getConnection().prepareStatement
                    ("SELECT * FROM `music` WHERE `guild_id` = ?;");
            ps.setString(1, guild.getId());
            ResultSet rs = db.getObject(ps);
            this.musicRole = rs.getString("music_role");
            this.enabled = rs.getBoolean("enabled");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return "Music";
    }

    @Override
    public String getDescription() {
        return "Listen to music in your voice channel.";
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public Result setEnabled(boolean val) {
        this.enabled = val;
        return Rixa.getInstance().getData().update("music", "enabled", "guild_id", val, guild.getId());
    }

    public boolean isRoleRequired() {
        return (musicRole.equalsIgnoreCase("default_value") && guild.getRolesByName(musicRole, true).size() > 0);
    }

    public String getMusicRole() {
        return musicRole;
    }

    public Result setRole(String newRole) {
        this.musicRole = newRole;
        return Rixa.getInstance().getData().update("music", "music_role", "guild_id", newRole, guild.getId());
    }

    public Guild getGuild() {
        return guild;
    }

    public boolean checkExists() {
        return Rixa.getInstance().getData().exists("SELECT `enabled` FROM `music` WHERE `guild_id` = '" + guild.getId() + "'") == Result.TRUE;
    }
}
