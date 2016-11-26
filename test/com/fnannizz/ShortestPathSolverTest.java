package com.fnannizz;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by francesca on 11/26/16.
 */
public class ShortestPathSolverTest {

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errorStream = new ByteArrayOutputStream();

    @Before
    public void setUp() throws Exception {
        // Set output and error streams to capture RouteSolver output
        System.setOut(new PrintStream(outputStream));
        System.setErr(new PrintStream(errorStream));
    }

//    @Test
//    public void testComputeAllPermutationsOfArray1() throws Exception {
//        ArrayList<Integer> array = new ArrayList<Integer>() {{
//            add(1);
//            add(2);
//        }};
//        ShortestPathSolver.computeAllPermutationsOfArray(array, 0, System.out.println(array));
//        assertEquals("[1, 2]\n" + "[2, 1]".trim(), outputStream.toString().trim());
//    }
//
//    @Test
//    public void testComputeAllPermutationsOfArray2() throws Exception {
//        ArrayList<Integer> array = new ArrayList<Integer>() {{
//            add(1);
//            add(2);
//            add(3);
//            add(4);
//        }};
//        ShortestPathSolver.computeAllPermutationsOfArray(array, 0);
//        assertEquals("[1, 2]\n" + "[2, 1]".trim(), outputStream.toString().trim());
//    }

    @After
    public void tearDown() throws Exception {
        // Unset output and error streams before shutting down
        System.setOut(null);
        System.setErr(null);
    }
}