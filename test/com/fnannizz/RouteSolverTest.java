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
 * Created by francesca on 11/25/16.
 */
public class RouteSolverTest {

    private final String testfilesPath = "./test/com/fnannizz/testfiles/";
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errorStream = new ByteArrayOutputStream();

    private String readSolutionFile(String solutionFilePath) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(solutionFilePath));
        return new String(encoded, StandardCharsets.UTF_8);
    }

    @Before
    public void setUp() throws Exception {
        // Set output and error streams to capture RouteSolver output
        System.setOut(new PrintStream(outputStream));
        System.setErr(new PrintStream(errorStream));
    }

    @Test
    public void testSetScenario() throws Exception {
        RouteSolver solver = new RouteSolver();
        solver.setScenario(testfilesPath + "basic/scenario.txt");
        solver.printItemsToCollect();
        assertEquals("potted plant\nknife".trim(), outputStream.toString().trim());
    }

    @Test
    public void testSolvingBasicMap() throws Exception {
        RouteSolver solver = new RouteSolver();
        solver.setMap(testfilesPath + "basic/map.xml");
        solver.setScenario(testfilesPath + "basic/scenario.txt");
        String solution = readSolutionFile(testfilesPath + "basic/solution.txt");
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