package com.trafficAnalysis;

import java.util.ArrayList;
import java.util.List;

public class GridManager {

    public static final float WAITING_THRESHOLD_PRESSURE = 5f;
    public static final float FULL_SPEED_PRESSURE_RATE = -2f;
    public static final float SLOW_SPEED_PRESSURE_RATE = -1f;
    public static final float WAITING_PRESSURE_RATE = 1f;
    public static final float ANNOYED_PRESSURE_RATE = 2f;
    public static final float NO_CAR_PRESSURE_RATE = -3f;
    public static final float LOWEST_PRESSURE = 0.01f;

    QuantumGenerator quantumGenerator;
    UpdateManager updateManager;
    List<Intersection> intersections;
    List<Road> roads;

    GridManager(){
        setup(Double.MIN_VALUE);
    }

    GridManager(double chance){
        setup(chance);
    }

    void setup(double chance){
        if(chance == Double.MIN_VALUE){
            quantumGenerator = new QuantumGenerator();
        }
        else{
            quantumGenerator = new QuantumGenerator(chance);
        }
        updateManager = new UpdateManager(quantumGenerator);
        intersections = new ArrayList<>();
        roads = new ArrayList<>();
    }

    void createIntersection(Road[] roadsArr){
        Intersection intersection = new Intersection.IntersectionBuilder().in(roadsArr[0],roadsArr[1],roadsArr[2],roadsArr[3]).out(roadsArr[4],roadsArr[5],roadsArr[6],roadsArr[7]).build();
        intersections.add(intersection);
        updateManager.addIntersectionToMap(intersection.getUuid(),intersection);
    }

    void createRoad(int numNodes){
        Road road = new Road(numNodes);
        roads.add(road);
        updateManager.addRoadToMap(road.getUuid(), road);
        documentNodes(road.getNodes());
    }

    void documentNodes(com.trafficAnalysis.Node[] nodes){
        for (com.trafficAnalysis.Node node : nodes){
            updateManager.addNodeToMap(node.getUuid(), node);
        }
    }

    void onCreationComplete(){
        updateManager.setEntranceRoads();
    }

    void simulateStep(){
        updateManager.runStep();
    }

    void simulateSteps(long steps){
        for(long i = 0; i < steps; i++){
            updateManager.runStep();
        }
    }
}
