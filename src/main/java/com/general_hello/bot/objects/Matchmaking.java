package com.general_hello.bot.objects;

import com.general_hello.Bot;
import com.general_hello.Config;
import com.general_hello.bot.objects.enums.Map;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;

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
    public static final int MEMBERS_PER_MATCH = Integer.parseInt(Config.get("members_per_match"));

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
     * Clears the matchmaking queue.
     */
    public static void clearQueue() {
        queue.clear();
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
                usersToBeAssigned.add(queue.get(0));
                queue.remove(0);
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
     * Returns an EmbedBuilder object with information about the matchmaking queue.
     * @param guild the guild to get information for
     * @return an EmbedBuilder object with information about the matchmaking queue
     */
    public static EmbedBuilder getQueueEmbed(Guild guild) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Matchmaking Queue");
        embedBuilder.setDescription("**The following users are currently in the matchmaking queue:**\n");
        for (Long id : queue) {
            embedBuilder.appendDescription("<@" + id.toString() + "> - *" + id + "*\n");
        }
        embedBuilder.addField("Queue", Matchmaking.getQueueSize() + "/" + Matchmaking.MEMBERS_PER_MATCH, true);
        embedBuilder.setFooter(guild.getName(), guild.getIconUrl());
        embedBuilder.setColor(guild.getSelfMember().getColor());
        return embedBuilder;
    }

    /**
     * Returns an ActionRow object with buttons to join or leave the matchmaking queue.
     * @return an ActionRow object with buttons to join or leave the matchmaking queue
     */
    public static ActionRow getQueueActionRow() {
        return ActionRow.of(
                Button.success("0000:join", "Join Queue"),
                Button.danger("0000:leave", "Leave Queue"));
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
        private final List<Map> bannedMaps = new ArrayList<>();
        private boolean banTurn = true;
        private long teamACaptain = 0L;
        private long teamBCaptain = 0L;

        /**
         * Constructs a new Match with the given list of user IDs.
         * @param users the list of user IDs to include in the match
         */
        public Match(List<Long> users) {
            this.users = users;
            for (Long user : users) {
                usersToMatch.put(user, this);
            }

            // sort users by elo
            for (int i = 0; i < users.size(); i++) {
                for (int j = 0; j < users.size() - 1; j++) {
                    if (ELOUser.getElo(users.get(j)) < ELOUser.getElo(users.get(j + 1))) {
                        long temp = users.get(j);
                        users.set(j, users.get(j + 1));
                        users.set(j + 1, temp);
                    }
                }
            }

            // randomly assign users to teams
            for (int i = 0; i < users.size(); i++) {
                if (i % 2 == 0) {
                    teamA.add(users.get(i));
                } else {
                    teamB.add(users.get(i));
                }
            }

            // get the highest elo per team and assign as captain
            long highestEloA = -1L;
            long highestEloB = -1L;
            for (Long user : teamA) {
                if (ELOUser.getElo(user) > highestEloA) {
                    highestEloA = ELOUser.getElo(user);
                    teamACaptain = user;
                }
            }
            for (Long user : teamB) {
                if (ELOUser.getElo(user) > highestEloB) {
                    highestEloB = ELOUser.getElo(user);
                    teamBCaptain = user;
                }
            }

            // create the match
            createMatch();
            updateHashmap();
        }

        /**
         * Creates the match.
         */
        private void createMatch() {
            // create the text channel
            Guild guild = Bot.getJda().getGuildById(Config.get("guild"));
            ChannelAction<Category> channelAction = guild.createCategory("Match " + users.get(0));
            channelAction.addRolePermissionOverride(guild.getPublicRole().getIdLong(), null, EnumSet.of(Permission.VIEW_CHANNEL));
            Category category = channelAction.complete();
            ChannelAction<TextChannel> textChannelChannelAction = category.createTextChannel("match");
            for (Long user : getUsers()) {
                textChannelChannelAction.addMemberPermissionOverride(user, EnumSet.of(Permission.VIEW_CHANNEL), null);
            }
            TextChannel textChannel = textChannelChannelAction.complete();
            setTextChannelId(textChannel.getIdLong());
            // create the voice channels
            ChannelAction<VoiceChannel> voiceChannelAAction = category.createVoiceChannel("Team A");
            for (Long user : getTeamA()) {
                voiceChannelAAction.addMemberPermissionOverride(user, EnumSet.of(Permission.VIEW_CHANNEL), null);
            }
            setVoiceChannelAId(voiceChannelAAction.complete().getIdLong());
            ChannelAction<VoiceChannel> textChannelBAction = category.createVoiceChannel("Team B");
            for (Long user : getTeamB()) {
                textChannelBAction.addMemberPermissionOverride(user, EnumSet.of(Permission.VIEW_CHANNEL), null);
            }
            setVoiceChannelBId(textChannelBAction.complete().getIdLong());

            // embed
            String mention;
            if (banTurn) {
                mention = "<@" + teamACaptain + ">";
            } else {
                mention = "<@" + teamBCaptain + ">";
            }

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Match " + getUsers().get(0));
            embedBuilder.setDescription("A match has been created for you! " +
                    mention + ", kindly vote for a map to ban using the selection below.\n\n" +
                    "**Possible Maps:**\n" + Map.getMapString(bannedMaps));
            embedBuilder.addField("Team A", getTeamString(getTeamA()), false);
            embedBuilder.addField("Team B", getTeamString(getTeamB()), false);
            embedBuilder.addField("Map", "Map will be chosen once voting is complete.", false);
            embedBuilder.setFooter(guild.getName(), guild.getIconUrl());
            embedBuilder.setColor(guild.getSelfMember().getColor());
            // mention all the users
            StringBuilder mentions = new StringBuilder();
            for (Long user : getUsers()) {
                mentions.append("<@").append(user).append("> ");
            }
            textChannel.sendMessage(mentions).queue();
            textChannel.sendMessageEmbeds(embedBuilder.build()).setComponents(getMapActionRow()).queue();
            updateHashmap();
        }

        /**
         * Ends the match.
         */
        public void endMatch(String winner, Guild guild, GenericSelectMenuInteractionEvent event) {
            event.reply("Match has ended! ELO points are being added. Winner is **" + winner + "**.").queue();
            event.getMessage().delete().queue();

            if (Objects.equals(winner, "Team A")) {
                for (Long user : getTeamA()) {
                    ELOUser.addElo(user, true);
                    ELOUser.addWins(1, user);
                }
                for (Long user : getTeamB()) {
                    ELOUser.addElo(user, false);
                    ELOUser.addLosses(1, user);
                }
            } else if (Objects.equals(winner, "Team B")) {
                for (Long user : getTeamA()) {
                    ELOUser.addElo(user, false);
                    ELOUser.addLosses(1, user);
                }
                for (Long user : getTeamB()) {
                    ELOUser.addElo(user, true);
                    ELOUser.addWins(1, user);
                }
            }
            // delete the text channel
            Category parentCategory = getTextChannel().getParentCategory();
            getTextChannel().delete().queueAfter(20, TimeUnit.SECONDS);
            // delete the voice channels
            guild.getVoiceChannelById(getVoiceChannelAId()).delete().queueAfter(20, TimeUnit.SECONDS);
            guild.getVoiceChannelById(getVoiceChannelBId()).delete().queueAfter(20, TimeUnit.SECONDS);
            // delete the category
            parentCategory.delete().queueAfter(20, TimeUnit.SECONDS);
            // remove users from hashmap
            for (Long user : getUsers()) {
                usersToMatch.remove(user);
            }
        }

        /**
         * Returns an ActionRow object with buttons to vote for a map to ban.
         * @return an ActionRow object with buttons to vote for a map to ban
         */
        public ActionRow getMapActionRow() {
            // select menu
            long id;
            if (banTurn) {
                id = teamACaptain;
            } else {
                id = teamBCaptain;
            }
            banTurn = !banTurn;

            StringSelectMenu.Builder builder = StringSelectMenu.create("map:" + id);
            builder.setPlaceholder("Select a map to ban");
            // list of maps without the banned maps
            List<Map> maps = new ArrayList<>(Arrays.asList(Map.values()));
            maps.removeAll(getBannedMaps());

            for (Map map : maps) {
                builder.addOption(map.getName(), map.getName());
            }
            builder.setRequiredRange(1, 1);
            updateHashmap();
            return ActionRow.of(builder.build());
        }

        /**
         * Returns a string representation of the given list of user IDs.
         * @param users the list of user IDs
         * @return a string representation of the given list of user IDs
         */
        private String getTeamString(List<Long> users) {
            StringBuilder stringBuilder = new StringBuilder();
            for (Long user : users) {
                stringBuilder.append("<@").append(user).append("> - ").append(ELOUser.getElo(user)).append(" ELO");
                if (user.equals(teamACaptain) || user.equals(teamBCaptain)) {
                    stringBuilder.append(" **(Team Captain)**");
                } else {
                    stringBuilder.append("\n");
                }
            }

            updateHashmap();
            return stringBuilder.toString();
        }

        /**
         * Returns the map that has been banned.
         * @return the map that has been banned
         */
        public List<Map> getBannedMaps() {
            return bannedMaps;
        }

        /**
         * Bans the given map.
         * @param map the map to ban
         */
        public void banMap(Map map, Guild guild, GenericSelectMenuInteractionEvent event) {
            getBannedMaps().add(map);
            if (getBannedMaps().size() == Map.values().length - 1) {
                // set the map that isnt banned
                for (Map m : Map.values()) {
                    if (!getBannedMaps().contains(m)) {
                        setMap(m);
                        break;
                    }
                }
            }

            if (getMap() != null) {
                // start the match
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setTitle("Match " + getUsers().get(0));
                embedBuilder.setDescription("A match has been created for you!");
                embedBuilder.addField("Team A", getTeamString(getTeamA()), false);
                embedBuilder.addField("Team B", getTeamString(getTeamB()), false);
                embedBuilder.addField("Map", map.getName(), false);
                embedBuilder.setFooter(guild.getName(), guild.getIconUrl());
                embedBuilder.setColor(guild.getSelfMember().getColor());
                event.getHook().editOriginalEmbeds(embedBuilder.build()).setComponents(getWinnerActionRow()).queue();
            } else {
                String mention;
                if (banTurn) {
                    mention = "<@" + teamACaptain + ">";
                } else {
                    mention = "<@" + teamBCaptain + ">";
                }

                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setTitle("Match " + getUsers().get(0));
                embedBuilder.setDescription("A match has been created for you! " +
                        mention + ", kindly vote for a map to ban using the selection below.\n\n" +
                        "**Possible Maps:**\n" + Map.getMapString(bannedMaps));
                embedBuilder.addField("Team A", getTeamString(getTeamA()), false);
                embedBuilder.addField("Team B", getTeamString(getTeamB()), false);
                embedBuilder.addField("Map", "Map will be chosen once voting is complete.", false);
                embedBuilder.setFooter(guild.getName(), guild.getIconUrl());
                embedBuilder.setColor(guild.getSelfMember().getColor());
                event.getHook().editOriginalEmbeds(embedBuilder.build()).setComponents(getMapActionRow()).queue();
            }
            updateHashmap();
        }

        /**
         * Select menu of leaders
         * @return the ActionRow object
         */
        private ActionRow getWinnerActionRow() {
            StringSelectMenu.Builder builder = StringSelectMenu.create("winner:0000");
            builder.setPlaceholder("Select the team that won");
            builder.addOption("Team A", "Team A");
            builder.addOption("Team B", "Team B");
            builder.setRequiredRange(1, 1);
            updateHashmap();
            return ActionRow.of(builder.build());
        }

        /**
         * Returns the TextChannel object of this match.
         * @return the TextChannel object of this match
         */
        private TextChannel getTextChannel() {
            return Bot.getJda().getTextChannelById(getTextChannelId());
        }

        /**
         * Returns if the map has been banned.
         * @return if the map has been banned
         */
        private boolean isMapBanned() {
            return bannedMaps.size() == Map.values().length - 1;
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
         *
         * @param textChannelId the text channel ID to set
         */
        public void setTextChannelId(long textChannelId) {
            this.textChannelId = textChannelId;
            updateHashmap();
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
         *
         * @param voiceChannelAId the voice channel ID to set
         */
        public void setVoiceChannelAId(long voiceChannelAId) {
            this.voiceChannelAId = voiceChannelAId;
            updateHashmap();
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
         *
         * @param voiceChannelBId the voice channel ID to set
         */
        public void setVoiceChannelBId(long voiceChannelBId) {
            this.voiceChannelBId = voiceChannelBId;
            updateHashmap();
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
         *
         * @param map the map to set
         */
        public void setMap(Map map) {
            this.map = map;
            updateHashmap();
        }
    }
}