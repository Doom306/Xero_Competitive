package com.general_hello.commands.Objects;

import com.general_hello.commands.Database.DataUtils;
import com.general_hello.commands.Items.Initializer;
import com.general_hello.commands.Objects.User.Rank;
import net.dv8tion.jda.api.entities.Emoji;

public class Object {
    private final String name;
    private final Integer costToBuy;
    private final String emoji;
    private final String description;
    private final Rank rank;
    private final boolean patreonOnly;

    public Object(String name, Integer costToBuy, String emoji, String description, Rank rank, boolean patreonOnly) {
        this.name = name;
        this.costToBuy = costToBuy;
        this.emoji = emoji;
        this.description = description;
        this.rank = rank;
        this.patreonOnly = patreonOnly;
        Initializer.allObjects.put(name, this);
        Initializer.allNames.add(name);
    }

    public String getEmojiOfItem() {
        return emoji == null ? "" : emoji;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public Integer getCostToBuy() {
        return costToBuy;
    }

    public Long getEmojiId() {
        Emoji emoji = Emoji.fromMarkdown(this.emoji);
        return emoji.getIdLong();
    }

    public Rank getRank() {
        return rank;
    }

    public boolean isPatreonOnly() {
        return patreonOnly;
    }

    public String getEmojiUrl() {
        if (getEmojiOfItem().equals("")) {
            return "";
        }
        Emoji emoji = Emoji.fromMarkdown(getEmojiOfItem());
        if (emoji.isAnimated()) {
            return "https://cdn.discordapp.com/emojis/" + emoji.getId() + ".gif";
        }
        return "https://cdn.discordapp.com/emojis/" + emoji.getId() + ".png";

    }

    public String getFormattedPrice() {
        return RPGEmojis.berri + " " + DataUtils.formatter.format(this.costToBuy);
    }
}