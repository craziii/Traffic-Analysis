package com.trafficAnalysis;

import java.util.List;
import java.util.UUID;

public class Road{

    List<Node> nodesInRoad;

    UUID uuid;

    Node firstNode;
    Node lastNode;

    float totalPressure;

    public Road(int nodes){
        for(int i = 0; i < nodes; i++){
            Node node = new Node();
            addNodeToRoad(node);
        }
        organiseNodes();
        uuid = UUID.randomUUID();
        totalPressure = 0;
    }

    void organiseNodes(){
        for(int i = 0; i < nodesInRoad.size(); i++){
            Node node = nodesInRoad.get(i);
            if(i != 0){
                node.setNodeBefore(nodesInRoad.get(i-1));
            }
            if(i != nodesInRoad.size()-1){
                node.setNodeAfter(nodesInRoad.get(i+1));
            }
        }
        setFirstNode(nodesInRoad.get(0));
        setLastNode(nodesInRoad.get(nodesInRoad.size()-1));
    }

    void getTotalPressure(){
        float tempPressure = 0;
        for (Node node:nodesInRoad) {
            tempPressure = tempPressure + node.getPressure();
        }
        totalPressure = tempPressure;
    }

    void addNodeToRoad(Node node){
        nodesInRoad.add(node);
    }

    void attachToIntersection(){

    }

    void simulate(){

    }

    public UUID getUuid() {
        return uuid;
    }

    public Node[] getNodes(){
        return (Node[]) nodesInRoad.toArray();
    }

    public Node getFirstNode() {
        return firstNode;
    }

    public void setFirstNode(Node firstNode) {
        this.firstNode = firstNode;
    }

    public Node getLastNode() {
        return lastNode;
    }

    public void setLastNode(Node lastNode) {
        this.lastNode = lastNode;
    }

}