package me.savvy.rixa.guild;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import me.savvy.rixa.Rixa;
import me.savvy.rixa.commands.handlers.RixaPermission;
import me.savvy.rixa.data.database.sql.DatabaseManager;
import me.savvy.rixa.enums.Result;
import me.savvy.rixa.guild.management.GuildSettings;
import me.savvy.rixa.modules.music.MusicModule;
import me.savvy.rixa.modules.twitter.TwitterModule;
import net.dv8tion.jda.core.entities.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Timber on 5/23/2017.
 */
public class RixaGuild {
    
    @Getter private Guild guild;
    private DatabaseManager db;
    @Setter private GuildSettings guildSettings;
    @Getter @Setter private MusicModule musicModule;
    @Getter @Setter private TwitterModule twitterModule;
    @Getter private List<String> mutedMembers = new ArrayList<>();

    public RixaGuild(Guild guild) {
        this.guild = guild;
        this.db = Rixa.getDbManager();
        setMusicModule(new MusicModule(guild));
        load();
    }

    private void load() {
        if(!(checkExists())) {
            Rixa.getDbManager()
                    .insert("INSERT INTO `core` (`guild_id`, `guild_name`, `description`, `keywords`) VALUES ('%id%', '%name%', 'Description not set.', 'No Keywords Found.')"
                            .replace("%id%", guild.getId())
                            .replace("%name%", guild.getName().replace("'", "\\'")));
        }
        setGuildSettings(new GuildSettings(this.guild));
        addGuild(this);
    }

    public GuildSettings getGuildSettings() {
        return (guildSettings == null) ? this.guildSettings = new GuildSettings(getGuild()) : guildSettings;
    }
    
    private boolean checkExists() {
        Result r = Rixa.getDbManager().checkExists("SELECT `guild_name` FROM `core` WHERE `guild_id` = '" + guild.getId() + "';");
        return r == Result.TRUE;
    }
    
    public boolean hasPermission(Member member, RixaPermission permission) {
        if(Rixa.getConfig().getJsonObject().getJSONArray("botAdmins").toList().contains(member.getUser().getId()) ||
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
            ps.executeUpdate();
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

    @Getter
    private static Map<String, RixaGuild> guilds = new HashMap<>();

    public static void addGuild(RixaGuild guild) {
        if(check(guild.getGuild())) return;
        guilds.put(guild.getGuild().getId(), guild);
    }

    public static RixaGuild getGuild(Guild guild) {
        if(!check(guild)) {
            addGuild(new RixaGuild(guild));
        }
        return guilds.get(guild.getId());
    }

    public static void removeGuild(RixaGuild guild) {
        if(!check(guild.getGuild())) return;
        guilds.remove(guild.getGuild().getId());
    }

    private static boolean check(Guild guild) {
        return guilds.containsKey(guild.getId());
    }
    
}
