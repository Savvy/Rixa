package me.savvy.rixa.utils;

import me.savvy.rixa.Rixa;
import me.savvy.rixa.enums.Result;
import net.dv8tion.jda.core.entities.Guild;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseUtils {

    public static Result update(String table, String setting, String key, Object placeholder, Object placeholder2) {
        PreparedStatement statement = null;
        try {
            statement = Rixa.getDatabase().getPreparedStatement("UPDATE `" + table + "` SET `" + setting + "` = ? WHERE `" + key + "` = ?;");
        statement.setObject(1, placeholder);
        statement.setObject(2, placeholder2);
        statement.executeUpdate();
        statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return Result.FALSE;
        }
        return Result.TRUE;
    }

    public static boolean checkExists(String table, Guild guild) {
        try {
            PreparedStatement statement = Rixa.getDatabase().getPreparedStatement("SELECT `guild_id` FROM `" + table + "` WHERE `guild_id` = ?;");
            statement.setString(1, guild.getId());
            ResultSet set = statement.executeQuery();
            boolean b = set.next();
            statement.close();
            set.close();
            return b;
        } catch (SQLException e) {
            System.out.println("INFO: Failed to check if exists : " + e.getLocalizedMessage());
            return false;
        }
    }
}