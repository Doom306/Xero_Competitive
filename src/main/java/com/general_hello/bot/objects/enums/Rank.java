package com.general_hello.bot.objects.enums;

/**
 * This enum represents different ranks in a game.
 */
public enum Rank {
    BRONZE(1500, "Bronze"),
    SILVER(1750, "Silver"),
    GOLD(2000, "Gold"),
    PLAT(2250, "Platinum"),
    DIA(2500, "Diamond"),
    MASTER(2750, "Master");

    private final int elo; // The Elo rating associated with this rank.
    private final String name; // The name of this rank.

    /**
     * Constructor for the Rank enum.
     * @param elo The Elo rating associated with this rank.
     * @param name The name of this rank.
     */
    Rank(int elo, String name) {
        this.elo = elo;
        this.name = name;
    }

    /**
     * Returns the Elo rating associated with this rank.
     * @return The Elo rating associated with this rank.
     */
    public int getElo() {
        return elo;
    }

    /**
     * Returns the Rank enum associated with the given Elo rating.
     * @param elo The Elo rating to retrieve the rank for.
     * @return The Rank enum associated with the given Elo rating.
     */
    public static Rank getRank(int elo) {
        for (Rank rank : Rank.values()) {
            if (elo < rank.getElo()) {
                return rank;
            }
        }
        return MASTER; // Default rank if Elo rating is higher than the maximum defined.
    }

    /**
     * Returns the Rank enum associated with the given rank name.
     * @param rank The name of the rank to retrieve.
     * @return The Rank enum associated with the given rank name, or null if not found.
     */
    public static Rank getRank(String rank) {
        for (Rank r : Rank.values()) {
            if (r.name().equalsIgnoreCase(rank)) {
                return r;
            }
        }
        return null; // Rank not found.
    }

    /**
     * Get the win point for the rank.
     * @return the win point associated with the rank
     */
    public int getWinPoint() {
        if (this == BRONZE) {
            return 26;
        } else if (this == SILVER) {
            return 22;
        } else if (this == GOLD) {
            return 18;
        } else if (this == PLAT) {
            return 14;
        } else if (this == DIA) {
            return 10;
        } else if (this == MASTER) {
            return 6;
        }
        return 0;
    }

    /**
     * Get the loss point for the rank.
     * @return the loss point associated with the rank
     */
    public int getLossPoint() {
        if (this == BRONZE) {
            return -10;
        } else if (this == SILVER) {
            return -14;
        } else if (this == GOLD) {
            return -18;
        } else if (this == PLAT) {
            return -22;
        } else if (this == DIA) {
            return -26;
        } else if (this == MASTER) {
            return -30;
        }
        return 0;
    }

    /**
     * Returns the name of this rank.
     * @return The name of this rank.
     */
    public String getName() {
        return name;
    }
}