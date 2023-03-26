package com.general_hello.bot.commands.mod;

import com.general_hello.bot.objects.ELOUser;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

/**
 * A command that adds points to a user's ELO rating.
 */
public class AddPointsCommand extends SlashCommand {
    /**
     * Constructs a new AddPointsCommand.
     * Sets the name, help message, guildOnly and ownerCommand fields, and defines the options.
     */
    public AddPointsCommand() {
        this.name = "addpoints";
        this.help = "Adds points to a user";
        this.guildOnly = true;
        this.ownerCommand = true;
        // option users
        this.options = List.of(new OptionData[]{
                new OptionData(OptionType.USER, "user", "The user to add points to", true),
                new OptionData(OptionType.INTEGER, "points", "The amount of points to add", true)
        });
    }

    /**
     * Executes the AddPointsCommand.
     * Adds the specified number of points to the ELO rating of the user specified in the command options.
     * @param event the SlashCommandEvent representing the command invocation
     */
    @Override
    protected void execute(SlashCommandEvent event) {
        ELOUser.addElo(event.getOption("user").getAsUser().getIdLong(), event.getOption("points").getAsInt());
        event.reply("Added " + event.getOption("points").getAsInt() + " points to " + event.getOption("user").getAsUser().getAsTag()).queue();
    }
}
