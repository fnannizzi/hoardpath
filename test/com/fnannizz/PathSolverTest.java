package com.fnannizz;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * Testing interface methods of PathSolver.
 */
public class PathSolverTest {

    private final String testfilesPath = "./test/com/fnannizz/testfiles/";
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errorStream = new ByteArrayOutputStream();

    // Used to verify the solution output matches what is expected.
    private String readSolutionFile(String solutionFilePath) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(solutionFilePath));
        return new String(encoded, StandardCharsets.UTF_8);
    }

    @Before
    public void setUp() throws Exception {
        // Set output and error streams to capture PathSolver output
        System.setOut(new PrintStream(outputStream));
        System.setErr(new PrintStream(errorStream));
    }

    @Test
    public void testSetMap() throws Exception {
        PathSolver solver = new PathSolver();
        solver.initializeWithMap(testfilesPath + "basic/map.xml");
        solver.printMap();
        assertEquals("1 Hallway\n" + "2 Dining Room\n" + "3 Kitchen\n" + "4 Sun Room".trim(), outputStream.toString().trim());
    }

    @Test
    public void testSetScenario() throws Exception {
        PathSolver solver = new PathSolver();
        solver.initializeWithMap(testfilesPath + "basic/map.xml");
        solver.setScenario(testfilesPath + "basic/scenario.txt");
        solver.printItemsToCollect();
        assertEquals("potted plant\n" + "knife".trim(), outputStream.toString().trim());
    }

    @Test(expected = InvalidScenarioException.class)
    public void testSetScenarioExtraItem() throws Exception {
        PathSolver solver = new PathSolver();
        solver.initializeWithMap(testfilesPath + "basic/map.xml");
        solver.setScenario(testfilesPath + "basic/scenario_extra_item.txt");
        solver.solve();
    }

    @Test(expected = InvalidScenarioException.class)
    public void testSetScenarioBeforeMapInitialized() throws Exception {
        PathSolver solver = new PathSolver();
        solver.setScenario(testfilesPath + "basic/scenario.txt");
    }

    @Test(expected = InvalidScenarioException.class)
    public void testSetScenarioNoItems() throws Exception {
        PathSolver solver = new PathSolver();
        solver.initializeWithMap(testfilesPath + "basic/map.xml");
        solver.setScenario(testfilesPath + "basic/scenario_no_items.txt");
    }

    @Test(expected = InvalidScenarioException.class)
    public void testSetScenarioEmpty() throws Exception {
        PathSolver solver = new PathSolver();
        solver.initializeWithMap(testfilesPath + "basic/map.xml");
        solver.setScenario(testfilesPath + "basic/scenario_empty.txt");
    }

    @Test
    public void testSolvingBasicMap() throws Exception {
        PathSolver solver = new PathSolver();
        solver.initializeWithMap(testfilesPath + "basic/map.xml");
        solver.setScenario(testfilesPath + "basic/scenario.txt");
        String solution = readSolutionFile(testfilesPath + "basic/solution.txt");
        solver.solve();

        assertEquals(solution.trim(), outputStream.toString().trim());
    }

    @Test
    public void testSolvingComplexMap() throws Exception {
        PathSolver solver = new PathSolver();
        solver.initializeWithMap(testfilesPath + "complex/map.xml");
        solver.setScenario(testfilesPath + "complex/scenario.txt");
        String solution = readSolutionFile(testfilesPath + "complex/solution.txt");
        solver.solve();

        assertEquals(solution.trim(), outputStream.toString().trim());
    }

    @After
    public void tearDown() throws Exception {
        // Unset output and error streams before shutting down
        System.setOut(null);
        System.setErr(null);
    }

}