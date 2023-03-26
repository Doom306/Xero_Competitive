package com.general_hello.bot.commands;

import com.general_hello.bot.objects.Matchmaking;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;

/**
 * A command that sends a join/leave queue message to a guild channel. Only usable by the guild owner.
 */
public class SendJoinLeaveMessageCommand extends SlashCommand {

    /**
     * Constructs a new SendJoinLeaveMessageCommand object.
     * Sets the name, help message, and access restrictions of the command.
     */
    public SendJoinLeaveMessageCommand() {
        this.name = "sendjoinleavemessage";
        this.help = "Sends a join/leave queue message to the channel.";
        this.guildOnly = true;
        this.ownerCommand = true;
    }

    /**
     * Executes the command to send the join/leave queue message to the channel.
     * Defers the reply, constructs the embed with queue information, adds the join/leave buttons, and sends the message.
     * @param event The SlashCommandEvent object containing information about the command event.
     */
    @Override
    protected void execute(SlashCommandEvent event) {
        event.deferReply().setEphemeral(true).queue();

        event.getChannel().sendMessageEmbeds(Matchmaking.getQueueEmbed(event.getGuild()).build()).setComponents(
                Matchmaking.getQueueActionRow()
        ).queue();
        event.getHook().editOriginal("Sent!").queue();
    }
}
