package com.general_hello.bot.commands.mod;

import com.general_hello.bot.objects.ELOUser;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

/**
 * A class representing the "ban" slash command.
 * This command allows an owner of the server to ban a user.
 */
public class BanCommand extends SlashCommand {
    /**
     * Constructs a new BanCommand object.
     * Sets the name, help message, and required options for the command.
     * Also specifies that this command can only be used in a guild and is an owner-only command.
     */
    public BanCommand() {
        this.name = "ban";
        this.help = "Bans a user";
        this.guildOnly = true;
        this.ownerCommand = true;
        this.options = List.of(new OptionData[]{
                new OptionData(OptionType.USER, "user", "The user to ban", true)
        });
    }

    /**
     * Executes the "ban" command.
     * Bans the specified user from the guild.
     * @param event The SlashCommandEvent representing the "ban" command.
     */
    @Override
    protected void execute(SlashCommandEvent event) {
        ELOUser.banUser(event.getOption("user").getAsUser().getIdLong());
        event.reply("Banned " + event.getOption("user").getAsUser().getAsTag()).queue();
    }
}
