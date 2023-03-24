package com.general_hello.bot.objects;

import com.general_hello.Config;
import com.general_hello.bot.objects.enums.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The Matchmaking class represents a matchmaking queue for grouping users into matches.
 * It also includes a nested Match class to manage user groups once they are matched.
 */
public class Matchmaking {
    /**
     * The logger for the Matchmaking class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Matchmaking.class);
    /**
     * The queue is a list of user IDs that are waiting to be matched.
     */
    private static final ArrayList<Long> queue = new ArrayList<>();
    /**
     * The number of users that should be matched per match.
     */
    private static final int MEMBERS_PER_MATCH = Integer.parseInt(Config.get("members_per_match"));

    /**
     * Adds a user ID to the matchmaking queue.
     * @param id the user ID to add
     */
    public static void addToQueue(long id) {
        queue.add(id);
        LOGGER.info("User {} was added to the matchmaking queue!", id);
        checkQueue();
    }

    /**
     * Removes a user ID from the matchmaking queue.
     * @param id the user ID to remove
     */
    public static void removeFromQueue(long id) {
        queue.remove(id);
        LOGGER.info("User {} was removed from the matchmaking queue!", id);
    }

    /**
     * Checks if a user ID is in the matchmaking queue.
     * @param id the user ID to check
     * @return true if the user ID is in the queue, false otherwise
     */
    public static boolean isInQueue(long id) {
        return queue.contains(id);
    }

    /**
     * Returns the number of users in the matchmaking queue.
     * @return the number of users in the queue
     */
    public static int getQueueSize() {
        return queue.size();
    }

    /**
     * Checks the matchmaking queue and attempts to assign users to a match if enough are present.
     * If there are not enough users in the queue, the matchmaking attempt fails.
     * If there are enough users in the queue, the users are assigned to a match and removed from the queue.
     * The match is then created and the users are assigned to it.
     * */
    public static void checkQueue() {
        List<Long> usersToBeAssigned = new ArrayList<>();
        if (getQueueSize() >= MEMBERS_PER_MATCH) {
            for (int i = 0; i < MEMBERS_PER_MATCH; i++) {
                usersToBeAssigned.add(queue.get(i));
                queue.remove(i);
            }
        } else {
            return;
        }

        // assign users to a match
        if (usersToBeAssigned.size() == MEMBERS_PER_MATCH) {
            Match match = new Match(usersToBeAssigned);
            LOGGER.info("Matchmaking successful, users {} were assigned to a match!", match.getUsers());
        } else {
            LOGGER.error("Matchmaking failed, not enough users in queue. Mismatch occurred!");
        }
    }

    /**
     * The Match class represents a group of users that have been matched together.
     */
    public static class Match {
        // HashMap of users to matches
        public static final HashMap<Long, Match> usersToMatch = new HashMap<>();
        private final List<Long> users;
        private long textChannelId = 0L;
        private long voiceChannelAId = 0L;
        private long voiceChannelBId = 0L;
        private final ArrayList<Long> teamA = new ArrayList<>();
        private final ArrayList<Long> teamB = new ArrayList<>();
        private Map map;

        /**
         * Constructs a new Match with the given list of user IDs.
         * @param users the list of user IDs to include in the match
         */
        public Match(List<Long> users) {
            this.users = users;
            for (Long user : users) {
                usersToMatch.put(user, this);
            }

            // randomly assign users to teams
            for (int i = 0; i < users.size(); i++) {
                if (i % 2 == 0) {
                    teamA.add(users.get(i));
                } else {
                    teamB.add(users.get(i));
                }
            }
        }

        /**
         * Returns the list of user IDs in this match.
         * @return the list of user IDs
         */
        public List<Long> getUsers() {
            return users;
        }

        /**
         * Returns the list of user IDs in team A.
         * @return the list of user IDs in team A
         */
        public ArrayList<Long> getTeamA() {
            return teamA;
        }

        /**
         * Returns the list of user IDs in team B.
         * @return the list of user IDs in team B
         */
        public ArrayList<Long> getTeamB() {
            return teamB;
        }

        /**
         * Returns the text channel ID associated with this match.
         * @return the text channel ID
         */
        public long getTextChannelId() {
            return textChannelId;
        }

        /**
         * Sets the text channel ID associated with this match.
         * @param textChannelId the text channel ID to set
         * @return this Match object
         */
        public Match setTextChannelId(long textChannelId) {
            this.textChannelId = textChannelId;
            updateHashmap();
            return this;
        }

        /**
         * Returns the voice channel ID associated with this match.
         * @return the voice channel ID
         */
        public long getVoiceChannelAId() {
            return voiceChannelAId;
        }

        /**
         * Sets the voice channel ID associated with this match.
         * @param voiceChannelAId the voice channel ID to set
         * @return this Match object
         */
        public Match setVoiceChannelAId(long voiceChannelAId) {
            this.voiceChannelAId = voiceChannelAId;
            updateHashmap();
            return this;
        }

        /**
         * Returns the voice channel ID associated with this match.
         * @return the voice channel ID
         */
        public long getVoiceChannelBId() {
            return voiceChannelBId;
        }

        /**
         * Sets the voice channel ID associated with this match.
         * @param voiceChannelBId the voice channel ID to set
         * @return this Match object
         */
        public Match setVoiceChannelBId(long voiceChannelBId) {
            this.voiceChannelBId = voiceChannelBId;
            updateHashmap();
            return this;
        }

        /**
         * Updates the usersToMatch HashMap with the current users in this match.
         */
        private void updateHashmap() {
            for (Long user : users) {
                usersToMatch.put(user, this);
            }
        }

        /**
         * Returns the map associated with this match.
         * @return the map
         */
        public Map getMap() {
            return map;
        }

        /**
         * Sets the map associated with this match.
         * @param map the map to set
         * @return this Match object
         */
        public Match setMap(Map map) {
            this.map = map;
            return this;
        }
    }
}