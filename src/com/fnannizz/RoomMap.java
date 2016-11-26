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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * Created by francesca on 11/25/16.
**/

public class RoomMap {
    private HashMap<Integer, Room> map;
    private HashMap<String, Integer> itemLocationsMap;
    static private HashMap<String, Integer> directionsMap;

    public RoomMap(String mapFilePath) {
        directionsMap = new HashMap<String, Integer>() {{
            put("north", 0);
            put("east",  1);
            put("south", 2);
            put("west",  3);
        }};
        itemLocationsMap = new HashMap<>();
        map = new HashMap<>();

        try {
            parseXML(mapFilePath);
        }
        // TODO: figure out what to do here
        catch (ParserConfigurationException | IOException | SAXException e) {
            System.out.println("ERROR: " + e.toString());
        }
    }



    private void parseXML(String mapFilePath) throws ParserConfigurationException, IOException, SAXException {
        File file = new File(mapFilePath);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(file);

        NodeList roomList = doc.getElementsByTagName("room");

        for (int roomIndex = 0; roomIndex < roomList.getLength(); roomIndex++) {
            if (roomList.item(roomIndex).getNodeType() == Node.ELEMENT_NODE) {
                Element current = (Element) roomList.item(roomIndex);
                Room newRoom = new Room(current.getAttribute("name"), new Integer(current.getAttribute("id")));

                // Add all the objects to the room
                NodeList objectList = current.getElementsByTagName("object");
                for (int objectIndex = 0; objectIndex < objectList.getLength(); objectIndex++) {
                    String itemName = ((Element) objectList.item(objectIndex)).getAttribute("name");
                    newRoom.addItemToRoom(itemName);
                    itemLocationsMap.put(itemName, newRoom.getId());
                }

                // Add all connecting rooms
                for (String direction : directionsMap.keySet()) {
                    if (!Objects.equals(current.getAttribute(direction),"")) {
                        Integer i = new Integer(current.getAttribute(direction));
                        Integer dir = new Integer(directionsMap.get(direction));
                        newRoom.addConnectingRoom(dir, i);
                    }
                }
                map.put(newRoom.getId(), newRoom);
            }
        }
    }

    public void printMap() {
        for (Integer key : map.keySet()) {
            System.out.println(key + " " + map.get(key).getName());
        }
    }


}
