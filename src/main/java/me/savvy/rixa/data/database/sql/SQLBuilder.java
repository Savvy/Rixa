package me.savvy.rixa.data.database.sql;

import me.savvy.rixa.Rixa;

import java.sql.*;

public class SQLBuilder {

    private String userName, password, port, databaseName, hostName;
    private Connection connection;

    public SQLBuilder(String userName, String password, String port, String databaseName, String hostName) {
        this.userName = userName;
        this.password = password;
        this.port = port;
        this.databaseName = databaseName;
        this.hostName = hostName;
    }

    public PreparedStatement getPreparedStatement(String query) throws SQLException {
        return getConnection().prepareStatement(query);
    }

    public SQLBuilder executeUpdate(PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.executeUpdate();
        return this;
    }

    public SQLBuilder executeUpdate(String query) throws SQLException {
        PreparedStatement stmt = getConnection().prepareStatement(query);
        stmt.executeUpdate();
        stmt.close();
        return this;
    }

    public ResultSet executeQuery(PreparedStatement preparedStatement) throws SQLException {
        return preparedStatement.executeQuery();
    }


    public Object getObject(PreparedStatement preparedStatement, String objectToGet) throws SQLException {
        Object result = null;
        ResultSet results = preparedStatement.executeQuery();
        if (results.next()) {
            result = results.getObject(objectToGet);
        }
        results.close();
        return result;
    }

    public String getString(PreparedStatement preparedStatement, String stringToGet) throws SQLException {
        return String.valueOf(getObject(preparedStatement, stringToGet));
    }

    public Integer getInteger(PreparedStatement preparedStatement, String intToGet) throws SQLException {
        return (int) getObject(preparedStatement, intToGet);
    }

    public boolean getBoolean(PreparedStatement preparedStatement, String booleanToGet) throws SQLException {
        return (boolean) getObject(preparedStatement, booleanToGet);
    }


    public Connection getConnection() {
        if (connection == null) {
            connect();
        }
        return connection;
    }

    private SQLBuilder connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            Rixa.getInstance().getLogger().severe("Could not find JDBC Driver");
            e.printStackTrace();
            return this;
        }
        try {
            connection = DriverManager.getConnection
                    (String.format("jdbc:mysql://%s:%s/%s", this.hostName, this.port, this.databaseName),
                            this.userName, this.password);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return this;
    }

    public void closeConnection() {
        try {
            if ((getConnection() != null) && (!getConnection().isClosed())) {
                getConnection().close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}