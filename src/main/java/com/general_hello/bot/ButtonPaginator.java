package com.general_hello.bot;

import com.general_hello.bot.utils.EmbedUtil;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.MessageEditAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import net.dv8tion.jda.internal.utils.Checks;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * A paginator that uses buttons to navigate through pages of items.
 *
 * @author Koala
 * @version 1.0
 * @since 1.0
 */

@SuppressWarnings("unused")
public class ButtonPaginator {
    private static final Button first = Button.secondary("first", Emoji.fromCustom("first", 930264043564961822L, false));
    private static final Button previous = Button.secondary("previous", Emoji.fromCustom("left", 915425233215827968L, false));
    private static final Button next = Button.secondary("next", Emoji.fromCustom("right", 915425310592356382L, false));
    private static final Button last = Button.secondary("last", Emoji.fromCustom("last", 930264202331975701L, false));
    private static final Button refresh = Button.secondary("refresh", Emoji.fromUnicode("🔄"));

    private final EventWaiter waiter;
    private final int itemsPerPage;
    private final int pages;
    private final long timeout;
    private String[] items;
    private final JDA jda;
    private final Set<Long> allowedUsers;
    private final boolean numbered;
    private final String title;
    private final Color color;
    private final String footer;
    private final ButtonPaginator.Builder builder;
    private int page = 1;
    private boolean interactionStopped = false;

    private final String thumbnail;

    private ButtonPaginator(EventWaiter waiter, long timeout, String[] items, JDA jda,
                            Set<Long> allowedUsers, int itemsPerPage, boolean numberedItems,
                            String title, Color color, String footer, ButtonPaginator.Builder builder, String thumbnail) {
        this.waiter = waiter;
        this.timeout = timeout;
        this.items = items;
        this.jda = jda;
        this.allowedUsers = Collections.unmodifiableSet(allowedUsers);
        this.itemsPerPage = itemsPerPage;
        this.numbered = numberedItems;
        this.title = title;
        this.color = color;
        this.footer = footer;
        this.pages = (int) Math.ceil((double) items.length / itemsPerPage);
        this.builder = builder;
        this.thumbnail = thumbnail;
    }

    public void paginate(Message message, int page)
    {
        this.page = page;
        message.editMessage("\u200E").setEmbeds(getEmbed(page)).setComponents(getButtonLayout(page))
                .queue(m -> waitForEvent(m.getChannel().getIdLong(), m.getIdLong()));
    }

    public void paginate(SlashCommandEvent slashCommandEvent, int page)
    {
        this.page = page;
        slashCommandEvent.replyEmbeds(getEmbed(page)).setComponents(getButtonLayout(page))
                .queue(m -> m.retrieveOriginal().queue((message -> waitForEvent(slashCommandEvent.getChannel().asPrivateChannel().getIdLong(), message.getIdLong()))));
    }

    public void paginate(TextChannel textChannel, int page)
    {
        this.page = page;
        textChannel.sendMessageEmbeds(getEmbed(page)).setComponents(getButtonLayout(page))
                .queue(m -> waitForEvent(m.getChannel().getIdLong(), m.getIdLong()));
    }

    public void paginate(MessageEditAction messageAction, int page)
    {
        this.page = page;
        messageAction.setEmbeds(getEmbed(page)).setComponents(getButtonLayout(page))
                .queue(m -> waitForEvent(m.getChannel().getIdLong(), m.getIdLong()));
    }

    public void paginate(WebhookMessageEditAction<Message> action, int page)
    {
        this.page = page;
        action.setEmbeds(getEmbed(page)).setComponents(getButtonLayout(page))
                .queue(m -> waitForEvent(m.getChannel().getIdLong(), m.getIdLong()));
    }

    private ActionRow getButtonLayout(int page)
    {
        if (pages > 2)
            return ActionRow.of(
                    page <= 1 ? first.asDisabled() : first,
                    page <= 1 ? previous.asDisabled() : previous,
                    page >= pages ? next.asDisabled() : next,
                    page >= pages ? last.asDisabled() : last);
        else
            return ActionRow.of(
                    page <= 1 ? previous.asDisabled() : previous,
                    page >= pages ? next.asDisabled() : next);
    }

    private void waitForEvent(long channelId, long messageId)
    {
        try {
            waiter.waitForEvent(
                    ButtonInteractionEvent.class,
                    event ->
                    {
                        if (interactionStopped) return false;
                        if (messageId != event.getMessageIdLong()) return false;
                        if (allowedUsers.size() >= 1) {
                            if (!allowedUsers.contains(event.getUser().getIdLong())) {
                                //event.deferEdit().queue(s -> {}, e -> {});
                                return false;
                            }
                        }
                        return true;
                    },
                    event ->
                    {
                        switch (event.getComponentId()) {
                            case "previous" -> {
                                page--;
                                if (page < 1) page = 1;
                                event.editMessageEmbeds(getEmbed(this.page)).setComponents(getButtonLayout(page)).queue();
                                waitForEvent(event.getChannel().getIdLong(), event.getMessageIdLong());
                            }
                            case "next" -> {
                                page++;
                                if (page > pages) page = pages;
                                event.editMessageEmbeds(getEmbed(this.page)).setComponents(getButtonLayout(page)).queue();
                                waitForEvent(event.getChannel().getIdLong(), event.getMessageIdLong());
                            }
                            case "stop" -> {
                                interactionStopped = true;
                                if (!event.getMessage().isEphemeral())
                                    event.getMessage().delete().queue(s -> {
                                    }, e -> {
                                    });
                                else
                                    event.editMessageEmbeds(getEmbed(page)).setComponents(Collections.emptyList()).queue();
                            }
                            case "first" -> {
                                page = 1;
                                event.editMessageEmbeds(getEmbed(this.page)).setComponents(getButtonLayout(page)).queue();
                                waitForEvent(event.getChannel().getIdLong(), event.getMessageIdLong());
                            }
                            case "last" -> {
                                page = pages;
                                event.editMessageEmbeds(getEmbed(this.page)).setComponents(getButtonLayout(page)).queue();
                                waitForEvent(event.getChannel().getIdLong(), event.getMessageIdLong());
                            }
                            case "refresh" -> {
                                page = 1;
                                ArrayList<String> refresh = builder.refresh(event);
                                String[] convertedItems = new String[refresh.size()];
                                int x = 0;
                                while (x < refresh.size()) {
                                    convertedItems[x] = refresh.get(x);
                                    x++;
                                }
                                this.items = convertedItems;
                                try {
                                    event.editMessageEmbeds(getEmbed(this.page)).setComponents(getButtonLayout(page)).queue();
                                } catch (Exception ignored) {
                                }
                                waitForEvent(event.getChannel().getIdLong(), event.getMessageIdLong());
                            }
                        }
                    },
                    timeout,
                    TimeUnit.SECONDS,
                    () ->
                    {
                        interactionStopped = true;
                        TextChannel channel = jda.getTextChannelById(channelId);
                        if (channel == null) return;
                        channel.retrieveMessageById(messageId)
                                .flatMap(m -> m.editMessageComponents(Collections.emptyList()))
                                .flatMap(Message::delete)
                                .queue(s -> {
                                }, e -> {
                                });
                    }
            );
        } catch (Exception ignored) {}
    }

    private MessageEmbed getEmbed(int page)
    {
        if (page > pages) page = pages;
        if (page < 1) page = 1;
        int start = page == 1 ? 0 : ((page - 1) * itemsPerPage);
        int end = Math.min(items.length, page * itemsPerPage);
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < end; i++)
        {
            sb.append(numbered ? "**" + (i + 1) + ".** " : "").append(this.items[i]).append("\n");
        }
        EmbedBuilder builder = new EmbedBuilder()
                .setFooter("Page " + page + "/" + pages + (footer != null ? " • "+footer : ""))
                .setColor(color)
                .setTitle(this.title)
                .setDescription(sb.toString().trim())
                .setImage(thumbnail);
        return builder.build();
    }

    @SuppressWarnings("unused")
    public static abstract class Builder
    {
        private final JDA jda;
        private EventWaiter waiter;
        private long timeout = -1;
        private String[] items;
        private final Set<Long> allowedUsers = new HashSet<>();
        private int itemsPerPage = 10;
        private boolean numberItems = true;
        private String title = null;
        private Color color;
        private String footer;

        private String thumbnail;

        public Builder(JDA jda)
        {
            this.jda = jda;
        }

        public Builder setEventWaiter(EventWaiter waiter)
        {
            this.waiter = waiter;
            return this;
        }

        public Builder setTimeout(long delay, TimeUnit unit)
        {
            Checks.notNull(unit, "TimeUnit");
            Checks.check(delay > 0, "Timeout must be greater than 0!");
            timeout = unit.toSeconds(delay);
            return this;
        }

        public Builder setThumbnail(String thumbnail)
        {
            this.thumbnail = thumbnail;
            return this;
        }

        public Builder setItems(String[] items)
        {
            this.items = items;
            return this;
        }

        public Builder setItems(ArrayList<String> items)
        {
            String[] convertedItems = new String[items.size()];
            int x = 0;
            while (x < items.size()) {
                convertedItems[x] = items.get(x);
                x++;
            }
            this.items = convertedItems;
            return this;
        }

        public Builder addAllowedUsers(Long... userIds)
        {
            allowedUsers.addAll(Set.of(userIds));
            return this;
        }

        public Builder setColor(Color color)
        {
            this.color = color;
            return this;
        }

        public Builder setColor(int color)
        {
            this.color = EmbedUtil.intToColor(color);
            return this;
        }

        public Builder setItemsPerPage(int items)
        {
            Checks.check(items > 0, "Items per page must be at least 1");
            this.itemsPerPage = items;
            return this;
        }

        public void useNumberedItems(boolean b)
        {
            this.numberItems = b;
        }

        public Builder setTitle(String title)
        {
            this.title = title;
            return this;
        }

        public Builder setFooter(String footer)
        {
            this.footer = footer;
            return this;
        }

        public ButtonPaginator build()
        {
            Checks.notNull(waiter, "Waiter");
            Checks.check(timeout != -1, "You must set a timeout using #setTimeout()!");
            Checks.noneNull(items, "Items");
            Checks.notEmpty(items, "Items");
            return new ButtonPaginator(waiter, timeout, items, jda, allowedUsers, itemsPerPage, numberItems, title, color == null ? Color.black : color, footer, this, thumbnail);
        }

        protected abstract ArrayList<String> refresh(ButtonInteractionEvent event);
    }
}
