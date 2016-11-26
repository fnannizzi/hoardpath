package com.fnannizz;

import java.io.IOException;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Created by francesca on 11/25/16.
 */
class RouteSolver {
    private RoomGraph roomGraph;
    private ArrayList<String> itemsToCollect;
    private String startingLocation;

    RouteSolver(String mapFilePath) {
        roomGraph = new RoomGraph(mapFilePath);
        itemsToCollect = new ArrayList<>();
    }

    void setScenario(String scenarioFilePath) throws InvalidScenarioException {
        // Allow the roomGraph to be reused with multiple scenarios
        if (itemsToCollect.size() > 0) {
            itemsToCollect.clear();
            roomGraph.clearItemLocations();
        }

        try {
            BufferedReader in = new BufferedReader(new FileReader(scenarioFilePath));
            String str;
            startingLocation = in.readLine();
            if (startingLocation == null || !roomGraph.nodeExistsInMap(startingLocation)) {
                throw new InvalidScenarioException("Starting location does not exist in roomGraph.");
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
        ShortestPathSolver solver = new ShortestPathSolver(roomGraph);
        solver.findShortestPath(itemsToCollect, startingLocation);
    }

    void printItemsToCollect() {
        for (String item : itemsToCollect) {
            System.out.println(item);
        }
    }

    void printMap() {
        roomGraph.printMap();
    }
}
