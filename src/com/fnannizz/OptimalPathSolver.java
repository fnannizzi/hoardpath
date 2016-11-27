package com.fnannizz;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Collections;

/**
 * Solves for the optimal path through a map to collect a given list of items. Implements the following algorithm:
 * 1) create a list of must-pass nodes (the start node, and each node that contains an item on the list)
 * 2) find the length of the shortest path between each pair of must-pass nodes using Dijkstra's algorithm
 * 3) for every permutation of the list of must-pass nodes (always beginning with the start node), compute the
 *    total path length to find the shortest path through all the nodes
 * 4) once the optimal the must-pass nodes is found, re-run Dijkstra's for each segment of the path (must-pass node
 *    to must-pass node) and rebuild the optimal path
 *
 * There is an important performance trade-off in this implementation of the algorithm. In a sense, it is wasteful to
 * redo the work to compute a path we've already computed once in step 4.
 *
 * memory growth: number of must-pass * number of must-pass * length of path between must-pass nodes
 * time complexity growth: number of must-pass * O(number of edges + number of nodes*log(number of nodes))
 */
class OptimalPathSolver {

    private Integer shortestPathLength;
    private ArrayList<PairStringInteger> shortestPath;
    private Integer[][] shortestPaths;

    OptimalPathSolver() {
        shortestPathLength = Integer.MAX_VALUE;
    }

    void findOptimalPath(MapData mapData, ArrayList<String> neededItems, String startingLocation) throws InvalidScenarioException {
        ArrayList<String> locationsOfNeededItems = getNeededNodes(mapData, startingLocation, neededItems);
        Integer numMustVisitNodes = locationsOfNeededItems.size();
        shortestPaths = new Integer[numMustVisitNodes][numMustVisitNodes];

        for (int start = 0; start < numMustVisitNodes; start++) {
            String currentStartNode = locationsOfNeededItems.get(start);

            ShortestPathData shortestPathData = ShortestPathSolver.findShortestPathFromNode(currentStartNode, mapData.roomMap);

            // update the shortest paths registry
            for (int end = 0; end < numMustVisitNodes; end++) {
                String currentEndNode = locationsOfNeededItems.get(end);
                shortestPaths[start][end] = shortestPathData.getDistance(currentEndNode);
            }
        }

        // We need to be able to reorder this list, while still maintaining knowledge of the original index
        // of the room id in locationsOfNeededItems. This is how we access the matrix of shortest paths.
        // Because Collections.swap requires a List type, we use a list of room id-index pairs rather than something
        // like a hash map.
        ArrayList<PairStringInteger> roomIdsAndLocationIndices = new ArrayList<>();
        for (int i = 0; i < numMustVisitNodes; i++) {
            roomIdsAndLocationIndices.add(new PairStringInteger(locationsOfNeededItems.get(i), i));
        }
        shortestPathLength = Integer.MAX_VALUE;
        computeAllPermutationsOfArray(roomIdsAndLocationIndices, 1);

        if (shortestPathLength == Integer.MAX_VALUE) {
            System.out.println("Unable to find a path to collect all needed items.");
        }
        else {
            ArrayList<String> optimalPath = computeBestPath(mapData);
            printSolution(mapData, optimalPath, neededItems);
        }
    }

    private ArrayList<String> getNeededNodes(MapData mapData, String startingLocation, ArrayList<String> neededItems) throws InvalidScenarioException {
        ArrayList<String> locationsOfNeededItems = mapData.getLocationsOfNeededItems(neededItems);


        // The number of must-visit nodes is the number of nodes containing objects we need, plus the starting node if
        // it isn't already in the list of must-visits. The starting node must be the first node in the list, so
        // remove it from it's original location if needed.
        if (locationsOfNeededItems.contains(startingLocation)) {
            locationsOfNeededItems.remove(startingLocation);
        }
        locationsOfNeededItems.add(0, startingLocation);

        return locationsOfNeededItems;
    }

    private void computePathLength(ArrayList<PairStringInteger> roomIds) {
        Integer pathLength = 0;
        for (int i = 1; i < roomIds.size(); i++) {
            pathLength += shortestPaths[roomIds.get(i-1).getInteger()][roomIds.get(i).getInteger()];
        }
        if (pathLength < shortestPathLength) {
            shortestPathLength = pathLength;
            shortestPath = new ArrayList<>(roomIds);
        }
    }

    private void computeAllPermutationsOfArray(ArrayList<PairStringInteger> array, Integer index){
        if (Objects.equals(index, array.size())) {
            computePathLength(array);
            return;
        }
        for (int j = index; j < array.size(); j++) {
            Collections.swap(array, index, j);
            computeAllPermutationsOfArray(array, index + 1);
            Collections.swap(array, index, j);
        }
    }

    private ArrayList<String> computeBestPath(MapData mapData) {
        Integer beginSectionIndex = shortestPath.size() - 2;
        Integer endSectionIndex = shortestPath.size() - 1;
        ArrayList<String> path = new ArrayList<>();
        while (beginSectionIndex >= 0) {
            String beginSectionNode = shortestPath.get(beginSectionIndex).getStr();
            String endSectionNode = shortestPath.get(endSectionIndex).getStr();

            // Rerunning this algorithm repeatedly is expensive, but the extra time cost outweighs
            // the huge memory cost of storing all the potential paths. Instead of trying to keep
            // this information in memory, we rebuild the paths between critical nodes.
            ShortestPathData shortestPathData = ShortestPathSolver.findShortestPathFromNode(beginSectionNode, mapData.roomMap);
            while (!Objects.equals(endSectionNode, beginSectionNode)) {
                path.add(endSectionNode);
                endSectionNode = shortestPathData.getPrevious(endSectionNode);

            }
            if (beginSectionIndex == 0) {
                path.add(beginSectionNode);
            }
            beginSectionIndex--;
            endSectionIndex--;
        }
        Collections.reverse(path);
        return path;
    }

    private void printSolution(MapData mapData, ArrayList<String> path, ArrayList<String> neededItems) {
        System.out.println("Found an optimal path of length " + shortestPathLength + ".");
        System.out.println("-------------------------------------------------");

        Integer nextRoomIndex = 1;
        for (String roomId : path) {
            System.out.println("Entering " + roomId + ".");
            Room currentRoom = mapData.roomMap.get(roomId);
            if (currentRoom.containsItems()) {
                ArrayList<String> items = currentRoom.getItems();
                for (String item : items) {
                    if (neededItems.contains(item)) {
                        System.out.println("Picking up " + item + ".");
                    }
                }
            }
            if (nextRoomIndex < path.size()) {
                System.out.println("Moving " + currentRoom.getDirectionTo(path.get(nextRoomIndex)) + ".");
            }

            nextRoomIndex++;
        }
        System.out.println("All items collected.");
    }
}

// Used to implement a priority queue with integer priority values, as well as grouping
// string room ids to their numeric array index.
class PairStringInteger implements Comparable<PairStringInteger> {
    private String string;
    private Integer integer;

    PairStringInteger(String s, Integer i) {
        string = s;
        integer = i;
    }

    String getStr() {
        return string;
    }

    Integer getInteger() {
        return integer;
    }

    @Override public int compareTo(PairStringInteger other) {
        return Integer.compare(this.integer, other.integer);
    }
}