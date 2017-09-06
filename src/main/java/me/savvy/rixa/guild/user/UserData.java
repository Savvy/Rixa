package me.savvy.rixa.guild.user;

import lombok.Getter;
import lombok.Setter;
import me.savvy.rixa.Rixa;
import me.savvy.rixa.enums.Result;
import me.savvy.rixa.guild.RixaGuild;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by savit on 7/14/2017.
 */
public class UserData {
    @Getter private final Guild guild;
    @Getter private User user;
    @Getter private int experience;
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
        RixaGuild.getGuild(guild).getLevelsModule().registerUser(userData);
    }

    private void load() {
        if(!checkExists()) {
            insert();
            setExperience(0);
            return;
        }
        String query = "SELECT * FROM `%s` WHERE `%s` = '%s' AND `%s` = '%s';";
        PreparedStatement ps;
            ResultSet rs;
            try {
                ps = Rixa.getDbManager().getConnection().prepareStatement(String.format
                        (query, "levels", "guild_id",
                                guild.getId(), "user_id",
                                user.getId()));
            rs = Rixa.getDbManager().getObject(ps);
            setExperience(rs.getInt("experience"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getLevel() {
        return getLevelFromExperience(getExperience());
    }

    public boolean awardIfCan() {
        if(awardedLast) {
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
        String query = "SELECT `%s` FROM `%s` WHERE `%s` = '%s' AND `%s` = '%s';";
        Result r;
        try {
            r = Rixa.getDbManager().checkExists(String.format
                    (query, "user_id", "levels", "guild_id",
                            guild.getId(), "user_id",
                            user.getId()));
        return r == Result.TRUE;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void insert() {
    String query = "INSERT INTO `%s` (`%s`,`%s`,`%s`) VALUES ('%s', '%s', '%s');";
        Rixa.getDbManager()
                .insert(String.format(query, "levels", "guild_id", "user_id", "experience",
                        guild.getId(), user.getId(), "0"));
    }

    private void setExperience(int experience) {
        this.experience = experience;
        String query = "UPDATE `%s` SET `%s` = '%s' WHERE `%s` = '%s' AND `%s` = '%s';";
        try {
            PreparedStatement ps = Rixa.getDbManager().getConnection().prepareStatement(String.format
                        (query, "levels", "experience", experience, "guild_id",
                                guild.getId(), "user_id",
                                user.getId()));
            Rixa.getDbManager().executeUpdate(ps);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getRandom() {
        int i = random.nextInt(25);
        return (i > 15 && i < 25 ? i : getRandom());
    }
}
