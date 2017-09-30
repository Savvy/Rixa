package me.savvy.rixa.guild.user;

import lombok.Getter;
import me.majrly.database.statements.Query;
import me.majrly.database.statements.Update;
import me.savvy.rixa.Rixa;
import me.savvy.rixa.enums.Result;
import me.savvy.rixa.guild.management.Guilds;
import me.savvy.rixa.modules.levels.LevelsModule;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
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
            Query query = new Query("SELECT * FROM `levels` WHERE `guild_id` = ? AND `user_id` = ?;");
            query.setString(guild.getId());
            query.setString(user.getId());
            Optional<?> optional = Rixa.getDatabase().send(query);
            if (!optional.isPresent()) return;
            if (!(optional.get() instanceof ResultSet)) return;
            ResultSet set = (ResultSet) optional.get();
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
        Result r = Result.FALSE;
        try {
            Query query = new Query("SELECT `user_id` FROM `levels` WHERE `guild_id` = ? AND `user_id` = ?;");
            query.setString(guild.getId());
            query.setString(user.getId());
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

    private void insert() {
        Update update = new Update("INSERT INTO `levels` (guild_id, user_id, experience) VALUES (?, ?, ?);");
        update.setString(guild.getId());
        update.setString(user.getId());
        update.setInteger(0);
        Rixa.getDatabase().send(update);
    }

    private void setExperience(int experience) {
        this.experience = experience;
        String query = "UPDATE `levels` SET `experience` = ? WHERE `guild_id` = ? AND `user_id` = ?;";
        Update update = new Update(query);
        update.setInteger(experience);
        update.setString(guild.getId());
        update.setString(user.getId());
        Rixa.getDatabase().send(update);
    }

    private int getRandom() {
        int i = random.nextInt(25);
        return (i > 15 && i < 25 ? i : getRandom());
    }
}
