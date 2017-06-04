package me.savvy.rixa.data.database;

import me.savvy.rixa.Rixa;
import me.savvy.rixa.data.database.sql.DatabaseManager;

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
                // SELECT guild FROM table WHERE key = value.
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

    public void update(String key, String value) {
        switch (dataType) {

        }
    }

    public void delete(String key, String value) {
        switch (dataType) {

        }
    }
}
