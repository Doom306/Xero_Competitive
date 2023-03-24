package com.general_hello.bot.objects;

import com.general_hello.bot.database.SQLiteDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The ELOUser class provides a Java representation of an ELO user in a game. It allows for creating, updating, and
 * retrieving information about ELO users, including their ELO rating, wins, and losses.
 */
public class ELOUser {
    /** A logger object used for logging information about ELO user creation. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ELOUser.class);

    /**
     * Constructs a new ELOUser object for the given user ID.
     * @param id the ID of the user being created.
     */
    public ELOUser(long id) {
        LOGGER.info("Made a new ELO user for " + id + ".");
        try (final PreparedStatement preparedStatement = SQLiteDataSource.getConnection()
                .prepareStatement("INSERT INTO User VALUES (?);")) {
            preparedStatement.setString(1, String.valueOf(id));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the ELO rating of the user with the given user ID.
     * @param userId the ID of the user being queried.
     * @return the ELO rating of the user with the given ID.
     */
    public static int getElo(long userId) {
        doCheck(userId);
        try (Connection connection = SQLiteDataSource.getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT ELO FROM User WHERE UserId = ?")) {
            preparedStatement.setLong(1, userId);

            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("ELO");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Sets the ELO rating of the user with the given user ID.
     *
     * @param elo the new ELO rating for the user.
     * @param userId the ID of the user being updated.
     */
    public static void setElo(int elo, long userId) {
        doCheck(userId);
        try (final PreparedStatement preparedStatement = SQLiteDataSource.getConnection()
                .prepareStatement("UPDATE User SET ELO=? WHERE UserId=?"
                )) {

            preparedStatement.setInt(1, elo);
            preparedStatement.setLong(2, userId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the number of wins for the user with the given user ID.
     * @param userId the ID of the user being queried.
     * @return the number of wins for the user with the given ID.
     */
    public static int getWins(long userId) {
        doCheck(userId);
        try (Connection connection = SQLiteDataSource.getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT Wins FROM User WHERE UserId = ?")) {
            preparedStatement.setLong(1, userId);

            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("Wins");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Sets the number of wins for the user with the given user ID.
     * @param wins the new number of wins for the user.
     * @param userId the ID of the user being updated.
     */
    public static void setWins(int wins, long userId) {
        doCheck(userId);
        try (final PreparedStatement preparedStatement = SQLiteDataSource.getConnection()
                .prepareStatement("UPDATE User SET Wins=? WHERE UserId=?"
                )) {

            preparedStatement.setInt(1, wins);
            preparedStatement.setLong(2, userId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the number of losses for a specified user.
     * @param userId The ID of the user whose number of losses is being retrieved.
     * @return The number of losses for the specified user.
     */
    public static int getLosses(long userId) {
        doCheck(userId);
        try (Connection connection = SQLiteDataSource.getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT Losses FROM User WHERE UserId = ?")) {
            preparedStatement.setLong(1, userId);

            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("Losses");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Sets the number of losses for a specified user.
     * @param losses The new number of losses for the specified user.
     * @param userId The ID of the user whose number of losses is being updated.
     */
    public static void setLosses(int losses, long userId) {
        doCheck(userId);
        try (final PreparedStatement preparedStatement = SQLiteDataSource.getConnection()
                .prepareStatement("UPDATE User SET Losses=? WHERE UserId=?"
                )) {

            preparedStatement.setInt(1, losses);
            preparedStatement.setLong(2, userId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns whether the account exists in the database or not.
     * @param userId The ID of the user whose account is being checked.
     * @return Whether the account exists in the database or not.
     */
    private static boolean doesAccountExist(long userId) {
        try (Connection connection = SQLiteDataSource.getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT ELO FROM User WHERE UserId = ?")) {
            preparedStatement.setLong(1, userId);

            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("ELO") != -1;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Checks if the account exists, and if it doesn't, creates it.
     * @param userId The ID of the user whose account is being checked.
     */
    private static void doCheck(long userId) {
        if (!doesAccountExist(userId)) {
            new ELOUser(userId);
        }
    }
}
