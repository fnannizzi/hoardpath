package com.fnannizz;

import java.io.IOException;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Objects;

/**
 * Created by francesca on 11/25/16.
 */
class RouteSolver {
    private RoomMap map;
    private ArrayList<String> itemsToCollect;
    private String startNode;

    RouteSolver(String mapFilePath) {
        map = new RoomMap(mapFilePath);
        itemsToCollect = new ArrayList<>();
    }

    void setScenario(String scenarioFilePath) throws InvalidScenarioException {
        // Allow the map to be reused with multiple scenarios
        if (itemsToCollect.size() > 0) {
            itemsToCollect.clear();
            map.clearItemLocations();
        }

        try {
            BufferedReader in = new BufferedReader(new FileReader(scenarioFilePath));
            String str;
            startNode = in.readLine();
            if (startNode == null || !map.nodeExistsInMap(startNode)) {
                throw new InvalidScenarioException("Starting location does not exist in map.");
            }

            while ((str = in.readLine()) != null) {
                itemsToCollect.add(str.toLowerCase());
            }
        }
        catch (IOException e) {
            System.out.println("ERROR: " + e.toString());
        }

        if (itemsToCollect.size() < 1) {
            throw new InvalidScenarioException("No items found in the scenario input file.");
        }
    }

    void solve() throws InvalidScenarioException {
        if (itemsToCollect.size() < 1) {
            throw new InvalidScenarioException("Please provide a list of items to collect before attempting to solve.");
        }
        ShortestPathSolver solver = new ShortestPathSolver(map);
        solver.findShortestPath(itemsToCollect, startNode);
    }

    void printItemsToCollect() {
        for (String item : itemsToCollect) {
            System.out.println(item);
        }
    }

    void printMap() {
        map.printMap();
    }
}
