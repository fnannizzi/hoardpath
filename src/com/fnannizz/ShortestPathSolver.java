package com.fnannizz;

import java.util.*;

/**
 * Created by francesca on 11/26/16.
 */
public class ShortestPathSolver {

    private RoomMap roomMapModel;
    private Integer shortestPathLength;
    private ArrayList<IndexedString> shortestPath;
    private Integer[][] shortestPaths;

    ShortestPathSolver(RoomMap map) {
        roomMapModel = map;
        shortestPathLength = Integer.MAX_VALUE;
    }


    public void findShortestPath(ArrayList<String> itemsToCollect, String startLocation) throws InvalidScenarioException {
        ArrayList<String> locationsOfNeededItems = roomMapModel.getLocationsOfNeededItems(itemsToCollect);
        HashMap<String, Room> roomMap = roomMapModel.getRoomMap();

        // The number of must-visit nodes is the number of nodes containing objects we need, plus the start node if
        // it isn't already in the list of must-visits
        if (!locationsOfNeededItems.contains(startLocation)) {
            locationsOfNeededItems.add(0, startLocation);
        }
        Integer numMustVisitNodes = locationsOfNeededItems.size();

        shortestPaths = new Integer[numMustVisitNodes][numMustVisitNodes];

        for (int start = 0; start < numMustVisitNodes; start++) {
            String startNode = locationsOfNeededItems.get(start);

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
                String currentRoom = unvisitedSet.remove().str;
                HashMap<String, String> adjacentRooms = roomMap.get(currentRoom).getAdjacentRooms();
                for (String room : adjacentRooms.keySet()) {
                    String roomId = adjacentRooms.get(room);
                    Integer alternateDistance = distance.get(currentRoom) + 1;
                    if (alternateDistance < distance.get(roomId)) {
                        unvisitedSet.remove(new PriorityString(roomId, distance.get(roomId)));
                        distance.put(roomId, alternateDistance);
                        previous.put(roomId, currentRoom);
                        unvisitedSet.add(new PriorityString(roomId, alternateDistance));
                    }
                }
            }

            // update the shortest paths registry
            for (int end = 0; end < numMustVisitNodes; end++) {
                String endNode = locationsOfNeededItems.get(end);
                shortestPaths[start][end] = distance.get(endNode);
            }
        }
        ArrayList<IndexedString> roomIdsAndIndices = new ArrayList<>();
        for (int i = 0; i < numMustVisitNodes; i++) {
            roomIdsAndIndices.add(new IndexedString(locationsOfNeededItems.get(i), i));
        }
        shortestPathLength = Integer.MAX_VALUE;
        computeAllPermutationsOfArray(roomIdsAndIndices, 1);

        printSolution();
    }


    void computePathLength(ArrayList<IndexedString> roomIds) {
        Integer pathLength = 0;
        for (int i = 1; i < roomIds.size(); i++) {
            pathLength += shortestPaths[roomIds.get(i-1).getIndex()][roomIds.get(i).getIndex()];
        }
        if (pathLength < shortestPathLength) {
            shortestPathLength = pathLength;
            shortestPath = new ArrayList<>(roomIds);
        }
    }

    void computeAllPermutationsOfArray(ArrayList<IndexedString> array, Integer index){
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

    private void printSolution() {
        System.out.println(shortestPathLength);
        System.out.println("SHORTEST PATH");
        for (IndexedString str : shortestPath) {
            System.out.println(str.getStr());
        }
    }

//    void computeShortestPathHelper(ArrayList<IndexedString> roomIds, Integer[][] shortestPaths, Integer numRooms, Integer index) {
//        if (Objects.equals(index, numRooms)) {
//            computePathLength(roomIds, shortestPaths, numRooms);
//            return;
//        }
//        for (int i = index; i < numRooms; i++) {
//            Collections.swap(roomIds, i, index);
//            computeShortestPathHelper(roomIds, shortestPaths, numRooms, i+1);
//            Collections.swap(roomIds, i, index);
//        }
//    }

}

// We need to reorder
class IndexedString {
    private String str;
    private Integer index;

    IndexedString(String s, Integer i) {
        str = s;
        index = i;
    }

    public String getStr() {
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

    @Override public int compareTo(PriorityString other) {
        return Integer.compare(this.priority, other.priority);
    }
}