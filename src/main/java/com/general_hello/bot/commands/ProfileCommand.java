package com.general_hello.bot.commands;

import com.general_hello.bot.objects.ELOUser;
import com.general_hello.bot.objects.enums.Rank;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.text.DecimalFormat;
import java.util.List;

/**
 * A command that shows the profile of a Discord member. If no member is specified, the profile of the command invoker will be displayed.
 */
public class ProfileCommand extends SlashCommand {
    /**
     * Initializes a new instance of the {@link ProfileCommand} class.
     */
    public ProfileCommand() {
        this.name = "profile";
        this.help = "Shows your profile";
        // member option
        this.options = List.of(new OptionData[]{
                new OptionData(OptionType.USER, "member", "The member to show the profile of")
        });
    }

    /**
     * Executes the profile command and displays the profile of the specified member or the command invoker.
     * @param event The slash command event.
     */
    @Override
    protected void execute(SlashCommandEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        Member member = event.getMember();

        if (event.getOption("member") != null) {
            member = event.getOption("member").getAsMember();
        }
        embedBuilder.setTitle(member.getEffectiveName() + "'s Profile");
        long idLong = member.getIdLong();
        int elo = ELOUser.getElo(idLong);
        double winrate = ELOUser.getWinrate(idLong);
        winrate = Math.round(winrate * 100.0) / 100.0;
        DecimalFormat formatter = new DecimalFormat("###,###");

        embedBuilder.setDescription("**ELO points:** " + formatter.format(elo) + "\n" +
                "**Rank:** " + Rank.getRank(elo).getName() + "\n" +
                "**Wins:** " + ELOUser.getWins(idLong) + "\n" +
                "**Losses:** " + ELOUser.getLosses(idLong) + "\n" +
                "**Win rate:** " + winrate + "%");
        embedBuilder.setColor(event.getGuild().getSelfMember().getColor());
        embedBuilder.setFooter("Requested by " + event.getUser().getAsTag(), event.getUser().getAvatarUrl());
        event.replyEmbeds(embedBuilder.build()).queue();
    }
}
