package com.fnannizz;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by francesca on 11/25/16.
 */
public class Room {
    private static final int NUM_CONNECTIONS = 4;

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
        items.add(itemName);
    }

    void addConnectingRoom(String direction, String roomId) {
        adjacentRooms.put(direction, roomId);
    }

    HashMap<String, String> getAdjacentRooms() {
        return adjacentRooms;
    }

    String getId() {
        return id;
    }

    String getName() {
        return name;
    }
}
