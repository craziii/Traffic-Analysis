package com.trafficAnalysis;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class UpdateManager {
    Map<UUID, Node> nodeMap;
    Map<UUID, Road> roadMap;
    Map<UUID, Intersection> intersectionMap;

    long cycleCounter;

    List<NodeMove> nodeMoveList;
    List<IntersectionMove> intersectionMoveList;

    public UpdateManager(){
        nodeMoveList = new ArrayList<>();
        intersectionMoveList = new ArrayList<>();
        cycleCounter = 0;
    }

    void addNodeToMap(UUID uuid, Node node){
        nodeMap.put(uuid,node);
    }

    void addRoadToMap(UUID uuid, Road road){
        roadMap.put(uuid,road);
    }

    void addIntersectionToMap(UUID uuid, Intersection intersection){
        intersectionMap.put(uuid, intersection);
    }

    //<editor-fold desc="old code">

    /*

    void updateCycle(){
        cycleError error = cycleError.noError;
        nodeMoveList.clear();
        intersectionMoveList.clear();
        ExecutorService threadPool = Executors.newCachedThreadPool();
        List<Future<NodeMove[]>> nodeTasks = new ArrayList<>();
        List<Future<IntersectionMove[]>> intersectionTasks = new ArrayList<>();
        for (Map.Entry<UUID, Road> pair:roadMap.entrySet()) {
            Future<NodeMove[]> futureNodeTask = threadPool.submit(() -> getRoads(pair.getValue()));
            nodeTasks.add(futureNodeTask);
        }
        for (Map.Entry<UUID, Intersection> pair:intersectionMap.entrySet()) {
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

    NodeMove[] getRoads(Road road){
        return road.simulate();
    }

    IntersectionMove[] getIntersections(Intersection intersection){
        return intersection.simulate();
    }

     */

    //</editor-fold>

    //<editor-fold desc="new code">

    void updateCycle(){
        nodeMoveList.clear();
        intersectionMoveList.clear();
        //TODO:STEP 1 - Calculate Wanted Movement from Each Node on Each Road
        ExecutorService threadPool = Executors.newCachedThreadPool();
        List<Future<NodeMove[]>> nodeTasks = new ArrayList<>();
        for (Map.Entry<UUID, Road> pair:roadMap.entrySet()) {
            Future<NodeMove[]> futureNodeTask = threadPool.submit(() -> getWantedMovement(pair.getValue()));
            nodeTasks.add(futureNodeTask);
        }
        boolean nodeTaskBoolean = false;
        while(!nodeTaskBoolean) {
            nodeTaskBoolean = checkNodeTasks(nodeTasks);
        }
        for (Future<NodeMove[]> task: nodeTasks) {
            try {
                nodeMoveList.addAll(Arrays.asList(task.get()));
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        //TODO:STEP 2 - Move this info to Intersections to decide if some moves cannot be made

        //TODO:STEP 3 - Simulate Movement through intersections

        //TODO:STEP 4 - Pass movement information back to nodes

        //TODO:STEP 5 - Move cars in nodes

        //TODO:STEP 6 - Update lights for next step

    }

    NodeMove[] getWantedMovement(Road road){
        return road.getWantedMovement();
    }

    boolean checkNodeTasks(List<Future<NodeMove[]>> tasks){
        for (Future<NodeMove[]> task: tasks) {
            if(!task.isDone()){
                return false;
            }
        }
        return true;
    }

    //</editor-fold>

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
        move2,
        noCar
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

    void addIntersectionMove(IntersectionMove intersectionMove){
        intersectionMoveList.add(intersectionMove);
    }

    public static class NodeMove {
        Node node;
        NodeMoveEnum move;

        NodeMove(Node n, NodeMoveEnum m){
            node = n;
            move = m;
        }
    }

    public static class IntersectionMove{
        Intersection intersection;
        IntersectionMoveEnum move;

        IntersectionMove(Intersection i, IntersectionMoveEnum m){
            intersection = i;
            move = m;
        }
    }
}
