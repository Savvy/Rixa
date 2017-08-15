package me.savvy.rixa.modules.levels;

import lombok.Getter;
import lombok.Setter;
import me.savvy.rixa.Rixa;
import me.savvy.rixa.commands.handlers.RixaPermission;
import me.savvy.rixa.enums.Result;
import me.savvy.rixa.guild.RixaGuild;
import me.savvy.rixa.guild.user.UserData;
import me.savvy.rixa.modules.RixaModule;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Timber on 5/23/2017.
 */
public class LevelsModule implements RixaModule {

    @Getter private final RixaGuild rixaGuild;
    @Getter private Map<String, UserData> userData = new HashMap<>();
    @Getter private boolean enabled;

    public LevelsModule(RixaGuild rixaGuild) {
        this.rixaGuild = rixaGuild;
        enabled = true;
        load();
    }

    @Override
    public String getName() {
        return "Levels";
    }

    @Override
    public String getDescription() {
        return "Rixa levels module.";
    }

    public void registerUser(UserData userData) {
        if (getUserData().containsKey(userData.getUser().getId())) {
            return;
        }
        getUserData().put(userData.getUser().getId(), userData);
    }

    public UserData getUserData(String key) {
        checkUser(key);
        return getUserData().get(key);
    }

    private void checkUser(String key) {
        if (getUserData().containsKey(key)) {
            return;
        }
        new UserData
                (getRixaGuild().getGuild().getJDA().getUserById(key),
                        getRixaGuild().getGuild());
    }

    private void load() {
        if (!(checkExists())) {
            this.enabled = true;
            insert();
        }
        String query = "SELECT `levels` FROM `modules` WHERE `guild_id` = ?;";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = Rixa.getDbManager().getConnection().prepareStatement(query);
                ps.setString(1, getRixaGuild().getGuild().getId());
            rs = Rixa.getDbManager().getObject(ps);
            this.enabled = rs.getBoolean("levels");
            ps.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean checkExists() {
        String query = "SELECT `%s` FROM `%s` WHERE `%s` = '%s';";
        Result r = Result.ERROR;
        try {
            r = Rixa.getDbManager().checkExists(String.format
                    (query, "guild_id", "modules", "guild_id", rixaGuild.getGuild().getId()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return r == Result.TRUE;
    }

    private void insert() {
        String query = "INSERT INTO `%s` (`%s`) VALUES ('%s');";
        Rixa.getDbManager()
                .insert(String.format(query, "modules", "guild_id", rixaGuild.getGuild().getId()));
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        Rixa.getData().update("modules", "enabled", "guild_id", enabled, rixaGuild.getGuild().getId());
    }
}
