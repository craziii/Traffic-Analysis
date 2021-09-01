package com.trafficAnalysis;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class UpdateManager {
    Map<UUID,Node> nodeMap;
    Map<UUID,Road> roadMap;
    Map<UUID,Intersection> intersectionMap;

    List<NodeMove> nodeMoveList;
    List<IntersectionMove> intersectionMoveList;

    public UpdateManager(){

    }

    void addNodeToDictionary(UUID uuid, Node node){
        nodeMap.put(uuid,node);
    }

    void addRoadToDictionary(UUID uuid, Road road){
        roadMap.put(uuid,road);
    }

    void addIntersectionToDictionary(UUID uuid, Intersection intersection){
        intersectionMap.put(uuid, intersection);
    }

    void updateCycle(){
        nodeMoveList.clear();
        intersectionMoveList.clear();
        ExecutorService threadPool = Executors.newCachedThreadPool();
        List<Future<NodeMove[]>> nodeTasks = new ArrayList<>();
        List<Future<IntersectionMove[]>> intersectionTasks = new ArrayList<>();
        for (Map.Entry<UUID,Road> pair:roadMap.entrySet()) {
            Future<NodeMove[]> futureNodeTask = threadPool.submit(() -> getRoads(pair.getValue()));
            nodeTasks.add(futureNodeTask);
        }
        for (Map.Entry<UUID,Intersection> pair:intersectionMap.entrySet()) {
            Future<IntersectionMove[]> futureIntersectionTask = threadPool.submit(() -> getIntersections(pair.getValue()));
            intersectionTasks.add(futureIntersectionTask);
        }
        boolean nodeTaskBoolean = false;
        boolean iterationTaskBoolean = false;
        while(!nodeTaskBoolean && !iterationTaskBoolean){

        }
    }

    void runCycle(){

    }

    NodeMove[] getRoads(Road road){
        return new NodeMove[1];
    }

    IntersectionMove[] getIntersections(Intersection intersection){
        return new IntersectionMove[1];
    }

    enum IntersectionMoveEnum {
        northToEast,
        northToSouth,
        northToWest,
        eastToSouth,
        eastToWest,
        eastToNorth,
        southToWest,
        southToNorth,
        southToEast,
        westToNorth,
        westToEast,
        westToSouth
    }

    enum NodeMoveEnum {
        noMove,
        move1,
        move2
    }

    public NodeMove buildNodeMove(UUID node, NodeMoveEnum move){
        return new NodeMove(nodeMap.get(node), move);
    }

    void addNodeMove(NodeMove nodeMove){
        nodeMoveList.add(nodeMove);
    }

    public IntersectionMove buildIntersectionMove(UUID intersection, IntersectionMoveEnum move){
        return new IntersectionMove(intersectionMap.get(intersection), move);
    }

    void buildIntersectionMove(IntersectionMove intersectionMove){
        intersectionMoveList.add(intersectionMove);
    }

    private static class NodeMove {
        Node node;
        NodeMoveEnum move;

        NodeMove(Node n, NodeMoveEnum m){
            node = n;
            move = m;
        }
    }

    private static class IntersectionMove{
        Intersection intersection;
        IntersectionMoveEnum move;

        IntersectionMove(Intersection i, IntersectionMoveEnum m){
            intersection = i;
            move = m;
        }
    }
}
