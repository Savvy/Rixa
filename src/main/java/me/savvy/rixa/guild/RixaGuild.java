package me.savvy.rixa.guild;

import lombok.Getter;
import lombok.Setter;
import me.majrly.database.Database;
import me.majrly.database.statements.Query;
import me.majrly.database.statements.Update;
import me.savvy.rixa.Rixa;
import me.savvy.rixa.commands.handlers.RixaPermission;
import me.savvy.rixa.enums.Result;
import me.savvy.rixa.guild.management.GuildSettings;
import me.savvy.rixa.guild.management.Guilds;
import me.savvy.rixa.modules.RixaModule;
import me.savvy.rixa.modules.conversations.ConversationModule;
import me.savvy.rixa.modules.levels.LevelsModule;
import me.savvy.rixa.modules.music.MusicModule;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * Created by Timber on 5/23/2017.
 * Edited by Majr on 9/22/2017
 */
public class RixaGuild {

    @Getter
    private Guild guild;
    private Database db;
    @Setter
    private GuildSettings guildSettings;
    @Getter
    private List<String> mutedMembers = new ArrayList<>();
    @Getter
    private HashMap<String, RixaModule> modules;

    public RixaGuild(Guild guild) {
        this.guild = guild;
        this.modules = new HashMap<>();
        this.db = Rixa.getDatabase();
        modules.put("Music", new MusicModule(this));
        modules.put("Levels", new LevelsModule(this));
        modules.put("Conversations", new ConversationModule(this));
        load();
    }

    public void load() {
        if (!(checkExists())) {
            Update update = new Update("INSERT INTO `core` (`guild_id`, `guild_name`, `description`, `keywords`) VALUES (?, ?, 'Description not set.', 'No Keywords Found.')");
            update.setString(guild.getId());
            update.setString(guild.getName());
            db.send(update);
        }
        setGuildSettings(new GuildSettings(this.guild));
        Guilds.addGuild(this);
    }

    public GuildSettings getGuildSettings() {
        return (guildSettings == null) ? this.guildSettings = new GuildSettings(getGuild()) : guildSettings;
    }

    private boolean checkExists() {
        Result r = Result.ERROR;

        try {
            Query query = new Query("SELECT `guild_name` FROM `core` WHERE `guild_id` = '" + guild.getId() + "';");
            Optional<?> optional = db.send(query);
            if (!optional.isPresent()) r = Result.ERROR;
            if (!(optional.get() instanceof ResultSet)) r = Result.ERROR;
            if (r != Result.ERROR) {
                ResultSet set = (ResultSet) optional.get();
                if (set.next()) {
                    r = Result.TRUE;
                } else {
                    r = Result.FALSE;
                }
                set.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return r == Result.TRUE;
    }

    public boolean hasPermission(Member member, RixaPermission permission) {
        if (Rixa.getConfig().getJsonObject().getJSONArray("botAdmins").toList().contains(member.getUser().getId()) ||
                member.getUser().getId().equals(guild.getOwner().getUser().getId())) {
            return true;
        }
        for (Role role : member.getRoles()) {
            if (hasPermission(role, permission)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasPermission(Role role, RixaPermission permission) {
        if (!permissionExists(role)) {
            return false;
        }

        boolean b = false;
        try {
            Query query = new Query("SELECT `" + permission.toString().toUpperCase() + "` FROM `permissions` WHERE `role_id` = ?");
            query.setString(role.getId());
            Optional<?> optional = db.send(query);
            if (!optional.isPresent()) return b;
            if (!(optional.get() instanceof ResultSet)) return b;
            ResultSet set = (ResultSet) optional.get();
            b = set.getBoolean(permission.toString().toUpperCase());
            set.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return b;
    }

    public void setPermission(Role role, RixaPermission permission, boolean value) {
        if (!permissionExists(role)) {
            Update update = new Update("INSERT INTO `permissions` " +
                    "(`role_id`, `guild_id`, `MUTE`, `ADD_ROLE`, `REMOVE_ROLE`, `CLEAR_CHAT`, " +
                    "`ACCESS_CONFIG`, `PM_MESSAGE`, `KICK_MEMBER`, `BAN_MEMBER`)" +
                    " VALUES (?, ?, '0', '0', '0', '0', '0', '0', '0', '0');");
            update.setString(role.getId());
            update.setString(guild.getId());
            db.send(update);
        }
        Update update = new Update("UPDATE `permissions` SET `" + permission.toString().toUpperCase() + "` = ? WHERE `guild_id` = ? AND `role_id` = ?;");
        update.setBoolean(value);
        update.setString(guild.getId());
        update.setString(role.getId());
        db.send(update);
    }

    private boolean permissionExists(Role role) {
        Query query = new Query("SELECT `" + RixaPermission.values()[0] + "` FROM `permissions` WHERE `guild_id` = ? AND `role_id` = ?");
        query.setString(guild.getId());
        query.setString(role.getId());
        Optional<?> optional = db.send(query);
        if (!optional.isPresent()) return false;
        if (!(optional.get() instanceof ResultSet)) return false;
        ResultSet set = (ResultSet) optional.get();
        try {
            return set.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                set.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isUserMuted(User user) {
        return mutedMembers.contains(user.getId());
    }

    public void unmuteMember(User user) {
        mutedMembers.remove(user.getId());
    }

    public void muteMember(User user) {
        if (!isUserMuted(user))
            mutedMembers.add(user.getId());
    }

    public void save() {
        for (RixaModule module : modules.values()) {
            if (!module.isEnabled()) {
                return;
            }
            module.save();
        }
    }

    public RixaModule getModule(String levels) {
        return this.modules.get(levels);
    }
}
