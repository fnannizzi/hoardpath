package com.fnannizz;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Representation of a single room.
 */
class Room {

    private String name;
    private String id;
    private ArrayList<String> items;
    private HashMap<String, String> adjacentRooms;

    public Room(String n, String i) {
        name = n;
        id = i;

        items = new ArrayList<>();
        adjacentRooms = new HashMap<>();
    }

    void addItemToRoom(String itemName) {
        items.add(itemName.toLowerCase());
    }

    void addConnectingRoom(String direction, String roomId) {
        adjacentRooms.put(roomId, direction);
    }

    HashMap<String, String> getAdjacentRooms() {
        return adjacentRooms;
    }

    String getDirectionTo(String roomId) {
        if (adjacentRooms.containsKey(roomId)) {
            return adjacentRooms.get(roomId);
        }
        else {
            return "Room " + roomId + "is not adjacent to " + name + ".";
        }
    }

    boolean containsItems() {
        return items.size() > 0;
    }

    ArrayList<String> getItems() {
        return items;
    }

    String getId() {
        return id;
    }

    String getName() {
        return name;
    }
}
