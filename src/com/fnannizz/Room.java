package com.fnannizz;

import java.util.ArrayList;

/**
 * Created by francesca on 11/25/16.
 */
public class Room {
    private static final int NUM_CONNECTIONS = 4;

    private String name;
    private Integer id;
    private ArrayList<String> items;
    private ArrayList<Integer> adjacentRooms;

    public Room() {
        name = "DEAD END";
    }

    public Room(String n, Integer i) {
        name = n;
        id = i;

        items = new ArrayList<>();
        adjacentRooms = new ArrayList<Integer>() {{
            add(-1);
            add(-1);
            add(-1);
            add(-1);
        }};
    }

    public void addItemToRoom(String itemName) {
        items.add(itemName);
    }

    public void addConnectingRoom(Integer directionIndex, Integer roomIndex) {
        adjacentRooms.set(directionIndex, roomIndex);
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean doesRoomContainItem(String itemName) {
        return items.contains(itemName);
    }
}
