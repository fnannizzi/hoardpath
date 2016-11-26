package com.fnannizz;

// TODO: Fix this
import java.util.*;

/**
 * Created by francesca on 11/26/16.
 */
class DijkstraData {
    private final HashMap<String, Integer> distanceFromStart;
    private final HashMap<String, String> previousNodesInShortestPath;

    DijkstraData(HashMap<String, Integer> d, HashMap<String, String> p) {
        distanceFromStart = d;
        previousNodesInShortestPath = p;
    }

    Integer getDistance(String node) {
        return distanceFromStart.get(node);
    }

    String getPrevious(String node) {
        return previousNodesInShortestPath.get(node);
    }

}

class Dijkstra {
    static DijkstraData runDijkstra(String startNode, HashMap<String, Room> roomMap) {
        HashMap<String, Integer> distance = new HashMap<>();
        HashMap<String, String> previous = new HashMap<>();
        PriorityQueue<PriorityString> unvisitedSet = new PriorityQueue<>();
        distance.put(startNode, 0);

        for (String key : roomMap.keySet()) {
            if (!Objects.equals(key, startNode)) {
                distance.put(key, Integer.MAX_VALUE);
            }
            unvisitedSet.add(new PriorityString(key, distance.get(key)));
        }

        while (unvisitedSet.size() > 0) {
            String currentRoom = unvisitedSet.remove().getStr();
            HashMap<String, String> adjacentRooms = roomMap.get(currentRoom).getAdjacentRooms();
            for (String roomId : adjacentRooms.keySet()) {
                Integer alternateDistance = distance.get(currentRoom) + 1;
                if (alternateDistance < distance.get(roomId)) {
                    unvisitedSet.remove(new PriorityString(roomId, distance.get(roomId)));
                    distance.put(roomId, alternateDistance);
                    previous.put(roomId, currentRoom);
                    unvisitedSet.add(new PriorityString(roomId, alternateDistance));
                }
            }
        }
        return new DijkstraData(distance, previous);
    }
}

public class ShortestPathSolver {

    private RoomGraph roomGraph;
    private HashMap<String, Room> roomMap;
    private ArrayList<String> neededItems;

    private Integer shortestPathLength;
    private ArrayList<IndexedString> shortestPath;
    private Integer[][] shortestPaths;

    ShortestPathSolver(RoomGraph graph) {
        roomGraph = graph;
        roomMap = roomGraph.getRoomMap();
        shortestPathLength = Integer.MAX_VALUE;
    }

    ArrayList<String> getNeededNodes(String startingLocation) throws InvalidScenarioException {
        ArrayList<String> locationsOfNeededItems = roomGraph.getLocationsOfNeededItems(neededItems);


        // The number of must-visit nodes is the number of nodes containing objects we need, plus the starting node if
        // it isn't already in the list of must-visits. The starting node must be the first node in the list, so
        // remove it from it's original location if needed.
        if (locationsOfNeededItems.contains(startingLocation)) {
            locationsOfNeededItems.remove(startingLocation);
        }
        locationsOfNeededItems.add(0, startingLocation);

        return locationsOfNeededItems;
    }

    void findShortestPath(ArrayList<String> itemsToCollect, String startingLocation) throws InvalidScenarioException {
        neededItems = itemsToCollect;

        ArrayList<String> locationsOfNeededItems = getNeededNodes(startingLocation);
        Integer numMustVisitNodes = locationsOfNeededItems.size();
        shortestPaths = new Integer[numMustVisitNodes][numMustVisitNodes];

        for (int start = 0; start < numMustVisitNodes; start++) {
            String currentStartNode = locationsOfNeededItems.get(start);

            DijkstraData dijkstraData = Dijkstra.runDijkstra(currentStartNode, roomMap);

            // update the shortest paths registry
            for (int end = 0; end < numMustVisitNodes; end++) {
                String currentEndNode = locationsOfNeededItems.get(end);
                shortestPaths[start][end] = dijkstraData.getDistance(currentEndNode);
            }
        }

        // We need to be able to reorder this list, while still maintaining knowledge of the original index
        // of the room id in locationsOfNeededItems. This is how we access the matrix of shortest paths.
        // Because Collections.swap requires a List type, we use a list of room id-index pairs rather than something
        // like a hash map.
        ArrayList<IndexedString> roomIdsAndLocationIndices = new ArrayList<>();
        for (int i = 0; i < numMustVisitNodes; i++) {
            roomIdsAndLocationIndices.add(new IndexedString(locationsOfNeededItems.get(i), i));
        }
        shortestPathLength = Integer.MAX_VALUE;
        computeAllPermutationsOfArray(roomIdsAndLocationIndices, 1);

        if (shortestPathLength == Integer.MAX_VALUE) {
            System.out.println("Unable to find a path to collect all needed items.");
        }
        else {
            ArrayList<String> optimalPath = computeBestPath();
            printSolution(optimalPath);
        }
    }


    private void computePathLength(ArrayList<IndexedString> roomIds) {
        Integer pathLength = 0;
        for (int i = 1; i < roomIds.size(); i++) {
            pathLength += shortestPaths[roomIds.get(i-1).getIndex()][roomIds.get(i).getIndex()];
        }
        if (pathLength < shortestPathLength) {
            shortestPathLength = pathLength;
            shortestPath = new ArrayList<>(roomIds);
        }
    }

    private void computeAllPermutationsOfArray(ArrayList<IndexedString> array, Integer index){
        if (Objects.equals(index, array.size())) {
            computePathLength(array);
//            System.out.println(array);
            return;
        }
        for (int j = index; j < array.size(); j++) {
            Collections.swap(array, index, j);
            computeAllPermutationsOfArray(array, index + 1);
            Collections.swap(array, index, j);
        }
    }

    private ArrayList<String> computeBestPath() {
        Integer beginSectionIndex = shortestPath.size() - 2;
        Integer endSectionIndex = shortestPath.size() - 1;
        ArrayList<String> path = new ArrayList<>();
        while (beginSectionIndex >= 0) {
            String beginSectionNode = shortestPath.get(beginSectionIndex).getStr();
            String endSectionNode = shortestPath.get(endSectionIndex).getStr();

            // Rerunning this algorithm repeatedly is expensive, but the extra time cost outweighs
            // the huge memory cost of storing all the potential paths. Instead of trying to keep
            // this information in memory, we rebuild the paths between critical nodes.
            DijkstraData dijkstraData = Dijkstra.runDijkstra(beginSectionNode, roomMap);
            while (!Objects.equals(endSectionNode, beginSectionNode)) {
                path.add(endSectionNode);
                endSectionNode = dijkstraData.getPrevious(endSectionNode);

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

    private void printSolution(ArrayList<String> path) {
        System.out.println("Found an optimal path of length " + shortestPathLength + ".");
        System.out.println("-------------------------------------------------");

        Integer nextRoomIndex = 1;
        for (String roomId : path) {
            System.out.println("Entering " + roomId + ".");
            Room currentRoom = roomMap.get(roomId);
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

// TODO: rename this stuff
// We need to reorder
class IndexedString {
    private String str;
    private Integer index;

    IndexedString(String s, Integer i) {
        str = s;
        index = i;
    }

    String getStr() {
        return str;
    }

    Integer getIndex() {
        return index;
    }
}

class PriorityString implements Comparable<PriorityString> {
    String str;
    Integer priority;

    PriorityString(String s, Integer p) {
        str = s;
        priority = p;
    }

    String getStr() {
        return str;
    }

    @Override public int compareTo(PriorityString other) {
        return Integer.compare(this.priority, other.priority);
    }
}