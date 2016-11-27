package com.fnannizz;

import java.util.HashMap;
import java.util.ArrayList;

/**
 * Contains information about the game map which is needed to solve
 * for the optimal path.
**/
class MapData {

    /**
     * Model of the game map.
     */
    HashMap<String, Room> roomMap;

    /**
     * Rather than search the map for items later, store all the item locations in
     * a separate data structure.
     */
    private HashMap<String, String> itemLocationsMap;

    static String[] directions = { "north", "east", "south", "west" };

    /**
     * Constructor only called by MapFactory.
     */
    MapData(HashMap<String,Room> rMap, HashMap<String,String> iMap) {
        roomMap = rMap;
        itemLocationsMap = iMap;
    }

    boolean nodeExistsInMap(String id) {
        return roomMap.containsKey(id);
    }

    /**
     * Called only when asked to use a new scenario with existing map.
     */
    void clearItemLocations() {
        itemLocationsMap.clear();
    }

    /**
     * Using the list of known item locations, build a list of locations we need to visit in order to collect all items.
     * @param itemsToCollect list of items to collect from scenario.txt
     * @return list of rooms containing all items needed
     * @throws InvalidScenarioException if an item does not exist in the map
     */
    ArrayList<String> getLocationsOfNeededItems(ArrayList<String> itemsToCollect) throws InvalidScenarioException {
        ArrayList<String> locationsOfNeededItems = new ArrayList<>();
        for (String item : itemsToCollect) {
            if (itemLocationsMap.containsKey(item)) {
                locationsOfNeededItems.add(itemLocationsMap.get(item));
            }
            else {
                throw new InvalidScenarioException("The item " + item + " does not exist in the given map.");
            }
        }
        return locationsOfNeededItems;
    }

    void printMap() {
        for (String key : roomMap.keySet()) {
            System.out.println(key + " " + roomMap.get(key).getName());
        }
    }


}
