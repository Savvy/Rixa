package me.savvy.rixa.data.database.sql;

import me.savvy.rixa.Rixa;
import me.savvy.rixa.data.database.sql.mysql.mysql.MySQL;
import me.savvy.rixa.enums.Result;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseManager {

    private Connection connection;
    private MySQL MYSQL = null;

    public DatabaseManager(String hostName, String port, String databaseName, String userName, String password) {
        MYSQL = new MySQL(hostName, port, databaseName, userName, password);
    }

    public Connection getConnection() {
        return connection;
    }

    public void createTable() {
        checkConnection();
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS `core` (`guild_id` varchar(255) NOT NULL, `guild_name` varchar(255) NOT NULL, PRIMARY KEY (`guild_id`));");
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            Rixa.getInstance().getLogger().severe("Could not check if table exists, stopping server.");
            e.printStackTrace();
            // Redirect to 500
        }
    }

    private void checkConnection() {
        try {
            if (!MYSQL.checkConnection()) {
                connection = MYSQL.openConnection();
                Rixa.getInstance().getLogger().info("Mysql database connected");
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public Result executeUpdate(PreparedStatement ps) throws SQLException {
        checkConnection();
        try {
            ps.executeUpdate();
            return Result.SUCCESS;
        } catch (SQLException e) {
            e.printStackTrace();
            return Result.ERROR;
        }
    }

    public Object getObject(String string, String objToGet) throws SQLException {
        checkConnection();
        PreparedStatement ps = connection.prepareStatement(string);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getString(objToGet);
        } else {
            return null;
        }
    }

    public ResultSet getObject(PreparedStatement ps) throws SQLException {
        checkConnection();
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs;
        }
        return null;
    }

    public ResultSet executeQuery(String query) {
        checkConnection();
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            return ps.executeQuery();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public int getCount(String table) {
        checkConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT count(*) FROM '" + table + "';");
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            preparedStatement.close();
            resultSet.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public Result checkExists(String string) throws SQLException {
        checkConnection();
        try {
            PreparedStatement ps = connection.prepareStatement(string);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                rs.close();
                return Result.TRUE;
            } else {
                rs.close();
                return Result.FALSE;
            }
        } catch (SQLException e) {
            return Result.ERROR;
        }
    }


    public Result insert(String string) {
        checkConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(string);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            return Result.SUCCESS;
        } catch (SQLException e) {
            e.printStackTrace();
            return Result.ERROR;
        }
    }
}
