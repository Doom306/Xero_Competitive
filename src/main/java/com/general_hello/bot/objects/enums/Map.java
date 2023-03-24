package com.general_hello.bot.objects.enums;

public enum Map {
    // The maps are: Station-1, Station-2, Temple-M, Colosseum, Side-3, Ice Square and Wonderland.
    STATION_1("Station-1"),
    STATION_2("Station-2"),
    TEMPLE_M("Temple-M"),
    COLOSSEUM("Colosseum"),
    SIDE_3("Side-3"),
    ICE_SQUARE("Ice Square"),
    WONDERLAND("Wonderland");

    private final String name;

    /**
     * Constructor for the Map enum class.
     * @param name the name of the map.
     */
    Map(String name) {
        this.name = name;
    }

    /**
     * Returns the name of the map.
     * @return the name of the map.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the Map constant with the given name.
     * @param name the name of the map to get.
     * @return the Map constant with the given name, or null if not found.
     */
    public static Map getMap(String name) {
        for (Map map : Map.values()) {
            if (map.getName().equalsIgnoreCase(name)) {
                return map;
            }
        }
        return null;
    }
}