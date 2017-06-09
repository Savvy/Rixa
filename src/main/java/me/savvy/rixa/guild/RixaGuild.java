package me.savvy.rixa.guild;

import me.savvy.rixa.Rixa;
import me.savvy.rixa.commands.handlers.RixaPermission;
import me.savvy.rixa.data.database.sql.DatabaseManager;
import me.savvy.rixa.guild.management.GuildSettings;
import me.savvy.rixa.modules.music.MusicModule;
import net.dv8tion.jda.core.entities.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Timber on 5/23/2017.
 */
public class RixaGuild {

    private Guild guild;
    private DatabaseManager db;
    private GuildSettings guildSettings;
    private MusicModule musicModule;
    private List<String> mutedMembers = new ArrayList<>();

    public RixaGuild(Guild guild) {
        this.guild = guild;
        this.db = Rixa.getInstance().getDbManager();
        setMusicModule(new MusicModule(guild));
        load();
    }

    private void load() {
        if(check()) return;
        setGuildSettings(new GuildSettings(this.guild));
        RixaManager.addGuild(this);
    }

    public GuildSettings getGuildSettings() {
        return (guildSettings == null) ? this.guildSettings = new GuildSettings(getGuild()) : guildSettings;
    }

    public void setGuildSettings(GuildSettings guildSettings) {
        this.guildSettings = guildSettings;
    }

    /**
     * TODO: Check if Guild exists in database if not create new instance;
     */
    public boolean check() {
        return guildSettings == null;
    }

    public Guild getGuild() {
        return guild;
    }

    public boolean hasPermission(Member member, RixaPermission permission) {
        if(Rixa.getInstance().getConfig().getConfig().getStringList("botAdmins").contains(member.getUser().getId()) ||
                member.getUser().getId().equals(guild.getOwner().getUser().getId())) {
            return true;
        }
        for(Role role: member.getRoles()) {
            if(hasPermission(role, permission)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasPermission(Role role, RixaPermission permission) {
        if(!permissionExists(role)) {
            return false;
        }
        try {
            PreparedStatement ps =
                    db.getConnection().prepareStatement
                            ("SELECT `" + permission.toString().toUpperCase() + "` FROM `permissions` WHERE `role_id` = ?");
            ps.setString(1, role.getId());
            ResultSet rs = db.getObject(ps);
            return rs.getBoolean(permission.toString().toUpperCase());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setPermission(Role role, RixaPermission permission, boolean value) {
        if(!permissionExists(role)) {
            db
                    .insert("INSERT INTO `permissions` " +
                            "(`role_id`, `guild_id`, `MUTE`, `ADD_ROLE`, `REMOVE_ROLE`, `CLEAR_CHAT`, " +
                            "`ACCESS_CONFIG`, `PM_MESSAGE`, `KICK_MEMBER`, `BAN_MEMBER`)" +
                            " VALUES ('" + role.getId() + "', '" + guild.getId() + "', '0', '0', '0', '0', '0', '0', '0', '0');");
        }
        try {
            PreparedStatement ps = db.getConnection().prepareStatement
                    ("UPDATE `permissions` SET `" + permission.toString().toUpperCase() + "` = ? WHERE `guild_id` = ? AND `role_id` = ?;");
            ps.setBoolean(1, value);
            ps.setString(2, guild.getId());
            ps.setString(3, role.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean permissionExists(Role role) {
        ResultSet rs = db.executeQuery
                ("SELECT `" + RixaPermission.values()[0] + "` FROM `permissions` WHERE `guild_id` = '" + guild.getId() + "' AND `role_id` = '" + role.getId() + "'");
        try {
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isUserMuted(User user) {
        return mutedMembers.contains(user.getId());
    }

    public void unmuteMember(User user) {
        mutedMembers.remove(user.getId());
    }

    public void muteMember(User user) {
        if(!isUserMuted(user))
        mutedMembers.add(user.getId());
    }

    public MusicModule getMusicModule() {
        return musicModule;
    }

    public void setMusicModule(MusicModule musicModule) {
        this.musicModule = musicModule;
    }
}
