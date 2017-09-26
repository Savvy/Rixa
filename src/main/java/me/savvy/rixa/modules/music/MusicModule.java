package me.savvy.rixa.modules.music;

import lombok.Getter;
import me.majrly.database.Database;
import me.majrly.database.statements.Query;
import me.majrly.database.statements.Update;
import me.savvy.rixa.Rixa;
import me.savvy.rixa.enums.Result;
import me.savvy.rixa.modules.RixaModule;
import me.savvy.rixa.utils.DatabaseUtils;
import net.dv8tion.jda.core.entities.Guild;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Created by Timber on 5/23/2017.
 */
public class MusicModule implements RixaModule {
    private Database db;
    @Getter
    private boolean enabled;
    @Getter
    private String musicRole;
    @Getter
    private Guild guild;

    public MusicModule(Guild guild) {
        this.guild = guild;
        this.enabled = false;
        this.musicRole = "default_value";
        db = Rixa.getDatabase();
        load();
    }

    public void load() {
        Update music = new Update("CREATE TABLE IF NOT EXISTS `music` (`guild_id` varchar(255) NOT NULL, `music_role` varchar(255) NOT NULL, `enabled` INT(11) NOT NULL, PRIMARY KEY (`guild_id`));");
        db.send(music);
        if (!checkExists()) {
            Update update = new Update("INSERT INTO `music` (`guild_id`, `music_role`, `enabled`)" +
                    " VALUES ('" + guild.getId() + "', 'default_value', '0');");
            db.send(update);
        }
        try {
            Query query = new Query("SELECT * FROM `modules` WHERE `guild_id` = ?");
            query.setString(guild.getId());
            Optional<?> optional = Rixa.getDatabase().send(query);
            if (!optional.isPresent()) return;
            if (!(optional.get() instanceof ResultSet)) return;
            ResultSet set = (ResultSet) optional.get();
            if (set.next()) {
                this.musicRole = set.getString("music_role");
                this.enabled = set.getBoolean("enabled");
            }
            set.close();
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


    public Result setEnabled(boolean val) {
        this.enabled = val;
        return DatabaseUtils.update("music", "enabled", "guild_id", val, guild.getId());
    }

    public boolean isRoleRequired() {
        return (!musicRole.equalsIgnoreCase("default_value"));
    }


    public Result setRole(String newRole) {
        this.musicRole = newRole;
        return DatabaseUtils.update("music", "music_role", "guild_id", newRole, guild.getId());
    }

    public boolean checkExists() {
        Result r = Result.FALSE;
        try {
            Query query = new Query("SELECT `guild_id` FROM `music` WHERE `guild_id` = '" +
                    guild.getId() + "';");
            Optional<?> optional = Rixa.getDatabase().send(query);
            if (!optional.isPresent()) r = Result.ERROR;
            if (!(optional.get() instanceof ResultSet)) r = Result.ERROR;
            ResultSet set = (ResultSet) optional.get();
            if (r != Result.ERROR) {
                if (set.next()) {
                    r = Result.TRUE;
                } else {
                    r = Result.FALSE;
                }
            }
            set.close();
            return r == Result.TRUE;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
