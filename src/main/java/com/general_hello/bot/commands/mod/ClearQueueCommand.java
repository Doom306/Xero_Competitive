package com.general_hello.bot.commands.mod;

import com.general_hello.bot.objects.Matchmaking;
import com.jagrosh.jdautilities.command.MessageContextMenu;
import com.jagrosh.jdautilities.command.MessageContextMenuEvent;

/**
 * A Discord bot command to clear the matchmaking queue for a server.
 * This command can only be executed by the owner of the bot and is available as a context menu on messages.
 */
public class ClearQueueCommand extends MessageContextMenu {
    /**
     * Constructs a new ClearQueueCommand object and sets the name, guildOnly and ownerCommand fields.
     */
    public ClearQueueCommand() {
        this.name = "Clear Queue";
        this.guildOnly = true;
        this.ownerCommand = true;
    }

    /**
     * Executes the ClearQueueCommand by clearing the matchmaking queue and sending a confirmation message.
     * @param event the context menu event that triggered the command
     */
    @Override
    protected void execute(MessageContextMenuEvent event) {
        Matchmaking.clearQueue();
        event.getTarget().editMessageEmbeds(Matchmaking.getQueueEmbed(event.getGuild()).build()).queue();
        event.reply("Cleared the queue").setEphemeral(true).queue();
    }
}
