package com.general_hello.bot.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;

public class SendJoinLeaveMessageCommand extends SlashCommand {
    public SendJoinLeaveMessageCommand() {
        this.name = "sendjoinleavemessage";
        this.help = "Sends a join/leave queue message to the channel.";
        this.guildOnly = true;
        this.ownerCommand = true;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        event.reply("This command is not yet implemented.").setEphemeral(true).queue();
    }
}
