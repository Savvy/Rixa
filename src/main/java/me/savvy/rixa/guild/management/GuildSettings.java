package me.savvy.rixa.guild.management;

import lombok.Getter;
import lombok.Setter;
import me.savvy.rixa.Rixa;
import me.savvy.rixa.data.database.sql.SQLBuilder;
import me.savvy.rixa.enums.Result;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Timber on 5/23/2017.
 */
public class GuildSettings {
    @Getter
    private Guild guild;
    @Getter
    private boolean enlisted, joinVerification;
    @Getter
    private String prefix = "/", defaultRole, muteRole, joinMessage, quitMessage, joinPrivateMessage, description, currency;
    @Getter
    private TextChannel joinMessageChannel, quitMessageChannel;
    @Getter
    @Setter
    Guild.VerificationLevel defaultVerificationLevel;
    @Getter
    @Setter
    long lastJoin;
    private boolean raidMode;

    private SQLBuilder db;
    public GuildSettings(Guild guild) {
        this.guild = guild;
        try {
            load();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void load() throws SQLException {
        db = Rixa.getDatabase();
        if (!checkExists()) {
            PreparedStatement statement = db.getPreparedStatement
                    ("INSERT INTO settings(guild_id, log_enabled, log_channel, joinMessage, quitMessage, greetings, farewell, prefix, joinPm, joinVerification, defaultRole, muteRole) VALUES " +
                            "(?, '0', 'default_value', 'default_value', 'default_value', 'default_value', 'default_value', '/', 'default', '0', 'default_value', 'default_value');");
            statement.setString(1, guild.getId());
            statement.executeUpdate();
            return;
        }
        PreparedStatement statement = db.getPreparedStatement("SELECT * FROM `settings` WHERE `guild_id` = ?");
        statement.setString(1, guild.getId());
        ResultSet set = statement.executeQuery();
        if (set.next()) {
        this.prefix = (set.getString("prefix"));
        this.defaultRole = (set.getString("defaultRole"));
        this.joinMessage = (set.getString("joinMessage"));
        this.quitMessage = (set.getString("quitMessage"));
        this.joinPrivateMessage = (set.getString("joinPM"));
        this.muteRole = (set.getString("muteRole"));
        this.joinVerification = (set.getBoolean("joinVerification"));
        if (!set.getString("greetings").equalsIgnoreCase("default_value")) {
            joinMessageChannel = guild.getTextChannelById(set.getString("greetings"));
        }
        if (!set.getString("farewell").equalsIgnoreCase("default_value")) {
            quitMessageChannel = guild.getTextChannelById(set.getString("farewell"));
        }
    }
        statement = db.getPreparedStatement("SELECT * FROM `core` WHERE `guild_id` = ?");
        statement.setString(1, guild.getId());
        set = statement.executeQuery();
        if (set.next()) {
            this.description = (set.getString("description"));
            this.enlisted = (set.getBoolean("enlisted"));
        }
        this.raidMode = false;
    }

    private boolean checkExists() {
        Result r;
        try {
            PreparedStatement statement = db.getPreparedStatement
                    ("SELECT `guild_id` FROM `settings` WHERE `guild_id` = ?");
            statement.setString(1, guild.getId());
            ResultSet set = statement.executeQuery();
                if (set.next()) {
                    r = Result.TRUE;
                } else {
                    r = Result.FALSE;
                }
            set.close();
            return r == Result.TRUE;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void unload() {

    }

    public void setJoinMessage(String joinMessage) {
        this.joinMessage = joinMessage;
        try {
            update("settings", "joinMessage", "guild_id", joinMessage, guild.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setQuitMessage(String quitMessage) {
        this.quitMessage = quitMessage;
        try {
            update("settings", "quitMessage", "guild_id", quitMessage, guild.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setJoinPrivateMessage(String joinPrivateMessage) {
        this.joinPrivateMessage = joinPrivateMessage;
        try {
            update("settings", "joinPM", "guild_id", joinPrivateMessage, guild.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setJoinMessageChannel(TextChannel joinMessageChannel) {
        this.joinMessageChannel = joinMessageChannel;
        try {
            update("settings", "greetings", "guild_id", joinMessageChannel.getId(), guild.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setJoinMessageChannel(String joinMessageChannel) {
        if (joinMessageChannel.equalsIgnoreCase("default_value")) this.joinMessageChannel = null;
        try {
            update("settings", "greetings", "guild_id", joinMessageChannel, guild.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void update(String table, String setting, String key, Object placeholder, Object placeholder2) throws SQLException {
        PreparedStatement statement = db.getPreparedStatement("UPDATE ? SET ? = ? WHERE ? = ?;");
        statement.setString(1, table);
        statement.setString(2, setting);
        statement.setObject(3, placeholder);
        statement.setString(4, key);
        statement.setObject(5, placeholder2);
        statement.executeUpdate();
        statement.close();
    }

    public void setQuitMessageChannel(TextChannel quitMessageChannel) {
        this.quitMessageChannel = quitMessageChannel;
        try {
            update("settings", "farewell", "guild_id", quitMessageChannel.getId(), guild.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setQuitMessageChannel(String quitMessageChannel) {
        if (quitMessageChannel.equalsIgnoreCase("default_value")) this.quitMessageChannel = null;
        try {
            update("settings", "greetings", "guild_id", quitMessageChannel, guild.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setDefaultRole(String defaultRole) {
        this.defaultRole = defaultRole;
        try {
            update("settings", "defaultRole", "guild_id", defaultRole, guild.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
        try {
            update("settings", "prefix", "guild_id", prefix, guild.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setDescription(String description) {
        this.description = description;
        try {
            update("core", "description", "guild_id", description, guild.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setEnlisted(boolean enlisted) {
        this.enlisted = enlisted;
        try {
            update("core", "enlisted", "guild_id", enlisted, guild.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setMuteRole(String muteRole) {
        this.muteRole = muteRole;
        try {
            update("settings", "muteRole", "guild_id", muteRole, guild.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setJoinVerification(boolean joinVerification) {
        this.joinVerification = joinVerification;
        try {
            update("settings", "joinVerification", "guild_id", joinVerification, guild.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void startRaidMode() {
        this.raidMode = true;
        setDefaultVerificationLevel(guild.getVerificationLevel());
        guild.getManager().setVerificationLevel(Guild.VerificationLevel.HIGH).queue();
        raidModeScheduler();
    }

    public void endRaidMode() {
        this.raidMode = false;
        guild.getManager().setVerificationLevel(getDefaultVerificationLevel()).queue();
        setDefaultVerificationLevel(null);
    }

    public void raidModeScheduler() {
        ScheduledExecutorService scheduler = Rixa.getInstance().getExecutorService();
        scheduler.scheduleWithFixedDelay(new TimerTask() {
            @Override
            public void run() {
                if (isRaidMode()) {
                    endRaidMode();
                }
                this.cancel();
            }
        }, 0, 5, TimeUnit.MINUTES);
    }

    public boolean isRaidMode() {
        return raidMode;
    }
}