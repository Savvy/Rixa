package me.savvy.rixa.data.database;

import me.savvy.rixa.Rixa;
import me.savvy.rixa.data.database.sql.DatabaseManager;
import me.savvy.rixa.enums.Result;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Timber on 5/31/2017.
 *
 * This class will be used to grab and put data into databases (SQL, FlatFile)
 */
public class Data {

    private DataType dataType;
    private DatabaseManager db;
    public Data(DataType dataType) {
        this.dataType = dataType;
        this.db = Rixa.getInstance().getDbManager();
    }

    public Object get(String key, String value, String objToGet, String table) throws SQLException {
        switch (dataType) {
            case SQL:
                // SELECT objToGet FROM table WHERE key = value.
                PreparedStatement ps =
                        db.getConnection().prepareStatement("SELECT `" + objToGet + "` FROM `" + table + "` WHERE `" + key + "` = ?");
                ps.setString(1, value);
                return db.getObject(ps).getObject(objToGet);
            default:
                return null;
        }
    }

    public void put(String key, String value) {
        switch (dataType) {
        }
    }

    public Result update(String table, String setting, String key, Object placeholder, Object placeholder2) {
        switch (dataType) {
            case SQL:
                try {
                    PreparedStatement ps = db.getConnection().prepareStatement("UPDATE `" + table +"` SET `" + setting + "` = ? WHERE `" + key + "` = ?;");
                    ps.setObject(1, placeholder);
                    ps.setObject(2, placeholder2);
                    return db.executeUpdate(ps);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    return Result.ERROR;
                }
        }
        return Result.FALSE;
    }

    public void delete(String key, String value) {
        switch (dataType) {

        }
    }

    public Result exists(String check) {
        switch(dataType) {
            case SQL:
                return db.checkExists(check);
        }
        return Result.FALSE;
    }
}
