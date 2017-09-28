package me.savvy.rixa.modules.music;

import lombok.Getter;
import lombok.Setter;
import me.majrly.database.Database;
import me.majrly.database.statements.Query;
import me.majrly.database.statements.Update;
import me.savvy.rixa.Rixa;
import me.savvy.rixa.guild.RixaGuild;
import me.savvy.rixa.modules.RixaModule;
import me.savvy.rixa.utils.DatabaseUtils;
import net.dv8tion.jda.core.entities.Guild;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Created by Timber on 5/23/2017.
 */
public class MusicModule implements RixaModule {

    private Database db;
    @Getter
    @Setter
    private boolean enabled;
    @Getter
    @Setter
    private String musicRole;
    @Getter
    private Guild guild;

    @Override
    public void load(RixaGuild rixaGuild) {
        try {
            this.guild = rixaGuild.getGuild();
            this.enabled = false;
            this.musicRole = "default_value";
            db = Rixa.getDatabase();
            if (!DatabaseUtils.checkExists("music", guild)) {
                Update update = new Update("INSERT INTO `music` (`guild_id`, `music_role`, `enabled`) VALUES ('" + guild.getId() + "', 'default_value', '0');");
                db.send(update);
            }
            Query query = new Query("SELECT * FROM `modules` WHERE `guild_id` = ?");
            query.setString(guild.getId());
            Optional<?> optional = Rixa.getDatabase().send(query);
            if (!optional.isPresent()) return;
            if (!(optional.get() instanceof ResultSet)) return;
            ResultSet set = (ResultSet) optional.get();
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
