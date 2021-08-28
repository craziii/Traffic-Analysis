package com.trafficAnalysis;

public class Intersection {
    Node[] incomingNodes = new Node[4];
    Node[] outgoingNodes = new Node[4];

    Intersection(IntersectionNodes in, IntersectionNodes out){
        incomingNodes = in.toArray();
        outgoingNodes = out.toArray();
    }

    Intersection(IntersectionNodes nodes, boolean ingoing){
        if(ingoing){
            incomingNodes = nodes.toArray();
        }
        else{
            outgoingNodes = nodes.toArray();
        }
    }

    public class IntersectionNodes{
        Node north;
        Node east;
        Node south;
        Node west;

        IntersectionNodes(Node n, Node e, Node s, Node w){
            north = n;
            east = e;
            south = s;
            west = w;
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
}
