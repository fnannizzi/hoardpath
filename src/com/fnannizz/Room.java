package com.fnannizz;

import java.util.ArrayList;

/**
 * Created by francesca on 11/25/16.
 */
public class Room {
    private static final int NUM_CONNECTIONS = 4;

    private String name;
    private String id;
    private ArrayList<String> items;
    private ArrayList<String> adjacentRooms;

    public Room() {
        name = "DEAD END";
    }

    public Room(String n, String i) {
        name = n;
        id = i;

        items = new ArrayList<>();
        adjacentRooms = new ArrayList<String>() {{
            add("");
            add("");
            add("");
            add("");
        }};
    }

    void addItemToRoom(String itemName) {
        items.add(itemName);
    }

    void addConnectingRoom(Integer directionIndex, String roomId) {
        adjacentRooms.set(directionIndex, roomId);
    }

    String getId() {
        return id;
    }

    String getName() {
        return name;
    }
}
