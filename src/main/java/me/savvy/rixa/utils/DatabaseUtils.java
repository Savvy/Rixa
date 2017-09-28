package me.savvy.rixa.utils;

import me.majrly.database.statements.Query;
import me.majrly.database.statements.Update;
import me.savvy.rixa.Rixa;
import me.savvy.rixa.enums.Result;
import net.dv8tion.jda.core.entities.Guild;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class DatabaseUtils {

    public static Result update(String table, String setting, String key, Object placeholder, Object placeholder2) {
        Update update = new Update("UPDATE `" + table + "` SET `" + setting + "` = ? WHERE `" + key + "` = ?;");
        update.setObject(placeholder);
        update.setObject(placeholder2);
        Rixa.getDatabase().send(update);
        return Result.TRUE;
    }

    public static boolean checkExists(String table, Guild guild) {
        Result r = Result.FALSE;
        try {
            Query query = new Query("SELECT `guild_id` FROM `" + table + "` WHERE `guild_id` = '" + guild.getId() + "';");
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
            System.out.println("INFO: Failed to check if exists : " + e.getLocalizedMessage());
            return false;
        }
    }
}