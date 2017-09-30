package me.savvy.rixa.guild.management;

import lombok.Getter;
import lombok.Setter;
import me.majrly.database.statements.Query;
import me.majrly.database.statements.Update;
import me.savvy.rixa.Rixa;
import me.savvy.rixa.enums.Result;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
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

    public GuildSettings(Guild guild) {
        this.guild = guild;
        try {
            load();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void load() throws SQLException {
        if (!checkExists()) {
            Update update = new Update("INSERT INTO settings(guild_id, log_enabled, log_channel, joinMessage, quitMessage, greetings, farewell, prefix, joinPm, joinVerification, defaultRole, muteRole) VALUES ('" + guild.getId() + "', '0', 'default_value', 'default_value', 'default_value', 'default_value', 'default_value', '/', 'default', '0', 'default_value', 'default_value');");
            Rixa.getDatabase().send(update);
            return;
        }
        Query query = new Query("SELECT * FROM `settings` WHERE `guild_id` = ?");
        query.setString(guild.getId());
        Optional<?> optional = Rixa.getDatabase().send(query);
        if (!optional.isPresent()) {
            if (!(optional.get() instanceof ResultSet)) {
                Rixa.getInstance().getLogger().severe("Could not find " + guild.getName() + " it wasn't an instance of result set!, GuildSettings:61");
                return;
            }
            Rixa.getInstance().getLogger().severe("Could not find " + guild.getName() + ", GuildSettings:60");
            return;
        }
        ResultSet set = (ResultSet) optional.get();
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
        query = new Query("SELECT * FROM `core` WHERE `guild_id` = ?");
        query.setString(guild.getId());
        optional = Rixa.getDatabase().send(query);
        if (!optional.isPresent()) return;
        if (!(optional.get() instanceof ResultSet)) return;
        set = (ResultSet) optional.get();
        if (set.next()) {
            this.description = (set.getString("description"));
            this.enlisted = (set.getBoolean("enlisted"));
        }
        this.raidMode = false;
    }

    private boolean checkExists() {
        Result r = Result.FALSE;
        try {
            Query query = new Query("SELECT `guild_id` FROM `settings` WHERE `guild_id` = '" + guild.getId() + "'");
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

    public void unload() {

    }

    public void setJoinMessage(String joinMessage) {
        this.joinMessage = joinMessage;
        update("settings", "joinMessage", "guild_id", joinMessage, guild.getId());
    }

    public void setQuitMessage(String quitMessage) {
        this.quitMessage = quitMessage;
        update("settings", "quitMessage", "guild_id", quitMessage, guild.getId());
    }

    public void setJoinPrivateMessage(String joinPrivateMessage) {
        this.joinPrivateMessage = joinPrivateMessage;
        update("settings", "joinPM", "guild_id", joinPrivateMessage, guild.getId());
    }

    public void setJoinMessageChannel(TextChannel joinMessageChannel) {
        this.joinMessageChannel = joinMessageChannel;
        update("settings", "greetings", "guild_id", joinMessageChannel.getId(), guild.getId());
    }

    public void setJoinMessageChannel(String joinMessageChannel) {
        if (joinMessageChannel.equalsIgnoreCase("default_value")) this.joinMessageChannel = null;
        update("settings", "greetings", "guild_id", joinMessageChannel, guild.getId());
    }

    private void update(String table, String setting, String key, Object placeholder, Object placeholder2) {
        Update update = new Update("UPDATE `" + table + "` SET `" + setting + "` = ? WHERE `" + key + "` = ?;");
        update.setObject(placeholder);
        update.setObject(placeholder2);
        Rixa.getDatabase().send(update);
    }

    public void setQuitMessageChannel(TextChannel quitMessageChannel) {
        this.quitMessageChannel = quitMessageChannel;
        update("settings", "farewell", "guild_id", quitMessageChannel.getId(), guild.getId());
    }

    public void setQuitMessageChannel(String quitMessageChannel) {
        if (quitMessageChannel.equalsIgnoreCase("default_value")) this.quitMessageChannel = null;
        update("settings", "greetings", "guild_id", quitMessageChannel, guild.getId());
    }

    public void setDefaultRole(String defaultRole) {
        this.defaultRole = defaultRole;
        update("settings", "defaultRole", "guild_id", defaultRole, guild.getId());
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
        update("settings", "prefix", "guild_id", prefix, guild.getId());
    }

    public void setDescription(String description) {
        this.description = description;
        update("core", "description", "guild_id", description, guild.getId());
    }

    public void setEnlisted(boolean enlisted) {
        this.enlisted = enlisted;
        update("core", "enlisted", "guild_id", enlisted, guild.getId());
    }

    public void setMuteRole(String muteRole) {
        this.muteRole = muteRole;
        update("settings", "muteRole", "guild_id", muteRole, guild.getId());
    }

    public void setJoinVerification(boolean joinVerification) {
        this.joinVerification = joinVerification;
        update("settings", "joinVerification", "guild_id", joinVerification, guild.getId());
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