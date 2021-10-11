package com.trafficAnalysis;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class UpdateManager {
    Map<UUID, Node> nodeMap;
    Map<UUID, Road> roadMap;
    Map<UUID, Road> entryRoadMap;
    Map<UUID, Road> exitRoadMap;
    Map<UUID, Intersection> intersectionMap;
    Map<UUID, UpdateErrors> updateErrorsMap;

    long cycleCounter;
    long cyclesToCount;

    Duration cycleTime;
    Duration totalTime;

    QuantumGenerator quantumGenerator;

    List<NodeMove> nodeMoveList;
    List<IntersectionMove> intersectionMoveList;

    public UpdateManager(QuantumGenerator qg){
        nodeMap = new HashMap<>();
        roadMap = new HashMap<>();
        intersectionMap = new HashMap<>();
        nodeMoveList = new ArrayList<>();
        intersectionMoveList = new ArrayList<>();
        updateErrorsMap = new HashMap<>();
        entryRoadMap = new HashMap<>();
        exitRoadMap = new HashMap<>();
        cycleCounter = 0;
        cyclesToCount = 0;
        quantumGenerator = qg;
        totalTime = Duration.ZERO;
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

    void setEntranceRoads(){
        for(Road road:roadMap.values()){
            if(road.inIntersection == null){
                entryRoadMap.put(road.getUuid(),road);
            }
        }
    }

    void setExitRoads(){
        for(Road road: roadMap.values()){
            if(road.outIntersection == null){
                exitRoadMap.put(road.getUuid(),road);
            }
        }
    }

    public void runStep(){
        updateCycle();
    }

    public void runSteps(long cycles){
        cyclesToCount = cycles;
        for(long i = 0; i < cyclesToCount; i++){
            updateCycle();
        }
    }

    void updateCycle(){
        cycleCounter++;
        Instant cycleStart = Instant.now();
        nodeMoveList.clear();
        intersectionMoveList.clear();
        updateErrorsMap.clear();
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
        List<NodeMove> intersectionTest = new ArrayList<>();
        for (NodeMove nm:nodeMoveList) {
            if(nm.move == NodeMoveEnum.moveI1){
                intersectionTest.add(nm);
            }
        }
        for (NodeMove nm:nodeMoveList) {
            if(nm.move == NodeMoveEnum.moveI2){
                intersectionTest.add(nm);
            }
        }
        for (NodeMove nm:intersectionTest) {
            if(nm.node.parentRoad.outIntersection != null) {
                if (nm.node.parentRoad.outIntersection.hasSpaceForCars()) {
                    nodeMoveList.remove(nm);
                    nm.node.parentRoad.outIntersection.addCar(nm.node.parentRoad);
                } else {
                    NodeMove temp = nm;
                    if (nm.move == NodeMoveEnum.moveI2) {
                        temp.move = NodeMoveEnum.move1;
                    } else {
                        temp.move = NodeMoveEnum.noMove;
                    }
                    nodeMoveList.set(nodeMoveList.indexOf(nm), temp);
                }
            }
            else{
                NodeMove temp = nm;
                temp.move = NodeMoveEnum.moveOff;
                nodeMoveList.set(nodeMoveList.indexOf(nm), temp);
            }

        }
        //TODO:STEP 3 - Simulate Movement through intersections
        for (Intersection intersection:intersectionMap.values()) {
            int count = 0;
            switch (intersection.getIntersectionType()){
                case twoWay: count = 2; break;
                case threeWay: count = 3; break;
                case fourWay: count = 4; break;
            }
            for(int i = 0; i < count; i++){
                if(!intersection.isEmpty()){
                    IntersectionMove tempIntersectionMove = intersection.getNextIntersectionOutput(quantumGenerator);
                    if(tempIntersectionMove != null){
                        intersectionMoveList.add(tempIntersectionMove);
                    }
                }
                else{
                    break;
                }
            }
        }
        //TODO:STEP 4 - Pass movement information back to nodes
        //Done inside simulations
        //TODO:STEP 5 - Move cars in nodes
        for (NodeMove nm:nodeMoveList) {
            switch(nm.move){
                case move1: nm.node.setStatus(Node.CarStatus.noCar); nm.node.getNodeAfter().setStatus(Node.CarStatus.movingSlowly); break;
                case move2: nm.node.setStatus(Node.CarStatus.noCar); nm.node.getNodeAfter().getNodeAfter().setStatus(Node.CarStatus.movingFullSpeed); break;
                case moveOff: nm.node.setStatus(Node.CarStatus.noCar); break;
            }
        }
        //TODO:STEP 6 - Add New Cars
        for(Road road:entryRoadMap.values()){
            if(quantumGenerator.getNextBoolean()){
                if(!road.getFirstNode().addCar()){
                    updateErrorsMap.put(road.getUuid(),UpdateErrors.carSpawnError);
                }
            }
        }
        //TODO:STEP 7 - Update lights for next step
        for(Intersection intersection:intersectionMap.values()){
            intersection.updateGreenLights(false);
        }
        //TODO:STEP 8 - Check for Errors
        if(!updateErrorsMap.isEmpty()){
            printCycleErrors();
        }
        //TODO:STEP 9 - Increment cycle counter
        Instant cycleEnd = Instant.now();
        cycleTime = Duration.between(cycleStart,cycleEnd);
        totalTime = totalTime.plus(cycleTime);
        printMetrics();
    }

    void printCycleErrors(){
        boolean criticalError = false;
        for(UUID uuid:updateErrorsMap.keySet()){
            switch(updateErrorsMap.get(uuid)){
                case carSpawnError: Util.Logging.log("Car failed to spawn on road ["+uuid+"], road was not empty", Util.Logging.LogLevel.ERROR);
                default: Util.Logging.log("Unknown Error occurred at UUID ["+uuid+"]", Util.Logging.LogLevel.ERROR);
            }
        }
        if(criticalError){
            Util.Logging.log("Critical error on cycle ["+cycleCounter+"]. exiting.",Util.Logging.LogLevel.CRITICAL);
            System.exit(0);
        }

    }

    void printMetrics() {
        Util.Logging.log("cycle [" + cycleCounter + "] time taken to calculate: [" + cycleTime.toMinutesPart() + "m" + cycleTime.toSecondsPart() + "." + cycleTime.toMillisPart() + "s]", Util.Logging.LogLevel.INFO);
        Util.Logging.log("Total time to calculate [" + cycleCounter + "] cycles [" + totalTime.toHoursPart() + "h" + totalTime.toMinutesPart() + "m" + totalTime.toSecondsPart() + "." + totalTime.toMillisPart() + "s], Time per cycle so far [" + totalTime.dividedBy(cycleCounter).toMinutesPart() + "m" + totalTime.dividedBy(cycleCounter).toSecondsPart() + "." + totalTime.dividedBy(cycleCounter).toMillisPart() + "s]", Util.Logging.LogLevel.INFO);
        if (cyclesToCount > 0) {
            long cyclesLeft = cyclesToCount - cycleCounter;
            Duration timeLeft = totalTime.dividedBy(cycleCounter).multipliedBy(cyclesLeft);
            Util.Logging.log("Estimated Time to Completion with [" + cyclesLeft + "] cycles left, [" + timeLeft.toHoursPart() + "h" + timeLeft.toMinutesPart() + "m" + timeLeft.toSecondsPart() + "." + timeLeft.toMillisPart() + "s]", Util.Logging.LogLevel.INFO);
        }
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

    public static int directionToInt(Direction dir){
        return dir.ordinal();
    }

    public static Direction intToDirection(int i){
        switch (i){
            case 0: return Direction.north;
            case 1: return Direction.east;
            case 2: return Direction.south;
            case 3: return Direction.west;
            default: return Direction.none;
        }
    }

    public enum Direction {
        north,
        east,
        south,
        west,
        none
    }

    enum NodeMoveEnum {
        noMove,
        move1,
        move2,
        moveI1,
        moveI2,
        moveOff,
        noCar
    }

    enum UpdateErrors{
        carSpawnError
    }

    public NodeMove buildNodeMove(UUID node, NodeMoveEnum move){
        return new NodeMove(nodeMap.get(node), move);
    }

    void addNodeMove(NodeMove nodeMove){
        nodeMoveList.add(nodeMove);
    }

    public IntersectionMove buildIntersectionMove(UUID intersection, Direction in, Direction out){
        return new IntersectionMove(intersectionMap.get(intersection), in, out);
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
        Direction in;
        Direction out;

        IntersectionMove(Intersection i, Direction _in, Direction _out){
            intersection = i;
            in = _in;
            out = _out;
        }
    }
}
