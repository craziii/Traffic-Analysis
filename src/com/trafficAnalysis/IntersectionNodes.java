package com.trafficAnalysis;

public class IntersectionNodes{
    Node north;
    Node east;
    Node south;
    Node west;

    public IntersectionNodes(Node n, Node e, Node s, Node w){
        north = n;
        east = e;
        south = s;
        west = w;
    }

    public IntersectionNodes(){

    }

    Node[] toArray(){
        Node[] arr = new Node[4];
        arr[0] = north;
        arr[1] = east;
        arr[2] = south;
        arr[3] = west;
        return arr;
    }
}
