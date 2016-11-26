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

class Path {
    private ArrayList<Integer> path;

    public void addToPath(Integer node) {
        path.add(node);
    }
}

class RoomMap {
    private HashMap<String, Room> roomMap;
    private HashMap<String, String> itemLocationsMap;
    static private HashMap<String, Integer> directionsMap;

    RoomMap(String mapFilePath) {
        directionsMap = new HashMap<String, Integer>() {{
            put("north", 0);
            put("east",  1);
            put("south", 2);
            put("west",  3);
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

    // Called only when asked to reset the scenario
    void clearItemLocations() {
        itemLocationsMap.clear();
    }

    Integer getNumberOfRooms() {
        return roomMap.size();
    }

    public void findShortestPath(ArrayList<String> itemsToCollect, Integer startNode) throws InvalidScenarioException {
        ArrayList<String> locationsOfNeededItems = getLocationsOfNeededItems(itemsToCollect);

        // The number of must-visit nodes is the number of nodes containing objects we need, plus the start node if
        // it isn't already in the list of must-visits
        Integer numMustVisitNodes;
        if (locationsOfNeededItems.contains(startNode)) {
            numMustVisitNodes = locationsOfNeededItems.size();
        }
        else {
            numMustVisitNodes = locationsOfNeededItems.size() + 1;
        }

        Path[][] shortestPaths = new Path[numMustVisitNodes][numMustVisitNodes];

        for (int start = 0; start < numMustVisitNodes; start++) {
            for (int end = 0; end < numMustVisitNodes; end++) {
                if (start == end) {
                    //shortestPaths[start][end] = ;
                }
            }
        }




    }

    private ArrayList<String> getLocationsOfNeededItems(ArrayList<String> itemsToCollect) throws InvalidScenarioException {
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
                for (String direction : directionsMap.keySet()) {
                    if (!Objects.equals(current.getAttribute(direction),"")) {
                        newRoom.addConnectingRoom(new Integer(directionsMap.get(direction)), current.getAttribute(direction));
                    }
                }
                roomMap.put(newRoom.getId(), newRoom);
            }
        }
    }

    void printMap() {
        for (String key : roomMap.keySet()) {
            System.out.println(key + " " + roomMap.get(key).getName());
        }
    }


}
