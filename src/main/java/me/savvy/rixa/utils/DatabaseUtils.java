package me.savvy.rixa.utils;

import me.majrly.database.statements.Update;
import me.savvy.rixa.Rixa;
import me.savvy.rixa.enums.Result;

public class DatabaseUtils {

    public static Result update(String table, String setting, String key, Object placeholder, Object placeholder2) {
        Update update = new Update("UPDATE `" + table + "` SET `" + setting + "` = ? WHERE `" + key + "` = ?;");
        update.setObject(placeholder);
        update.setObject(placeholder2);
        Rixa.getDatabase().send(update);
        return Result.TRUE;
    }
}