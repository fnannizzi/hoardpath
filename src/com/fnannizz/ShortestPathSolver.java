package com.fnannizz;

import java.util.HashMap;
import java.util.Objects;
import java.util.PriorityQueue;

/**
 * Created by francesca on 11/27/16.
 */
class ShortestPathData {
    private final HashMap<String, Integer> distanceFromStart;
    private final HashMap<String, String> previousNodesInShortestPath;

    ShortestPathData(HashMap<String, Integer> d, HashMap<String, String> p) {
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

class ShortestPathSolver {
    static ShortestPathData findShortestPathFromNode(String startNode, HashMap<String, Room> roomMap) {
        HashMap<String, Integer> distance = new HashMap<>();
        HashMap<String, String> previous = new HashMap<>();
        PriorityQueue<PairStringInteger> unvisitedSet = new PriorityQueue<>();
        distance.put(startNode, 0);

        for (String key : roomMap.keySet()) {
            if (!Objects.equals(key, startNode)) {
                distance.put(key, Integer.MAX_VALUE);
            }
            unvisitedSet.add(new PairStringInteger(key, distance.get(key)));
        }

        while (unvisitedSet.size() > 0) {
            String currentRoom = unvisitedSet.remove().getStr();
            HashMap<String, String> adjacentRooms = roomMap.get(currentRoom).getAdjacentRooms();
            for (String roomId : adjacentRooms.keySet()) {
                Integer alternateDistance = distance.get(currentRoom) + 1;
                if (alternateDistance < distance.get(roomId)) {
                    unvisitedSet.remove(new PairStringInteger(roomId, distance.get(roomId)));
                    distance.put(roomId, alternateDistance);
                    previous.put(roomId, currentRoom);
                    unvisitedSet.add(new PairStringInteger(roomId, alternateDistance));
                }
            }
        }
        return new ShortestPathData(distance, previous);
    }
}