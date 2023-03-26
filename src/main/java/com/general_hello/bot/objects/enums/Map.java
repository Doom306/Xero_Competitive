package com.general_hello.bot.objects.enums;

import java.util.ArrayList;
import java.util.List;

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
        return this.name;
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

    /**
     * Returns a string with all the maps
     * @return a string with all the maps
     */
    public static String getMapString(List<Map> bannedMaps) {
        ArrayList<Map> listOfMaps = new ArrayList<>(List.of(Map.values()));
        listOfMaps.removeAll(bannedMaps);
        StringBuilder maps = new StringBuilder();
        for (Map map : listOfMaps) {
            maps.append(map.getName()).append(", ");
        }
        return maps.substring(0, maps.length() - 2);
    }
}