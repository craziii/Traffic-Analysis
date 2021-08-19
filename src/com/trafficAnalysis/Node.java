package com.trafficAnalysis;

import java.util.UUID;

public class Node{

    Node[] beforeAfter = new Node[2];
    UUID uuid;

    public Node(){
        createUuid();
    }

    public Node(Node nodeBeforeOrAfter, boolean isAfter){
        if(isAfter){
            setNodeAfter(nodeBeforeOrAfter);
        }
        else{
            setNodeBefore(nodeBeforeOrAfter);
        }
        createUuid();
    }

    public Node(Node nodeBefore, Node nodeAfter){
        setNodeBefore(nodeBefore);
        setNodeAfter(nodeAfter);
        createUuid();
    }

    void createUuid(){
        uuid = UUID.randomUUID();
    }

    UUID getUuid(){
        return uuid;
    }

    Node getNodeBefore(){
        return beforeAfter[0];
    }

    void setNodeBefore(Node node){
        beforeAfter[0] = node;
    }

    Node getNodeAfter(){
        return beforeAfter[1];
    }

    void setNodeAfter(Node node){
        beforeAfter[1] = node;
    }


}
