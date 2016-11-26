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
    static private HashSet<String> directions;

    RoomGraph(String mapFilePath) {
        directions = new HashSet<String>() {{
            add("north");
            add("east");
            add("south");
            add("west");
        }};
        itemLocationsMap = new HashMap<>();
        roomMap = new HashMap<>();

        try {
            parseXML(mapFilePath);
        }
        // TODO: figure out what to do here
        catch (ParserConfigurationException | IOException | SAXException e) {
            System.out.println("ERROR: " + e.toString());
        }
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

    // TODO: decide if this should be moved to a separate class
    private void parseXML(String mapFilePath) throws ParserConfigurationException, IOException, SAXException {
        File file = new File(mapFilePath);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(file);

        NodeList roomList = doc.getElementsByTagName("room");

        for (int roomIndex = 0; roomIndex < roomList.getLength(); roomIndex++) {
            if (roomList.item(roomIndex).getNodeType() == Node.ELEMENT_NODE) {
                Element current = (Element) roomList.item(roomIndex);
                Room newRoom = new Room(current.getAttribute("name"), current.getAttribute("id"));

                // Add all the objects to the room
                NodeList objectList = current.getElementsByTagName("object");
                for (int objectIndex = 0; objectIndex < objectList.getLength(); objectIndex++) {
                    String itemName = ((Element) objectList.item(objectIndex)).getAttribute("name");
                    newRoom.addItemToRoom(itemName);
                    itemLocationsMap.put(itemName.toLowerCase(), newRoom.getId());
                }

                // Add all connecting rooms
                for (String direction : directions) {
                    if (!Objects.equals(current.getAttribute(direction),"")) {
                        newRoom.addConnectingRoom(direction, current.getAttribute(direction));
                    }
                }
                roomMap.put(newRoom.getId(), newRoom);
            }
        }
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
