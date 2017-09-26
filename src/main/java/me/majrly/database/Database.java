package me.majrly.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.majrly.database.params.Parameter;
import me.majrly.database.statements.Query;
import me.majrly.database.statements.Statement;

import java.sql.*;
import java.util.Map;
import java.util.Optional;

/**
 * Database API
 *
 * @author Majrly
 * @since 1.0.0
 */
public class Database {

    // Variables
    private String name;
    private String hostname;
    private String username;
    private String password;
    private String database;

    private int port = 3306;
    private int timeout = 60 * 1000;

    private HikariDataSource source;
    private HikariConfig config = new HikariConfig();

    /**
     * Database API
     *
     * @param name     The name of this database instance
     * @param hostname The ip to use when connecting
     * @param username The username to authenticate as
     * @param password The password to authenticate yourself
     * @param database The name of the database to switch to
     * @param port     The port to use when connecting
     * @since 1.0.0
     */
    public Database(String name, String hostname, String username, String password, String database, int port, HikariConfig config) {
        this.config = config;
        this.config.setJdbcUrl("jdbc:" + (this.name = name) + "://" + (this.hostname = hostname) + ":" + (this.port = port) + "/" + (this.database = database));
        this.config.setDriverClassName("com.mysql.jdbc.Driver");
        this.config.setUsername(this.username = username);
        this.config.setPassword(this.password = password);
        this.source = new HikariDataSource(config);
    }

    /**
     * Get the database options class
     *
     * @return A reference to {@link DatabaseOptions}
     * @since 1.0.0
     */
    public static DatabaseOptions options() {
        return new DatabaseOptions();
    }

    /**
     * Connects to the database
     *
     * @return Whether it connected or not
     * @since 1.0.0
     */
    public void init() {
    }

    /**
     * Sends a query to the database
     *
     * @param statement The statement to send the database
     * @return Either the int of an update, or the ResultSet of a query
     * @since 1.0.0
     */
    public Optional<?> send(Statement statement) {
        Optional<PreparedStatement> optional = prepare(statement);
        if (!optional.isPresent()) return Optional.empty();
        PreparedStatement preparedStatement = optional.get();
        try {
            if (statement instanceof Query) {
                return Optional.of(preparedStatement.executeQuery());
            } else {
                return Optional.of(preparedStatement.executeUpdate());
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * Close a prepared statement
     *
     * @param preparedStatement The prepared statement you wish to close
     * @since 1.0.0
     */
    public void closeStatement(PreparedStatement preparedStatement) {
        try {
            if (preparedStatement != null && !preparedStatement.isClosed()) {
                preparedStatement.close();
            }
        } catch (SQLException e) {
            // Can't handle closing statement
            e.printStackTrace();
        }
    }

    /**
     * Prepare a statement
     *
     * @param statement The statement with parameters you wish to prepare
     * @return The optional value of {@link PreparedStatement}
     * @since 1.0.0
     */
    public Optional<PreparedStatement> prepare(Statement statement) {
        try {
            PreparedStatement preparedStatement = source.getConnection().prepareStatement(statement.getSQL());
            for (Map.Entry<Integer, Parameter> parameter : statement.getParameters().entrySet()) {
                switch (parameter.getValue().getType()) {
                    case STRING:
                        preparedStatement.setString(parameter.getKey(), (String) parameter.getValue().getData());
                        break;
                    case INTEGER:
                        preparedStatement.setInt(parameter.getKey(), (Integer) parameter.getValue().getData());
                        break;
                    case DOUBLE:
                        preparedStatement.setDouble(parameter.getKey(), (Double) parameter.getValue().getData());
                        break;
                    case LONG:
                        preparedStatement.setLong(parameter.getKey(), (Long) parameter.getValue().getData());
                        break;
                    case BLOB:
                        preparedStatement.setBlob(parameter.getKey(), (Blob) parameter.getValue().getData());
                        break;
                    case FLOAT:
                        preparedStatement.setFloat(parameter.getKey(), (Float) parameter.getValue().getData());
                        break;
                    case BOOLEAN:
                        preparedStatement.setBoolean(parameter.getKey(), (Boolean) parameter.getValue().getData());
                        break;
                    case DATE:
                        preparedStatement.setDate(parameter.getKey(), (Date) parameter.getValue().getData());
                        break;
                    case OBJECT:
                        preparedStatement.setObject(parameter.getKey(), parameter.getValue().getData());
                        break;
                    default:
                        preparedStatement.setObject(parameter.getKey(), parameter.getValue().getData());
                        break;
                }
            }
            return Optional.of(preparedStatement);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * Prepare a statement
     *
     * @param sql The statement you want to prepare
     * @return The optional value of {@link PreparedStatement}
     * @since 1.0.0
     */
    public Optional<PreparedStatement> prepare(String sql) {
        try {
            return Optional.of(source.getConnection().prepareStatement(sql));
        } catch (SQLException exception) {
            exception.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * Get the connection of MySQL
     *
     * @return The optional value of {@link Connection}
     * @since 1.0.0
     */
    public Optional<Connection> getConnection() {
        try {
            return Optional.of(source.getConnection());
        } catch (SQLException e) {
            return Optional.empty();
        }
    }

    /**
     * Closes the database
     *
     * @since 1.0.0
     */
    public void close() {
        source.close();
    }

    public String getName() {
        return name;
    }

    public HikariConfig getConfig() {
        return config;
    }

    public HikariDataSource getSource() {
        return source;
    }

    /**
     * Database options used for {@link Database}
     *
     * @author Majrly
     * @since 1.0.0
     */
    public static class DatabaseOptions {

        // Variables
        private HikariConfig config = new HikariConfig();

        private String name;
        private String hostname = "127.0.0.1";
        private String username = "root";
        private String password;
        private String database;

        private int port = 3306;
        private int timeout = 60 * 1000;

        /**
         * Set a key/value in the HikariConfig
         *
         * @param key   The key you want to set a value to
         * @param value The value you want to set
         * @since 1.0.0
         */
        public DatabaseOptions set(String key, String value) {
            config.addDataSourceProperty(key, value);
            return this;
        }

        /**
         * Set the hostname / port to connect
         *
         * @param hostname The hostname of the database
         * @param port     The port of the database
         * @return This object
         * @since 1.0.0
         */
        public DatabaseOptions hostname(String hostname, int port) {
            this.database = database;
            this.port = port;
            return this;
        }

        /**
         * Set the authentication username and password
         *
         * @param username The user you want to authenticate as
         * @param password The password you want to authenticate with
         * @return This object
         * @since 1.0.0
         */
        public DatabaseOptions auth(String username, String password) {
            this.username = username;
            this.password = password;
            return this;
        }

        /**
         * Set the database to switch to
         *
         * @param database The database you want to switch to
         * @return This object
         * @since 1.0.0
         */
        public DatabaseOptions database(String database) {
            this.database = database;
            return this;
        }

        /**
         * Set the name of the database connection
         *
         * @param name The name of the database connection
         * @return This object
         * @since 1.0.0
         */
        public DatabaseOptions type(String name) {
            this.name = name;
            return this;
        }

        /**
         * Set the timeout of the connection
         *
         * @param timeout The max amount of time to connect
         * @return This object
         * @since 1.0.0
         */
        public DatabaseOptions timeout(int timeout) {
            this.timeout = timeout;
            return this;
        }

        /**
         * Build this class
         *
         * @return The database object
         * @since 1.0.0
         */
        public Database build() {
            if (username.isEmpty()) {
                username = "root";
            }
            return new Database(name, hostname, username, password, database, port, config);
        }
    }
}