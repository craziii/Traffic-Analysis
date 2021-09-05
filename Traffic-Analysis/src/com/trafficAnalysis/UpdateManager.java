package com.trafficAnalysis;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class UpdateManager {
    Map<UUID, com.trafficAnalysis.Node> nodeMap;
    Map<UUID, com.trafficAnalysis.Road> roadMap;
    Map<UUID, com.trafficAnalysis.Intersection> intersectionMap;

    long cycleCounter;

    List<NodeMove> nodeMoveList;
    List<IntersectionMove> intersectionMoveList;

    public UpdateManager(){
        nodeMoveList = new ArrayList<>();
        intersectionMoveList = new ArrayList<>();
        cycleCounter = 0;
    }

    void addNodeToDictionary(UUID uuid, com.trafficAnalysis.Node node){
        nodeMap.put(uuid,node);
    }

    void addRoadToDictionary(UUID uuid, com.trafficAnalysis.Road road){
        roadMap.put(uuid,road);
    }

    void addIntersectionToDictionary(UUID uuid, com.trafficAnalysis.Intersection intersection){
        intersectionMap.put(uuid, intersection);
    }

    void updateCycle(){
        cycleError error = cycleError.noError;
        nodeMoveList.clear();
        intersectionMoveList.clear();
        ExecutorService threadPool = Executors.newCachedThreadPool();
        List<Future<NodeMove[]>> nodeTasks = new ArrayList<>();
        List<Future<IntersectionMove[]>> intersectionTasks = new ArrayList<>();
        for (Map.Entry<UUID, com.trafficAnalysis.Road> pair:roadMap.entrySet()) {
            Future<NodeMove[]> futureNodeTask = threadPool.submit(() -> getRoads(pair.getValue()));
            nodeTasks.add(futureNodeTask);
        }
        for (Map.Entry<UUID, com.trafficAnalysis.Intersection> pair:intersectionMap.entrySet()) {
            Future<IntersectionMove[]> futureIntersectionTask = threadPool.submit(() -> getIntersections(pair.getValue()));
            intersectionTasks.add(futureIntersectionTask);
        }
        boolean nodeTaskBoolean = false;
        boolean intersectionTaskBoolean = false;
        while(!nodeTaskBoolean && !intersectionTaskBoolean){
            if(!nodeTaskBoolean){
                nodeTaskBoolean = checkNodeTasks(nodeTasks);
            }
            if(!intersectionTaskBoolean){
                intersectionTaskBoolean = checkIntersectionTasks(intersectionTasks);
            }
        }
        for (Future<NodeMove[]> task: nodeTasks) {
            try {
                nodeMoveList.addAll(Arrays.asList(task.get()));
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                error = cycleError.nodeFutureError;
            }
        }
        for (Future<IntersectionMove[]> task: intersectionTasks) {
            try {
                intersectionMoveList.addAll(Arrays.asList(task.get()));
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                error = cycleError.intersectionFutureError;
            }
        }
        if(error != cycleError.noError){
            System.out.println("Cycle error at cycle: " + cycleCounter + ". Error caused by: " + error.name());
        }
        else{
            runCycle();
        }
    }

    enum cycleError{
        noError,
        nodeFutureError,
        intersectionFutureError
    }

    boolean checkNodeTasks(List<Future<NodeMove[]>> tasks){
        for (Future<NodeMove[]> task: tasks) {
            if(!task.isDone()){
                return false;
            }
        }
        return true;
    }

    boolean checkIntersectionTasks(List<Future<IntersectionMove[]>> tasks){
        for (Future<IntersectionMove[]> task: tasks) {
            if(!task.isDone()){
                return false;
            }
        }
        return true;
    }

    void runCycle(){

        cycleCounter++;
    }

    NodeMove[] getRoads(com.trafficAnalysis.Road road){
        return new NodeMove[1];
    }

    IntersectionMove[] getIntersections(com.trafficAnalysis.Intersection intersection){
        return intersection.simulate();
    }

    enum IntersectionMoveEnum {
        none,
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

    public static class NodeMove {
        com.trafficAnalysis.Node node;
        NodeMoveEnum move;

        NodeMove(com.trafficAnalysis.Node n, NodeMoveEnum m){
            node = n;
            move = m;
        }
    }

    public static class IntersectionMove{
        com.trafficAnalysis.Intersection intersection;
        IntersectionMoveEnum move;

        IntersectionMove(com.trafficAnalysis.Intersection i, IntersectionMoveEnum m){
            intersection = i;
            move = m;
        }
    }
}