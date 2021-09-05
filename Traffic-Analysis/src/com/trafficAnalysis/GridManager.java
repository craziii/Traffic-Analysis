package com.trafficAnalysis;

import java.util.ArrayList;
import java.util.List;

public class GridManager {

    UpdateManager updateManager;
    List<Intersection> intersections;
    List<Road> roads;

    GridManager(){
        updateManager = new UpdateManager();
        intersections = new ArrayList<>();
        roads = new ArrayList<>();
    }

    void createIntersection(Road[] roadsArr){
        Intersection intersection = new Intersection.IntersectionBuilder().inN(roadsArr[0]).inE(roadsArr[1]).inS(roadsArr[2]).inW(roadsArr[3]).outN(roadsArr[4]).outE(roadsArr[5]).outS(roadsArr[6]).outW(roadsArr[7]).build();
        intersections.add(intersection);
        updateManager.addIntersectionToDictionary(intersection.getUuid(),intersection);
    }

    void createRoad(int numNodes){
        Road road = new Road(numNodes);
        roads.add(road);
        updateManager.addRoadToDictionary(road.getUuid(), road);
        documentNodes(road.getNodes());
    }

    void documentNodes(com.trafficAnalysis.Node[] nodes){
        for (com.trafficAnalysis.Node node : nodes){
            updateManager.addNodeToDictionary(node.getUuid(), node);
        }
    }

    void simulateStep(){
        for (Intersection intersection : intersections) {
            intersection.simulate();
        }
        for (Road road : roads) {
            road.simulate();
        }
    }
}
