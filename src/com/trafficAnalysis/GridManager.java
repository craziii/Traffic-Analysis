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
        Intersection intersection = new Intersection.IntersectionBuilder().inN(roadsArr[0].getLastNode()).inE(roadsArr[1].getLastNode()).inS(roadsArr[2].getLastNode()).inW(roadsArr[3].getLastNode()).outN(roadsArr[4].getFirstNode()).outE(roadsArr[5].getFirstNode()).outS(roadsArr[6].getFirstNode()).outW(roadsArr[7].getFirstNode()).build();
        intersections.add(intersection);
        updateManager.addIntersectionToDictionary(intersection.getUuid(),intersection);
    }

    void createRoad(int numNodes){
        Road road = new Road(numNodes);
        roads.add(road);
        updateManager.addRoadToDictionary(road.getUuid(), road);
        documentNodes(road.getNodes());
    }

    void documentNodes(Node[] nodes){
        for (Node node : nodes){
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
