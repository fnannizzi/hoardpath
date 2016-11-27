package com.fnannizz;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class Main {
    /**
     *
     * @param args, args[0] = path to map.xml args[1] = path to scenario.txt
     * @throws InvalidScenarioException, when user error results in a bad state
     */
    public static void main(String[] args) throws InvalidScenarioException {
        try {
            PathSolver solver = new PathSolver();
            solver.initializeWithMap(args[0]);
            solver.setScenario(args[1]);
            solver.solve();
        }
        catch (IOException e) {
            System.out.println("Unable to open file: " + e.toString());
            System.exit(1);
        }
        catch (ParserConfigurationException | SAXException e) {
            System.out.println("Error parsing map.xml: " + e.toString());
            System.exit(1);
        }
    }
}