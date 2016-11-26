package com.fnannizz;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Created by francesca on 11/25/16.
 */
public class RouteSolver {
    private RoomMap map;
    private ArrayList<String> itemsToCollect;



    public void setMap(String mapFilePath) {
        map = new RoomMap(mapFilePath);
    }

    public void setScenario(String scenarioFilePath) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(scenarioFilePath));
            String str;
            itemsToCollect = new ArrayList<>();
            // Skip the first line
            in.readLine();
            while ((str = in.readLine()) != null) {
                itemsToCollect.add(str);
            }
        }
        catch (IOException e) {
            System.out.println("ERROR: " + e.toString());
        }
    }

    public void printItemsToCollect() {
        for (String item : itemsToCollect) {
            System.out.println(item);
        }
    }

    public void solve() {

    }
}
