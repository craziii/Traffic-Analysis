package com.trafficAnalysis;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GridManager {

    public static final float WAITING_THRESHOLD_PRESSURE = 5f;
    public static final float FULL_SPEED_PRESSURE_RATE = -0.8f;
    public static final float SLOW_SPEED_PRESSURE_RATE = -0.4f;
    public static final float WAITING_PRESSURE_RATE = 1f;
    public static final float ANNOYED_PRESSURE_RATE = 2f;
    public static final float NO_CAR_PRESSURE_RATE = -1f;
    public static final float LOWEST_PRESSURE = 0.01f;

    GridBuilder gridBuilder;
    QuantumGenerator quantumGenerator;
    UpdateManager updateManager;

    GridManager(){
        quantumGenerator = new QuantumGenerator();
        updateManager = new UpdateManager(quantumGenerator);
        gridBuilder = new GridBuilder(this,GridBuilder.DEFAULT_MAPFILE);
    }

    GridManager(double intersectionChance, double carChance){
        if(intersectionChance == 0 && carChance == 0){
            quantumGenerator = new QuantumGenerator();
        }
        else{
            quantumGenerator = new QuantumGenerator(intersectionChance,carChance);
        }
        updateManager = new UpdateManager(quantumGenerator);
        gridBuilder = new GridBuilder(this,GridBuilder.DEFAULT_MAPFILE);
    }

    GridManager(double intersectionChance,double carChance, String filename){
        if(intersectionChance == 0 && carChance == 0){
            quantumGenerator = new QuantumGenerator();
        }
        else{
            quantumGenerator = new QuantumGenerator(intersectionChance,carChance);
        }
        updateManager = new UpdateManager(quantumGenerator);
        gridBuilder = new GridBuilder(this,filename);
    }

    public void createWorld(){
        Instant start = Instant.now();
        gridBuilder.fileToIntersectionMapping();
        gridBuilder.intersectionMappingToWorldMap();
        onCreationComplete();
        Instant end = Instant.now();
        Duration worldCreationDuration = Duration.between(start,end);
        Util.Logging.log("World Map Complete, time taken to create [" + worldCreationDuration.toMinutesPart() + "m" + worldCreationDuration.toSecondsPart() + "." + worldCreationDuration.toMillisPart() + "s]", Util.Logging.LogLevel.INFO);
    }

    public Intersection createIntersection(Road[] roadsArr, int[] location){
        Intersection intersection = new Intersection.IntersectionBuilder(quantumGenerator).in(roadsArr[0],roadsArr[1],roadsArr[2],roadsArr[3]).out(roadsArr[4],roadsArr[5],roadsArr[6],roadsArr[7]).mapLocation(location).build();
        updateManager.addIntersectionToMap(intersection.getUuid(),intersection);
        return intersection;
    }

    public Road getRoad(UUID uuid){
        return updateManager.roadMap.get(uuid);
    }

    public Road createRoad(int numNodes){
        Road road = new Road(numNodes);
        updateManager.addRoadToMap(road.getUuid(), road);
        documentNodes(road.getNodes());
        return road;
    }

    void documentNodes(com.trafficAnalysis.Node[] nodes){
        for (com.trafficAnalysis.Node node : nodes){
            updateManager.addNodeToMap(node.getUuid(), node);
        }
    }

    public void onCreationComplete(){
        updateManager.setEntranceRoads();
        updateManager.setExitRoads();
    }

    void simulateStep(){
        updateManager.runStep();
    }

    void simulateSteps(long steps){
        updateManager.runSteps(steps);
        updateManager.printFinalInformation();
    }
}
