package com.general_hello;

import com.general_hello.bot.commands.SendJoinLeaveMessageCommand;
import com.general_hello.bot.events.OnButtonClick;
import com.general_hello.bot.events.OnReadyEvent;
import com.general_hello.bot.objects.GlobalVariables;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

/**
 * The main class of the bot, responsible for initializing and running the bot.
 */
public class Bot {

    /** The instance of the JDA object. */
    private static JDA jda;

    /** The instance of the Bot object. */
    private static Bot bot;

    /** The instance of the EventWaiter object. */
    private final EventWaiter eventWaiter;

    /** The logger for the bot. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Bot.class);

    /**
     * Returns the instance of the Bot object.
     * @return the instance of the Bot object.
     */
    public static Bot getBot() {
        return bot;
    }

    /**
     * Returns the instance of the EventWaiter object.
     * @return the instance of the EventWaiter object.
     */
    public EventWaiter getEventWaiter() {
        return eventWaiter;
    }
    /**
     * Returns the instance of the JDA object.
     * @return the instance of the JDA object.
     */
    public static JDA getJda() {
        return jda;
    }
    /**
     * Sets the instance of the JDA object.
     * @param jda the instance of the JDA object to be set.
     */
    public static void setJda(JDA jda) {
        LOGGER.info("JDA object initialized");
        Bot.jda = jda;
    }

    /**
     * Constructs a new Bot object.
     * @throws LoginException if there was a problem logging in to the bot.
     * @throws InterruptedException if the bot was interrupted while logging in.
     */
    public Bot() throws LoginException, InterruptedException {
        bot = this;
        CommandClientBuilder client = new CommandClientBuilder();
        client.setOwnerId(Config.get("owner_id"));
        client.setCoOwnerIds(Config.get("owner_id_partner"));
        client.setPrefix(Config.get("prefix"));
        client.setStatus(OnlineStatus.IDLE);
        client.setActivity(Activity.listening("A B C D E F G..."));
        addCommands(client);
        eventWaiter = new EventWaiter();
        CommandClient commandClient = client.build();

        jda = JDABuilder.createDefault(Config.get("token"),
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_MESSAGE_REACTIONS,
                        GatewayIntent.DIRECT_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.GUILD_PRESENCES,
                        GatewayIntent.GUILD_EMOJIS_AND_STICKERS,
                        GatewayIntent.SCHEDULED_EVENTS,
                        GatewayIntent.GUILD_VOICE_STATES)
                .addEventListeners(eventWaiter, commandClient, new OnReadyEvent(),
                        new OnButtonClick())
                .setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableCache(CacheFlag.ONLINE_STATUS, CacheFlag.SCHEDULED_EVENTS)
                .build().awaitReady();

        LOGGER.info("Builder successfully built!");
    }

    /**
     * The entry point for the bot.
     * @param args the arguments to be passed to the bot.
     * @throws LoginException if there was a problem logging in to the bot.
     */
    public static void main(String[] args) throws LoginException {
        LOGGER.info("Starting program v." + GlobalVariables.VERSION);
        try {
            new Bot();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    /**
     * Adds the commands to the command client.
     * @param clientBuilder the CommandClientBuilder object to add the commands to.
     */
    private static void addCommands(CommandClientBuilder clientBuilder) {
        clientBuilder.addSlashCommands(new SendJoinLeaveMessageCommand());
        LOGGER.info("Added the slash commands!");
    }
}