package com.general_hello.bot.events;

import com.general_hello.Bot;
import com.general_hello.bot.database.SQLiteDataSource;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * The OnReadyEvent class provides a Java representation of the bot's startup. It allows for creating, updating, and
 * retrieving information about the bot's startup, including the creation of the database file and the creation of the
 * User table.
 */
public class OnReadyEvent extends ListenerAdapter {
    /**
     * A logger object used for logging information about the bot's startup.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(OnReadyEvent.class);

    /**
     * Constructs a new OnReadyEvent object for the given user ID.
     * It initiates the database file.
     * @param event the ID of the user being created.
     */
    @Override
    public void onReady(@NotNull ReadyEvent event) {
        try {
            Bot.setJda(event.getJDA());
            LOGGER.info("Starting the bot...");
            try {
                final File dbFile = new File("database.db");
                // Create the database file if it doesn't exist
                if (!dbFile.exists()) {
                    if (dbFile.createNewFile()) {
                        LOGGER.info("Created database file");
                    } else {
                        LOGGER.info("Could not create database file");
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                SQLiteDataSource.connection = DriverManager.getConnection("jdbc:sqlite:database.db");
            } catch (Exception e) {
                e.printStackTrace();
            }

            LOGGER.info("Opened database successfully");

            // Make a new UserData table if it doesn't exist
            try (final PreparedStatement preparedStatement = SQLiteDataSource.getConnection()
                    .prepareStatement("CREATE TABLE IF NOT EXISTS User ( UserId INTEGER NOT NULL, " +
                            "ELO INTEGER DEFAULT -1, " +
                            "Wins INTEGER DEFAULT 0," +
                            "Losses INTEGER DEFAULT 0);"
                    )) {
                LOGGER.info("Made a new table (User)");
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}