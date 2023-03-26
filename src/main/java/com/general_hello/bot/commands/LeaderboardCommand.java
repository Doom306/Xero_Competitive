package com.general_hello.bot.commands;

import com.general_hello.Bot;
import com.general_hello.bot.ButtonPaginator;
import com.general_hello.bot.objects.ELOUser;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * A slash command to display a leaderboard of the server's members sorted by their ELO points.
 */
public class LeaderboardCommand extends SlashCommand {
    /**
     * Constructs a new LeaderboardCommand object and sets its name and help message.
     */
    public LeaderboardCommand() {
        this.name = "leaderboard";
        this.help = "Shows the leaderboard of the server";
    }

    /**
     * Executes the leaderboard command.
     * Retrieves a list of members, removes all bots from it, sorts the remaining members by their ELO points,
     * and displays the leaderboard using a ButtonPaginator.
     *
     * @param event The slash command event triggered by the user.
     */
    @Override
    protected void execute(SlashCommandEvent event) {
        List<Member> members = new ArrayList<>(event.getGuild().getMembers());
        event.reply("Sending leaderboard...").setEphemeral(true).queue();

        // remove all bots
        for (int i = 0; i < members.size(); i++) {
            if (members.get(i).getUser().isBot()) {
                members.remove(i);
                i--;
            }
        }

        // sort members by elo points
        for (int i = 0; i < members.size(); i++) {

            for (int j = 0; j < members.size() - 1; j++) {
                if (ELOUser.getElo(members.get(j).getIdLong()) < ELOUser.getElo(members.get(j + 1).getIdLong())) {
                    Member temp = members.get(j);
                    members.set(j, members.get(j + 1));
                    members.set(j + 1, temp);
                }
            }
        }

        StringBuilder stringBuilder = new StringBuilder();
        int rank = 1;
        ArrayList<String> list = new ArrayList<>();
        DecimalFormat formatter = new DecimalFormat("###,###");
        for (Member member : members) {
            if (member.getUser().isBot()) continue;
            int points = ELOUser.getElo(member.getIdLong());
            stringBuilder.append(rank).append(". **").append(member.getEffectiveName()).append("** - ").append(formatter.format(points)).append(" points");
            list.add(stringBuilder.toString());
            stringBuilder = new StringBuilder();
            rank++;
        }

        // ButtonPaginator
        ButtonPaginator.Builder builder = new ButtonPaginator.Builder(event.getJDA()) {
            @Override
            protected ArrayList<String> refresh(ButtonInteractionEvent event) {
                return null;
            }
        }
        .setEventWaiter(Bot.getBot().getEventWaiter())
                .setItems(list)
                .setColor(event.getGuild().getSelfMember().getColor())
                .setTitle("Leaderboard")
                .setItemsPerPage(8)
                .setTimeout(350, TimeUnit.DAYS);
        builder.useNumberedItems(false);
        builder.build().paginate(event.getTextChannel(), 1);
    }
}
