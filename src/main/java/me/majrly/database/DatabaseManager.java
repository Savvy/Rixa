package me.majrly.database;

import java.util.HashMap;

/**
 * Apart of the database api to manage all your databases
 *
 * @author Majrly
 * @since 1.0.0
 */
public class DatabaseManager {

    private static HashMap<String, Database> databases = new HashMap<>();

    /**
     * Add a database to {@link #databases}
     *
     * @param database The database you want to add
     * @since 1.0.0
     */
    public static void addDatabase(Database database) {
        databases.put(database.getName(), database);
    }

    /**
     * Get a database with specified name from {@link #databases}
     *
     * @param name The name of the database you want to obtain
     * @return The database wrapper
     * @since 1.0.0
     */
    public static Database getDatabase(String name) {
        return databases.get(name);
    }

    // Getters
    public static HashMap<String, Database> getDatabases() {
        return databases;
    }
}