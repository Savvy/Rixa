package me.savvy.rixa.guild.management;

import me.savvy.rixa.Rixa;
import me.savvy.rixa.enums.Result;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Timber on 5/23/2017.
 */
public class GuildSettings {

    private Guild guild;
    private boolean enlisted, joinVerification;
    private String prefix = "/", defaultRole, muteRole, joinMessage, quitMessage, joinPrivateMessage, description;
    private TextChannel joinMessageChannel, quitMessageChannel;

    public GuildSettings(Guild guild) {
        this.guild = guild;
        try {
            load();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void load() throws SQLException {
        if(!checkExists()) {
            Rixa.getInstance().getDbManager().insert("INSERT INTO `settings` (`guild_id`, `log_enabled`, `log_channel`, `joinMessage`, `quitMessage`, `greetings`, `farewell`," +
                    " `prefix`, `joinPm`, `joinVerification`, `defaultRole`, `muteRole`)" +
                    " VALUES ('" + guild.getId() + "', '0', 'default_value', 'default_value', 'default_value', 'default_value', 'default_value', '/'," +
                    " 'default', '0', 'default_value', 'default_value');");
            return;
        }
        PreparedStatement ps = Rixa.getInstance().getDbManager()
                .getConnection().prepareStatement("SELECT * FROM `settings` WHERE `guild_id` = ?");
        ps.setString(1, guild.getId());
        ResultSet set = Rixa.getInstance().getDbManager().getObject(ps);
        this.prefix = (set.getString("prefix"));
        this.defaultRole = (set.getString("defaultRole"));
        this.joinMessage = (set.getString("joinMessage"));
        this.quitMessage = (set.getString("quitMessage"));
        this.joinPrivateMessage = (set.getString("joinPM"));
        this.muteRole = (set.getString("muteRole"));
        this.joinVerification = (set.getBoolean("joinVerification"));
        if(!set.getString("greetings").equalsIgnoreCase("default_value")) {
            joinMessageChannel = guild.getTextChannelById(set.getString("greetings"));
        }
        if(!set.getString("farewell").equalsIgnoreCase("default_value")) {
            quitMessageChannel = guild.getTextChannelById(set.getString("farewell"));
        }
        ps = Rixa.getInstance().getDbManager()
                .getConnection().prepareStatement("SELECT * FROM `core` WHERE `guild_id` = ?");
        ps.setString(1, guild.getId());
        set = Rixa.getInstance().getDbManager().getObject(ps);
        this.description = (set.getString("description"));
        this.enlisted = (set.getBoolean("enlisted"));
    }

    private boolean checkExists() {
        return Rixa.getInstance().getDbManager().checkExists("SELECT `guild_id` FROM `settings` WHERE `guild_id` = '" + guild.getId() + "'") == Result.TRUE;
    }

    public void unload() {

    }

    public Guild getGuild() {
        return guild;
    }

    public String getJoinMessage() {
        return joinMessage;
    }

    public void setJoinMessage(String joinMessage) {
        this.joinMessage = joinMessage;
        Rixa.getInstance().getData().update("settings", "joinMessage", "guild_id", joinMessage, guild.getId());
    }

    public String getQuitMessage() {
        return quitMessage;
    }

    public void setQuitMessage(String quitMessage) {
        this.quitMessage = quitMessage;
        Rixa.getInstance().getData().update("settings", "quitMessage", "guild_id", quitMessage, guild.getId());
    }

    public String getJoinPrivateMessage() {
        return joinPrivateMessage;
    }

    public void setJoinPrivateMessage(String joinPrivateMessage) {
        this.joinPrivateMessage = joinPrivateMessage;
        Rixa.getInstance().getData().update("settings", "joinPM", "guild_id", joinPrivateMessage, guild.getId());
    }

    public TextChannel getJoinMessageChannel() {
        return joinMessageChannel;
    }

    public void setJoinMessageChannel(TextChannel joinMessageChannel) {
        this.joinMessageChannel = joinMessageChannel;
        Rixa.getInstance().getData().update("settings", "greetings", "guild_id", joinMessageChannel.getId(), guild.getId());
    }

    public TextChannel getQuitMessageChannel() {
        return quitMessageChannel;
    }

    public void setQuitMessageChannel(TextChannel quitMessageChannel) {
        this.quitMessageChannel = quitMessageChannel;
        Rixa.getInstance().getData().update("settings", "farewell", "guild_id", quitMessageChannel.getId(), guild.getId());
    }

    public String getDefaultRole() {
        return defaultRole;
    }

    public void setDefaultRole(String defaultRole) {
        this.defaultRole = defaultRole;
        Rixa.getInstance().getData().update("settings", "defaultRole", "guild_id", defaultRole, guild.getId());
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
        Rixa.getInstance().getData().update("settings", "prefix", "guild_id", prefix, guild.getId());
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        Rixa.getInstance().getData().update("core", "description", "guild_id", description, guild.getId());
    }

    public boolean isEnlisted() {
        return enlisted;
    }

    public void setEnlisted(boolean enlisted) {
        this.enlisted = enlisted;
        Rixa.getInstance().getData().update("core", "enlisted", "guild_id", enlisted, guild.getId());
    }

    public String getMuteRole() {
        return muteRole;
    }

    public void setMuteRole(String muteRole) {
        this.muteRole = muteRole;
        Rixa.getInstance().getData().update("settings", "muteRole", "guild_id", muteRole, guild.getId());
    }

    public boolean isJoinVerification() {
        return joinVerification;
    }

    public void setJoinVerification(boolean joinVerification) {
        this.joinVerification = joinVerification;
        Rixa.getInstance().getData().update("settings", "joinVerification", "guild_id", joinVerification, guild.getId());
    }
}
