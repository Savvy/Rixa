package me.savvy.rixa.guild.user;

import lombok.Getter;
import me.savvy.rixa.Rixa;
import me.savvy.rixa.enums.Result;
import me.savvy.rixa.guild.management.Guilds;
import me.savvy.rixa.modules.levels.LevelsModule;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by savit on 7/14/2017.
 */
public class UserData {
    @Getter
    private final Guild guild;
    @Getter
    private User user;
    @Getter
    private int experience;
    private boolean awardedLast;
    private Random random;

    public UserData(User user, Guild guild) {
        this.user = user;
        this.guild = guild;
        awardedLast = false;
        random = new Random();
        load();
        register(this);
    }

    private void register(UserData userData) {
        ((LevelsModule) Guilds.getGuild(guild).getModule("Levels")).registerUser(userData);
    }

    private void load() {
        if (!checkExists()) {
            insert();
            setExperience(0);
            return;
        }
        try {
            PreparedStatement statement = Rixa.getDatabase().getPreparedStatement("SELECT * FROM `levels` WHERE `guild_id` = ? AND `user_id` = ?;");
            statement.setString(1, guild.getId());
            statement.setString(2, user.getId());
            ResultSet set = statement.executeQuery();
            if (set.next()) {
                setExperience(set.getInt("experience"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getLevel() {
        return getLevelFromExperience(getExperience());
    }

    public boolean awardIfCan() {
        if (awardedLast) {
            return false;
        }
        int amountAdding = getRandom();
        int currentLevel = getLevelFromExperience(getExperience());
        setExperience(getExperience() + amountAdding);
        awardedLast = true;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                awardedLast = false;
            }
        }, 1000 * 60);
        return currentLevel < getLevelFromExperience(getExperience());
    }

    public int getLevelFromExperience(int xp) {
        int level = 0;
        while (xp >= this.getNeededXP(level)) {
            xp -= this.getNeededXP(level);
            level++;
        }
        return level;
    }

    public Double getNeededXP(double n) {
        if (n < 0) return 0.0;
        return (6 * Math.pow(n, 3) + 119 * n + 100);
    }

    public int getRemainingExperience() {
        int xp = this.getExperience();
        int level = getLevelFromExperience(xp);

        for (int i = 0; i < level; i++) {
            xp -= this.getNeededXP(i);
        }
        return xp;
    }

    private boolean checkExists() {
        Result r;
        try {
            PreparedStatement statement = Rixa.getDatabase().getPreparedStatement("SELECT `user_id` FROM `levels` WHERE `guild_id` = ? AND `user_id` = ?;");
            statement.setString(1, guild.getId());
            statement.setString(2, user.getId());
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

    private void insert() {
        try {
            PreparedStatement statement = Rixa.getDatabase().getPreparedStatement("INSERT INTO `levels` (guild_id, user_id, experience) VALUES (?, ?, ?);");
            statement.setString(1, guild.getId());
            statement.setString(2, user.getId());
            statement.setInt(3, 0);
            statement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void setExperience(int experience) {
        this.experience = experience;
        String query = "UPDATE `levels` SET `experience` = ? WHERE `guild_id` = ? AND `user_id` = ?;";
        try {
            PreparedStatement ps = Rixa.getDatabase().getPreparedStatement(query);
            ps.setInt(1, experience);
            ps.setString(2, guild.getId());
            ps.setString(3, user.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getRandom() {
        int i = random.nextInt(25);
        return (i > 15 && i < 25 ? i : getRandom());
    }
}
