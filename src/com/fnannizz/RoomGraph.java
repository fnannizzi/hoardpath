package com.fnannizz;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by francesca on 11/25/16.
**/
class RoomGraph {
    private HashMap<String, Room> roomMap;
    private HashMap<String, String> itemLocationsMap;
    static String[] directions = { "north", "east", "south", "west" };

    RoomGraph() {
        roomMap = new HashMap<>();
        itemLocationsMap = new HashMap<>();
    }

    RoomGraph(HashMap<String,Room> rMap, HashMap<String,String> iMap) {
        roomMap = rMap;
        itemLocationsMap = iMap;
    }

    HashMap<String, Room> getRoomMap() {
        return roomMap;
    }

    // Using the list of known item locations, build a list of locations we need to visit in order to collect all items.
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

    boolean nodeExistsInMap(String id) {
        return roomMap.containsKey(id);
    }

    // Called only when asked to reset the scenario
    void clearItemLocations() {
        itemLocationsMap.clear();
    }

    void printMap() {
        for (String key : roomMap.keySet()) {
            System.out.println(key + " " + roomMap.get(key).getName());
        }
    }


}
