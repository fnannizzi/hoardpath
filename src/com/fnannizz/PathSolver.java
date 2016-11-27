package com.fnannizz;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;

/**
 * This class provides a simplified interface and manages the shared data
 * object MapData.
 */
public class PathSolver {
    private MapData mapData;
    private ArrayList<String> itemsToCollect;
    private String startingLocation;

    /**
     * Constructor should not throw exceptions. Constructor call should always
     * be followed by call to initializeWithMap, otherwise an exception will be
     * thrown.
     */
    PathSolver() {
        itemsToCollect = new ArrayList<>();
    }

    /**
     * Initialize map data from map.xml. Should always be called after constructor.
     * @param mapFilePath path of map.xml
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    void initializeWithMap(String mapFilePath) throws ParserConfigurationException, IOException, SAXException {
        mapData = MapFactory.makeMap(mapFilePath);
    }

    /**
     * Initialize scenario data from scenario.txt. Clears old scenario data between uses,
     * allowing a map to be used with multiple scenarios.
     * @param scenarioFilePath path of scenario.txt
     * @throws InvalidScenarioException
     */
    void setScenario(String scenarioFilePath) throws InvalidScenarioException, IOException {
        if (mapData == null) {
            throw new InvalidScenarioException("Please initialize the map before specifying a scenario.");
        }

        // Allow the mapData to be reused with multiple scenarios
        if (itemsToCollect.size() > 0) {
            itemsToCollect.clear();
            mapData.clearItemLocations();
        }

        parseScenarioFile(scenarioFilePath);
    }

    /*
     * Parse scenario file and initialize starting:ocation and itemsToCollect.
     */
    private void parseScenarioFile(String scenarioFilePath) throws InvalidScenarioException, IOException {
        BufferedReader in = new BufferedReader(new FileReader(scenarioFilePath));
        String str;
        startingLocation = in.readLine();
        if (startingLocation == null || !mapData.nodeExistsInMap(startingLocation)) {
            throw new InvalidScenarioException("Invalid starting location.");
        }

        while ((str = in.readLine()) != null) {
            itemsToCollect.add(str.toLowerCase());
        }

        if (itemsToCollect.size() < 1) {
            throw new InvalidScenarioException("No items found in the scenario input file.");
        }
    }

    /**
     * Print the optimal path through the map given a list of items to collect.
     * @throws InvalidScenarioException
     */
    void solve() throws InvalidScenarioException {
        if (itemsToCollect.size() < 1) {
            throw new InvalidScenarioException("Please provide a list of items to collect before attempting to solve.");
        }
        OptimalPathSolver solver = new OptimalPathSolver();
        solver.findOptimalPath(mapData, itemsToCollect, startingLocation);
    }

    /**
     * Print the list of items to be collected.
     */
    void printItemsToCollect() {
        for (String item : itemsToCollect) {
            System.out.println(item);
        }
    }

    /**
     * Print the map.
     */
    void printMap() {
        mapData.printMap();
    }
}
