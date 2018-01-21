package io.rixa.bot.guild.settings;

import io.rixa.bot.data.storage.DatabaseAdapter;
import io.rixa.bot.guild.RixaGuild;
import io.rixa.bot.guild.manager.RixaSettings;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Settings implements RixaSettings {

    @Getter @Setter private String prefix, joinMessage, quitMessage, joinPrivateMessage;
    @Getter @Setter private boolean joinVerification;
    @Getter @Setter private TextChannel greetings, farewell;
    @Getter @Setter private Role muteRole, defaultRole;
    @Getter private RixaGuild rixaGuild;

    public Settings(RixaGuild rixaGuild) {
        this.rixaGuild = rixaGuild;
        load();
    }

    @Override
    public void load() {
        if (!(DatabaseAdapter.getInstance().exists("settings", "guild_id", rixaGuild.getId()))) {
            DatabaseAdapter.getInstance().get().update
                    ("INSERT INTO settings(guild_id, log_enabled, log_channel, joinMessage, quitMessage, greetings, farewell," +
                                    " prefix, joinPm, joinVerification, defaultRole, muteRole) VALUES " +
                                    "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);",
                            rixaGuild.getId(), false, "default_value", "default_value", "default_value", "default_value",
                            "default_value", "!", "default", 0, "default_value", "default_value");
        }
        DatabaseAdapter.getInstance().get().query("SELECT * FROM `settings` WHERE `guild_id` = ?",
                new Object[] { rixaGuild.getId() }, (resultSet, i) -> {
                    setPrefix(resultSet.getString("prefix"));
                    setJoinMessage(resultSet.getString("joinMessage"));
                    setQuitMessage(resultSet.getString("quitMessage"));
                    setJoinPrivateMessage(resultSet.getString("joinPm"));
                    setJoinVerification(resultSet.getBoolean("joinVerification"));
                    String greetingsId = resultSet.getString("greetings");
                    String farewellId = resultSet.getString("farewell");
                    String defaultRoleId = resultSet.getString("defaultRole");
                    String muteRoleId = resultSet.getString("muteRole");
                    if (!greetingsId.equalsIgnoreCase("default_value") && rixaGuild.getGuild().getTextChannelById(greetingsId) != null) {
                        greetings = rixaGuild.getGuild().getTextChannelById(greetingsId);
                    }
                    if (!farewellId.equalsIgnoreCase("default_value") && rixaGuild.getGuild().getTextChannelById(farewellId) != null) {
                        farewell = rixaGuild.getGuild().getTextChannelById(farewellId);
                    }
                    if (!defaultRoleId.equalsIgnoreCase("default_value") && rixaGuild.getGuild().getRoleById(defaultRoleId) != null) {
                        defaultRole = rixaGuild.getGuild().getRoleById(defaultRoleId);
                    }
                    if (!muteRoleId.equalsIgnoreCase("default_value") && rixaGuild.getGuild().getRoleById(muteRoleId) != null) {
                        muteRole = rixaGuild.getGuild().getRoleById(muteRoleId);
                    }
                    return null;
                });
    }

    @Override
    public void save() {
        DatabaseAdapter.getInstance().get().update("UPDATE `settings` SET " +
                "`prefix` = ?, `joinMessage` = ?, `quitMessage` = ?, " +
                "`joinPm` = ?, `joinVerification` = ?, `greetings` = ?, " +
                "`farewell` = ?, `defaultRole` = ?, `muteRole` = ?;",
                prefix, joinMessage, quitMessage, joinPrivateMessage, joinVerification,
                ((greetings == null) ? "default_value" : greetings.getId()),
                ((farewell == null) ? "default_value" : farewell.getId()),
                ((defaultRole == null) ? "default_value" : defaultRole.getId()),
                ((muteRole == null) ? "default_value" : muteRole.getId()));
    }
}