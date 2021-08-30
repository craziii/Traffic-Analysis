package com.trafficAnalysis;

import java.util.List;

public class GridManager {

    List<Intersection> intersections;
    List<Road> roads;

    GridManager(){

    }

    void createIntersection(Road[] roadsArr){
        Intersection intersection = new Intersection.IntersectionBuilder().inN(roadsArr[0].getLastNode()).inE(roadsArr[1].getLastNode()).inS(roadsArr[2].getLastNode()).inW(roadsArr[3].getLastNode()).outN(roadsArr[4].getFirstNode()).outE(roadsArr[5].getFirstNode()).outS(roadsArr[6].getFirstNode()).outW(roadsArr[7].getFirstNode()).build();
        intersections.add(intersection);
    }

    void buildRoad(int numNodes){
        Road road = new Road(numNodes);
        roads.add(road);
    }

    void simulateStep(){
        for(int i = 0; i < intersections.size(); i++){
            intersections.get(i).simulate();
        }
        for(int i = 0; i < roads.size(); i++){
            roads.get(i).simulate();
        }
    }

}
