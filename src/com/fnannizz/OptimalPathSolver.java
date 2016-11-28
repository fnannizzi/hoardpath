package com.fnannizz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

/**
 * Solves for the optimal path through a map to collect a given list of items. Implements the following algorithm:
 * 1) create a list of must-visit nodes (the start node, and each node that contains an item on the list)
 * 2) find the length of the shortest path between each pair of must-visit nodes using Dijkstra's algorithm
 * 3) for every permutation of the list of must-visit nodes (always beginning with the start node), compute the
 *    total path length to find the shortest path through all the nodes
 * 4) once the optimal the must-visit nodes is found, rebuild the path by piecing together the path segments
 *
 * There is an important performance trade-off in this implementation of the algorithm. The memory needed to store
 * all possible paths as they are determined is considerable - it increases the memory usage by
 * O(number of must-visit nodes * number of must-visit nodes * length of path between must-visit nodes)
 *
 * I tested this implementation against an alternate implementation which stored only the length of
 * the path, and thus needed to re-run Dijkstra's to rebuild each path segment after determining which path
 * was shortest. That implementation requires additional time on the order of
 * O(number of must-visit nodes * number of edges + number of must-visit nodes * number of nodes * log(number of nodes))
 *
 * The performance difference was noticeable even on small graphs, so I have opted for the more memory-intensive but
 * faster version here.
 */
class OptimalPathSolver {

    private Integer shortestPathLength;
    private ArrayList<PairStringInteger> shortestPath;
    private ShortestPathData[][] shortestPaths;

    OptimalPathSolver() {
        shortestPathLength = Integer.MAX_VALUE;
    }

    /**
     * Main method of OptimalPathSolver, which relies on internal helper methods to compute the shortest path
     * through a map that collects a certain set of items.
     */
    void findOptimalPath(MapData mapData, ArrayList<String> neededItems, String startingLocation) throws InvalidScenarioException {

        // Make sure to reset everything if the class is being reused.
        if (shortestPathLength != Integer.MAX_VALUE) {
            shortestPathLength = Integer.MAX_VALUE;
            shortestPath.clear();
        }

        // Determine the set of must-visit nodes based on the needed items
        ArrayList<String> locationsOfNeededItems = getNeededNodes(mapData, startingLocation, neededItems);
        Integer numMustVisitNodes = locationsOfNeededItems.size();
        shortestPaths = new ShortestPathData[numMustVisitNodes][numMustVisitNodes];

        // Compute the shortest path between every pair of must-visit nodes
        for (int start = 0; start < numMustVisitNodes; start++) {
            String currentStartNode = locationsOfNeededItems.get(start);

            ShortestPathData shortestPathData = ShortestPathSolver.findShortestPathFromNode(currentStartNode, mapData.roomMap);

            // update the shortest paths registry
            for (int end = 0; end < numMustVisitNodes; end++) {
                shortestPaths[start][end] = shortestPathData;
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

        // Find the permutation of must-visit nodes (always beginning with the start node) that
        // gives the shortest total path.
        findBestPermutationOfMustVisitNodes(roomIdsAndLocationIndices, 1);

        if (shortestPathLength == Integer.MAX_VALUE) {
            System.out.println("Unable to find a path to collect all needed items.");
        }
        else {
            // Piece together the optimal path and print it.
            ArrayList<String> optimalPath = reconstructShortestPath();
            printSolution(mapData, optimalPath, neededItems);
        }
    }

    /**
     * Determine the list of must-visit nodes - nodes that contain an item on our list of items to collect, or the starting node.
     */
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

    /**
     * Run through every permutation of the list of must-visit nodes (always beginning with the starting node)
     * and update the shortest path tracking variables if a new shortest path is found.
     */
    private void findBestPermutationOfMustVisitNodes(ArrayList<PairStringInteger> array, Integer index){
        if (Objects.equals(index, array.size())) {
            computePathLength(array);
            return;
        }
        for (int j = index; j < array.size(); j++) {
            Collections.swap(array, index, j);
            findBestPermutationOfMustVisitNodes(array, index + 1);
            Collections.swap(array, index, j);
        }
    }

    /**
     * Compute the path length for a given permutation.
     */
    private void computePathLength(ArrayList<PairStringInteger> roomIds) {
        Integer pathLength = 0;
        for (int i = 1; i < roomIds.size(); i++) {
            pathLength += shortestPaths[roomIds.get(i-1).getInteger()][roomIds.get(i).getInteger()].getDistance(roomIds.get(i).getStr());
        }
        if (pathLength < shortestPathLength) {
            shortestPathLength = pathLength;
            shortestPath = new ArrayList<>(roomIds);
        }
    }

    /**
     * Piece together the optimal path from path segments stored in the shortestPaths matrix.
     */
    private ArrayList<String> reconstructShortestPath() {
        Integer beginSectionIndex = shortestPath.size() - 2;
        Integer endSectionIndex = shortestPath.size() - 1;
        ArrayList<String> path = new ArrayList<>();
        while (beginSectionIndex >= 0) {
            String beginSectionNode = shortestPath.get(beginSectionIndex).getStr();
            String endSectionNode = shortestPath.get(endSectionIndex).getStr();

            // Rerunning this algorithm repeatedly is expensive, but the extra time cost outweighs
            // the huge memory cost of storing all the potential paths. Instead of trying to keep
            // this information in memory, we rebuild the paths between critical nodes.
            ShortestPathData shortestPathData = shortestPaths[shortestPath.get(beginSectionIndex).getInteger()][shortestPath.get(endSectionIndex).getInteger()];
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

    /**
     * Print the optimal path with directions and item updates.
     */
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

