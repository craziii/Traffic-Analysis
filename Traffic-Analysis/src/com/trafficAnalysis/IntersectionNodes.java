package com.traffic;

public class IntersectionNodes{
    com.trafficAnalysis.Node north;
    com.trafficAnalysis.Node east;
    com.trafficAnalysis.Node south;
    com.trafficAnalysis.Node west;

    public IntersectionNodes(com.trafficAnalysis.Node n, com.trafficAnalysis.Node e, com.trafficAnalysis.Node s, com.trafficAnalysis.Node w){
        north = n;
        east = e;
        south = s;
        west = w;
    }

    public IntersectionNodes(){

    }

    com.trafficAnalysis.Node[] toArray(){
        com.trafficAnalysis.Node[] arr = new com.trafficAnalysis.Node[4];
        arr[0] = north;
        arr[1] = east;
        arr[2] = south;
        arr[3] = west;
        return arr;
    }
}
