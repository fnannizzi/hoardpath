package com.fnannizz;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

/**
 * Created by francesca on 11/27/16.
 */
public class MapFactory {

    static RoomGraph makeMap(String mapFilePath) {
        try {
            return parseXML(mapFilePath);
        }
        catch (IOException e) {
            System.out.println("Unable to open map.xml: " + e.toString());
        }
        catch (ParserConfigurationException | SAXException e) {
            System.out.println("Error parsing map.xml: " + e.toString());
        }
        return new RoomGraph();
    }

     static private RoomGraph parseXML(String mapFilePath) throws ParserConfigurationException, IOException, SAXException {
         HashMap<String, Room> roomMap = new HashMap<>();
         HashMap<String, String> itemLocationsMap = new HashMap<>();

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
                for (String direction : RoomGraph.directions) {
                    if (!Objects.equals(current.getAttribute(direction),"")) {
                        newRoom.addConnectingRoom(direction, current.getAttribute(direction));
                    }
                }
                roomMap.put(newRoom.getId(), newRoom);
            }
        }
        return new RoomGraph(roomMap, itemLocationsMap);
    }
}
