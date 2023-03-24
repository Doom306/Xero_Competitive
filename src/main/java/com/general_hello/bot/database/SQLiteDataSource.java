package com.general_hello.bot.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * The SQLiteDataSource class provides a connection to the SQLite database.
 */
public class SQLiteDataSource {
    /**
     * A logger object used for logging information about database connections.
     * */
    private static final Logger LOGGER = LoggerFactory.getLogger(SQLiteDataSource.class);

    /**
     * The connection to the database.
     * */
    public static Connection connection = null;

    /**
     * Constructs a new SQLiteDataSource object.
     */
    public static Connection getConnection() throws SQLException {
        // Gets the database connection and makes if it isn't there
        if (connection == null) {
            connection = DriverManager.getConnection("jdbc:sqlite:database.db");
        }

        if (connection.isClosed()) {
            connection = DriverManager.getConnection("jdbc:sqlite:database.db");
        }
        return connection;
    }
}
