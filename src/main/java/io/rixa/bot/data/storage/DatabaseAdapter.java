package io.rixa.bot.data.storage;

import io.rixa.bot.Rixa;
import io.rixa.bot.data.config.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class DatabaseAdapter {

    private static DatabaseAdapter instance;
    private Rixa rixaInstance;
    private JdbcTemplate jdbcTemplate;
    private DatabaseAdapter() {
        instance = this;
        rixaInstance = Rixa.getInstance();
    }

    public void check() {
        if (jdbcTemplate != null) {
            return;
        }
        Configuration config = rixaInstance.getConfiguration();
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl(
                String.format("jdbc:mysql://%s:%s/%s", config.getSqlCredentials().get("hostName"),
                        config.getSqlCredentials().get("port"), config.getSqlCredentials().get("databaseName")));
        dataSource.setUsername(config.getSqlCredentials().get("userName"));
        dataSource.setPassword(config.getSqlCredentials().get("password"));
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public JdbcTemplate get() {
        check();
        return jdbcTemplate;
    }

    public boolean exists(String table, String key, String value) {
        int amount = get().queryForObject(String.format("SELECT COUNT(*) FROM `%s` WHERE `%s` = ?", table, key), new Object[] { value }, Integer.class);
        return amount > 0;
    }

    public static DatabaseAdapter getInstance() {
        return ( (instance == null) ? instance = new DatabaseAdapter() : instance );
    }
}
