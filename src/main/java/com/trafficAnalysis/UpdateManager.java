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
    long carSpawnErrors;

    Duration cycleTime;
    Duration totalTime;

    QuantumGenerator quantumGenerator;

    double systemMaxPressure;
    long systemCycleMaxPressure;
    List<String> systemNodeLines;
    UUID systemRoadMaxPressure;
    UUID systemIntersectionMaxPressure;

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
        systemNodeLines = new ArrayList<>();
        cycleCounter = 0;
        cyclesToCount = 0;
        carSpawnErrors = 0;
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
            try{
                road.inIntersection.getUuid();
            }
            catch(Exception e){
                entryRoadMap.put(road.getUuid(),road);
            }
        }
    }

    void setExitRoads(){
        for(Road road: roadMap.values()){
            try{
                road.outIntersection.getUuid();
            }
            catch(Exception e){
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

    void updateCycle() {
        cycleCounter++;
        Instant cycleStart = Instant.now();
        clearMoveLists();
        //STEP 1 - Calculate Wanted Movement from Each Node on Each Road
        calculateNodeMovement();
        //STEP 2 - Move this info to Intersections to decide if some moves cannot be made
        testMovementsOnIntersections();
        //STEP 3 - Simulate Movement through intersections
        simulateMovementThroughIntersections();
        //STEP 4 - Move cars in nodes
        moveCarsInNodes();
        //STEP 5 - Add New Cars
        moveCarsIntoEntranceRoads();
        //STEP 6 - Update lights for next step
        updateIntersectionLights();
        //STEP 7 - Check for Errors
        dealWithCycleErrors();
        //STEP 8 - Write information to file
        logOutcomes();
        //STEP 9 - Calculate Metrics
        Instant cycleEnd = Instant.now();
        updateCycleDuration(cycleStart,cycleEnd);
    }

    void clearMoveLists(){
        verboseLog(0);
        nodeMoveList.clear();
        intersectionMoveList.clear();
        updateErrorsMap.clear();
    }

    void calculateNodeMovement(){
        verboseLog(1);
        ExecutorService threadPool = Executors.newCachedThreadPool();
        List<Future<NodeMove[]>> nodeTasks = new ArrayList<>();
        for (Map.Entry<UUID, Road> pair : roadMap.entrySet()) {
            Future<NodeMove[]> futureNodeTask = threadPool.submit(() -> getWantedMovement(pair.getValue()));
            nodeTasks.add(futureNodeTask);
        }
        boolean nodeTaskBoolean = false;
        while (!nodeTaskBoolean) {
            nodeTaskBoolean = checkNodeTasks(nodeTasks);
        }
        for (Future<NodeMove[]> task : nodeTasks) {
            try {
                nodeMoveList.addAll(Arrays.asList(task.get()));
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        threadPool.shutdown();
    }

    void testMovementsOnIntersections(){
        verboseLog(2);
        List<NodeMove> intersectionTest = new ArrayList<>();
        for (NodeMove nm : nodeMoveList) {
            if (nm.move == NodeMoveEnum.moveI1) {
                intersectionTest.add(nm);
            }
        }
        for (NodeMove nm : nodeMoveList) {
            if (nm.move == NodeMoveEnum.moveI2) {
                intersectionTest.add(nm);
            }
        }
        for (NodeMove nm : intersectionTest) {
            if (nm.node.parentRoad.outIntersection != null) {
                if (nm.node.parentRoad.outIntersection.hasSpaceForCars()) {
                    nodeMoveList.remove(nm);
                    nm.node.parentRoad.outIntersection.addCar(nm.node.parentRoad);
                    nm.node.carExitingNode();
                } else {
                    NodeMove temp = nm;
                    if (nm.move == NodeMoveEnum.moveI2) {
                        temp.move = NodeMoveEnum.move1;
                    } else {
                        temp.move = NodeMoveEnum.noMove;
                    }
                    nodeMoveList.set(nodeMoveList.indexOf(nm), temp);
                }
            } else {
                NodeMove temp = nm;
                temp.move = NodeMoveEnum.moveOff;
                nodeMoveList.set(nodeMoveList.indexOf(nm), temp);
            }

        }
    }

    void simulateMovementThroughIntersections() {
        verboseLog(3);
        for (Intersection intersection : intersectionMap.values()) {
            int count = 0;
            switch (intersection.getIntersectionType()) {
                case twoWay:
                    count = 2;
                    break;
                case threeWay:
                    count = 2;
                    break;
                case fourWay:
                    count = 2;
                    break;
            }
            for (int i = 0; i < count; i++) {
                if (!intersection.isEmpty()) {
                    IntersectionMove tempIntersectionMove = intersection.getNextIntersectionOutput(quantumGenerator);
                    if (tempIntersectionMove != null) {
                        intersectionMoveList.add(tempIntersectionMove);
                    }
                } else {
                    break;
                }
            }
        }
    }

    void moveCarsInNodes(){
        verboseLog(4);
        for (NodeMove nm : nodeMoveList) {
            switch (nm.move) {
                case move1:
                    nm.node.setStatus(Node.CarStatus.noCar);
                    nm.node.getNodeAfter().setStatus(Node.CarStatus.movingSlowly);
                    break;
                case move2:
                    nm.node.setStatus(Node.CarStatus.noCar);
                    nm.node.getNodeAfter().getNodeAfter().setStatus(Node.CarStatus.movingFullSpeed);
                    break;
                case moveOff:
                    nm.node.setStatus(Node.CarStatus.noCar);
                    break;
            }
        }
    }

    void moveCarsIntoEntranceRoads(){
        verboseLog(5);
        for (Road road : entryRoadMap.values()) {
            if (quantumGenerator.getNextBoolean(1)) {
                if (!road.getFirstNode().addCar()) {
                    carSpawnErrors++;
                }
            }
        }
    }

    void updateIntersectionLights(){
        verboseLog(6);
        for(Intersection intersection:intersectionMap.values()){
            intersection.updateGreenLights(false,Main.pressureBasedAssessment);
        }
    }

    void dealWithCycleErrors(){
        verboseLog(7);
        if(!updateErrorsMap.isEmpty()){
            printCycleErrors();
        }
        if(cycleCounter % 100 == 0){
            Util.Logging.log("Car Spawn Errors in the past 100 cycles ["+carSpawnErrors+"]", Util.Logging.LogLevel.ERROR);
            carSpawnErrors = 0;
        }
    }

    void logOutcomes(){
        verboseLog(8);
        if(Main.verboseLogging && Main.logToFile) {
            writePressureOutput();
            writeCarsOutput();
        }
        writeIntersectionOutput();
        checkPressure();
    }

    void updateCycleDuration(Instant start, Instant end){
        verboseLog(9);
        cycleTime = Duration.between(start,end);
        totalTime = totalTime.plus(cycleTime);
        if(cycleCounter % Main.updateRate == 0){
            printMetrics();
        }
    }

    void verboseLog(int step){
        if (Main.verboseLogging) {
            Util.Logging.log("update cycle, step " + step, Util.Logging.LogLevel.INFO);
        }
    }

    void writeCarsOutput(){
        if(cycleCounter == 1){
            Util.FileManager.writeFile("output/"+Util.FileManager.FOLDER_NAME+"/Cars.csv","",true);
        }
        Util.FileManager.writeFile("output/"+Util.FileManager.FOLDER_NAME+"/Cars.csv","CYCLE COUNT:"+cycleCounter,false);
        List<String> lines = new ArrayList<>();
        for(Road road:roadMap.values()){
            StringBuilder sb = new StringBuilder();
            Integer[] carLocations = road.getCars();
            if(carLocations.length == 0){
                continue;
            }
            sb.append("Road:["+road.getUuid()+"] has ["+carLocations.length+"] cars in it, cars are at locations[");
            for(Integer car:carLocations){
                sb.append(car+",");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append("]");
            lines.add(sb.toString());
        }
        Util.FileManager.writeFile("output/"+Util.FileManager.FOLDER_NAME+"/Cars.csv",lines.toArray(new String[0]),false);
    }

    void writePressureOutput(){
        if(cycleCounter == 1){
            Util.FileManager.writeFile("output/"+Util.FileManager.FOLDER_NAME+"/Pressure.csv","",true);
        }
        Util.FileManager.writeFile("output/"+Util.FileManager.FOLDER_NAME+"/Pressure.csv","CYCLE COUNT:"+cycleCounter,false);
        double maxPressure = -1;
        String maxPressureUUID = "";
        double totalPressure = 0;
        List<String> lines = new ArrayList<>();
        for(Road road: roadMap.values()){
            double tempPressure = road.getTotalPressure();
            totalPressure = totalPressure + tempPressure;
            if(tempPressure != 0) {
                lines.add("Road:[" + road.getUuid() + "] Pressure:[" + tempPressure + "]");
            }
            if(tempPressure > maxPressure){
                maxPressure = tempPressure;
                maxPressureUUID = road.getUuid().toString();
            }
        }
        lines.add("Max Pressure from Roads is from Road:["+maxPressureUUID+"] with a pressure of:["+maxPressure+"]");
        Util.FileManager.writeFile("output/" + Util.FileManager.FOLDER_NAME + "/Pressure.csv", lines.toArray(new String[0]), false);
    }

    void writeIntersectionOutput(){
        for(Intersection intersection:intersectionMap.values()) {
            if (cycleCounter == 1) {
                Util.FileManager.writeFile("output/" + Util.FileManager.FOLDER_NAME + "/Intersection-[" + intersection.mapLocation[0] + "," + intersection.mapLocation[1] + "]" + intersection.getUuid() + ".csv", "cycleNumber,lightCountdown,northPressure,eastPressure,southPressure,westPressure,northLight,eastLight,southLight,westLight", true);
            }
            List<String> parts = new ArrayList<>();
            parts.add("" + cycleCounter);
            parts.add("" + intersection.stepCountdown);
            for (int i = 0; i < 4; i++) {
                parts.add("" + intersection.getPressure(intToDirection(i)));
            }
            for (int i = 0; i < 4; i++) {
                if (intersection.greenLights[i]) {
                    parts.add("" + 1);
                } else {
                    parts.add("" + 0);
                }
            }
            StringBuilder sb = new StringBuilder();
            for (String part : parts) {
                sb.append(part).append(Util.FileManager.DEFAULT_DELIMITER);
            }
            sb.deleteCharAt(sb.length() - 1);
            Util.FileManager.writeFile("output/" + Util.FileManager.FOLDER_NAME + "/Intersection-[" + intersection.mapLocation[0] + "," + intersection.mapLocation[1] + "]" + intersection.getUuid() + ".csv", sb.toString(), false);
        }
    }

    void checkPressure(){
        for(Road r:roadMap.values()){
            if(r.getTotalPressure() > systemMaxPressure){
                systemMaxPressure = r.getTotalPressure();
                systemCycleMaxPressure = cycleCounter;
                systemRoadMaxPressure = r.getUuid();
                systemIntersectionMaxPressure = r.getOutIntersection().getUuid();
                List<String> lines = new ArrayList<>();
                for(Node node: r.nodesInRoad){
                    lines.add(r.nodesInRoad.indexOf(node)+","+node.getPressure());
                }
                systemNodeLines = lines;
            }
            /*
            if(r.getTotalPressure() > 100) {
                Util.FileManager.writeFile("output/" + Util.FileManager.FOLDER_NAME + "/outcome.log", "Success,cycleCount,maxPressure,road,intersection", true);
                Util.FileManager.writeFile("output/" + Util.FileManager.FOLDER_NAME + "/outcome.log", "0," + cycleCounter + "," + r.getTotalPressure() + "," + r.getUuid() + "," + r.outIntersection.getUuid(), false);
                Util.FileManager.writeFile("output/" + Util.FileManager.FOLDER_NAME + "/fail.log", "Node,NodePressure", true);
                Util.Logging.log("Road [" + r.getUuid() + "] connected to Intersection [" + r.outIntersection.getUuid() + "] has reached over 100 total pressure, pressure [" + r.getTotalPressure() + "], this occured on cycle [" + cycleCounter + "]", Util.Logging.LogLevel.ERROR);
                List<String> lines = new ArrayList<>();
                for (Node node : r.nodesInRoad) {
                    lines.add(r.nodesInRoad.indexOf(node) + "," + node.getPressure());
                }
                Util.FileManager.writeFile("output/" + Util.FileManager.FOLDER_NAME + "/fail.log", lines.toArray(new String[0]), false);
                System.exit(0);
            }
            */
        }
    }

    void printFinalInformation() {
        Util.Logging.log("Maximum Pressure Reached During Test [" + systemMaxPressure + "]", Util.Logging.LogLevel.INFO);
        Util.Logging.log("Maximum Pressure Reached During Cycle [" + systemCycleMaxPressure + "]", Util.Logging.LogLevel.INFO);
        Util.Logging.log("Maximum Pressure Reached On Road [" + systemRoadMaxPressure + "]", Util.Logging.LogLevel.INFO);
        Util.Logging.log("Maximum Pressure Reached On Intersection [" + systemIntersectionMaxPressure + "]", Util.Logging.LogLevel.INFO);
        Util.FileManager.writeFile("output/" + Util.FileManager.FOLDER_NAME + "/outcome.log", "Success,cycleCount,maxPressure,road,intersection", true);
        Util.FileManager.writeFile("output/" + Util.FileManager.FOLDER_NAME + "/outcome.log", "1," + systemCycleMaxPressure + "," + systemMaxPressure + "," + systemRoadMaxPressure + "," + systemIntersectionMaxPressure, false);
        Util.FileManager.writeFile("output/" + Util.FileManager.FOLDER_NAME + "/success.log", "Node,NodePressure", true);
        Util.FileManager.writeFile("output/" + Util.FileManager.FOLDER_NAME + "/success.log", systemNodeLines.toArray(new String[0]), false);
    }

    void printCycleErrors(){
        boolean criticalError = false;
        for(UUID uuid:updateErrorsMap.keySet()){
            switch(updateErrorsMap.get(uuid)){
                case carSpawnError: Util.Logging.log("Car failed to spawn on road ["+uuid+"], road was not empty", Util.Logging.LogLevel.ERROR);
            }
        }
        if(criticalError){
            Util.Logging.log("Critical error on cycle ["+cycleCounter+"]. exiting.",Util.Logging.LogLevel.CRITICAL);
            System.exit(0);
        }

    }

    void printMetrics() {
        Util.Logging.log("cycle [" + cycleCounter + "] time taken to calculate: [" + cycleTime.toMinutesPart() + "m" + cycleTime.toSecondsPart() + "." + Util.getMillis(cycleTime.toMillisPart()) + "s]", Util.Logging.LogLevel.INFO);
        Util.Logging.log("Total time to calculate [" + cycleCounter + "] cycles [" + totalTime.toHoursPart() + "h" + totalTime.toMinutesPart() + "m" + totalTime.toSecondsPart() + "." + Util.getMillis(totalTime.toMillisPart()) + "s], Time per cycle so far [" + totalTime.dividedBy(cycleCounter).toMinutesPart() + "m" + totalTime.dividedBy(cycleCounter).toSecondsPart() + "." + Util.getMillis(totalTime.dividedBy(cycleCounter).toMillisPart()) + "s]", Util.Logging.LogLevel.INFO);
        if (cyclesToCount > 0) {
            long cyclesLeft = cyclesToCount - cycleCounter;
            Duration timeLeft = totalTime.dividedBy(cycleCounter).multipliedBy(cyclesLeft);
            Util.Logging.log("Estimated Time to Completion with [" + cyclesLeft + "] cycles left, [" + timeLeft.toHoursPart() + "h" + timeLeft.toMinutesPart() + "m" + timeLeft.toSecondsPart() + "." + Util.getMillis(timeLeft.toMillisPart()) + "s]", Util.Logging.LogLevel.INFO);
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
