package me.savvy.rixa.guild.management;

import me.savvy.rixa.Rixa;
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
        PreparedStatement ps = Rixa.getInstance().getDbManager()
                .getConnection().prepareStatement("SELECT * FROM `settings` WHERE `guild_id` = ?");
        ps.setString(1, guild.getId());
        ResultSet set = Rixa.getInstance().getDbManager().getObject(ps);
        setPrefix(set.getString("prefix"));
        setDefaultRole(set.getString("defaultRole"));
        setJoinMessage(set.getString("joinMessage"));
        setQuitMessage(set.getString("quitMessage"));
        setJoinPrivateMessage(set.getString("joinPM"));
        setMuteRole(set.getString("muteRole"));
        setJoinVerification(set.getBoolean("joinVerification"));
        setDescription((String)Rixa.getInstance().getData().get("guild_id", guild.getId(), "description", "core"));
        setEnlisted((boolean) Rixa.getInstance().getData().get("guild_id", guild.getId(), "enlisted", "core"));
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
    }

    public String getQuitMessage() {
        return quitMessage;
    }

    public void setQuitMessage(String quitMessage) {
        this.quitMessage = quitMessage;
    }

    public String getJoinPrivateMessage() {
        return joinPrivateMessage;
    }

    public void setJoinPrivateMessage(String joinPrivateMessage) {
        this.joinPrivateMessage = joinPrivateMessage;
    }

    public TextChannel getJoinMessageChannel() {
        return joinMessageChannel;
    }

    public void setJoinMessageChannel(TextChannel joinMessageChannel) {
        this.joinMessageChannel = joinMessageChannel;
    }

    public TextChannel getQuitMessageChannel() {
        return quitMessageChannel;
    }

    public void setQuitMessageChannel(TextChannel quitMessageChannel) {
        this.quitMessageChannel = quitMessageChannel;
    }

    public String getDefaultRole() {
        return defaultRole;
    }

    public void setDefaultRole(String defaultRole) {
        this.defaultRole = defaultRole;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isEnlisted() {
        return enlisted;
    }

    public void setEnlisted(boolean enlisted) {
        this.enlisted = enlisted;
    }

    public String getMuteRole() {
        return muteRole;
    }

    public void setMuteRole(String muteRole) {
        this.muteRole = muteRole;
    }

    public boolean isJoinVerification() {
        return joinVerification;
    }

    public void setJoinVerification(boolean joinVerification) {
        this.joinVerification = joinVerification;
    }
}
