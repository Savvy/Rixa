package me.savvy.rixa.guild;

import lombok.Getter;
import lombok.Setter;
import me.savvy.rixa.Rixa;
import me.savvy.rixa.commands.handlers.RixaPermission;
import me.savvy.rixa.data.database.sql.SQLBuilder;
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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Timber on 5/23/2017.
 * Edited by Majr on 9/22/2017
 */
public class RixaGuild {

    @Getter
    private Guild guild;
    private SQLBuilder db;
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
            try {
                PreparedStatement ps = db.getPreparedStatement("INSERT INTO `core` (`guild_id`, `guild_name`, `description`, `keywords`) VALUES (?, ?, 'Description not set.', 'No Keywords Found.')\"");
                ps.setString(1, guild.getId());
                ps.setString(2, guild.getName());
                db.executeUpdate(ps);
            } catch (SQLException ignored) {}
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
            PreparedStatement ps = db.getPreparedStatement("SELECT `guild_name` FROM `core` WHERE `guild_id` = ?;");
            ps.setString(1, guild.getId());
            ResultSet set = ps.executeQuery();
            if (set.next()) {
                r = Result.TRUE;
            } else {
                r = Result.FALSE;
            }
            set.close();
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
            PreparedStatement ps = db.getPreparedStatement("SELECT ? FROM `permissions` WHERE `role_id` = ?");
            ps.setString(1, permission.toString().toUpperCase());
            ps.setString(2, role.getId());
            ResultSet set = ps.executeQuery();
            if (set.next()) {
                b = set.getBoolean(permission.toString().toUpperCase());
            }
            set.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return b;
    }

    public void setPermission(Role role, RixaPermission permission, boolean value) {
        if (!permissionExists(role)) {
            PreparedStatement ps = null;
            try {
                ps = db.getPreparedStatement("INSERT INTO `permissions` " +
                        "(`role_id`, `guild_id`, `MUTE`, `ADD_ROLE`, `REMOVE_ROLE`, `CLEAR_CHAT`, " +
                        "`ACCESS_CONFIG`, `PM_MESSAGE`, `KICK_MEMBER`, `BAN_MEMBER`)" +
                        " VALUES (?, ?, '0', '0', '0', '0', '0', '0', '0', '0');");
                ps.setString(1, role.getId());
                ps.setString(2, guild.getId());
                db.executeUpdate(ps);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            PreparedStatement ps = db.getPreparedStatement("UPDATE `permissions` SET ? = ? WHERE `guild_id` = ? AND `role_id` = ?;");
            ps.setString(1, permission.toString().toUpperCase());
            ps.setBoolean(2, value);
            ps.setString(3, guild.getId());
            ps.setString(4, role.getId());
            db.executeUpdate(ps);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private boolean permissionExists(Role role) {
        try {
            PreparedStatement query = db.getPreparedStatement("SELECT ? FROM `permissions` WHERE `guild_id` = ? AND `role_id` = ?");
            query.setString(1, RixaPermission.values()[0].toString().toUpperCase());
            query.setString(2, guild.getId());
            query.setString(3, role.getId());
            ResultSet set = query.executeQuery();
            boolean b = set.next();
            query.close();
            set.close();
            return b;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
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
