package com.trafficAnalysis;

import java.util.List;
import java.util.UUID;

public class Road{

    List<com.trafficAnalysis.Node> nodesInRoad;

    UUID uuid;

    com.trafficAnalysis.Node firstNode;
    com.trafficAnalysis.Node lastNode;

    public Road(int nodes){
        for(int i = 0; i < nodes; i++){
            com.trafficAnalysis.Node node = new com.trafficAnalysis.Node();
            addNodeToRoad(node);
        }
        organiseNodes();
        uuid = UUID.randomUUID();
    }

    void organiseNodes(){
        for(int i = 0; i < nodesInRoad.size(); i++){
            com.trafficAnalysis.Node node = nodesInRoad.get(i);
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

    void addNodeToRoad(com.trafficAnalysis.Node node){
        nodesInRoad.add(node);
    }

    void attachToIntersection(){

    }

    void simulate(){

    }

    public UUID getUuid() {
        return uuid;
    }

    public com.trafficAnalysis.Node[] getNodes(){
        return (com.trafficAnalysis.Node[]) nodesInRoad.toArray();
    }

    public com.trafficAnalysis.Node getFirstNode() {
        return firstNode;
    }

    public void setFirstNode(com.trafficAnalysis.Node firstNode) {
        this.firstNode = firstNode;
    }

    public com.trafficAnalysis.Node getLastNode() {
        return lastNode;
    }

    public void setLastNode(com.trafficAnalysis.Node lastNode) {
        this.lastNode = lastNode;
    }

}
