package com.trafficAnalysis;

import java.util.UUID;

public class Node{

    Node[] beforeAfter = new Node[2];
    UUID uuid;
    float pressure;
    CarStatus nodeStatus;

    public Node(){
        createUuid();
        pressure = 0;
        nodeStatus = CarStatus.noCar;
    }

    public Node(float pressureOfNode){
        pressure = pressureOfNode;
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

    void updatePressure(){
        switch (nodeStatus){
            case noCar: break;
            case movingFullSpeed: moveCarFullSpeed(); break;
            case movingSlowly: moveCarSlowSpeed(); break;
            case waiting: carWaiting(); break;
            case annoyed: carAnnoyed(); break;
        }
    }

    void moveCarFullSpeed(){
        CarStatus[] nextNodes = {getNodeAfter().nodeStatus,getNodeAfter().getNodeAfter().nodeStatus};
        if(nextNodes[0] == CarStatus.noCar && nextNodes[1] == CarStatus.noCar){
            carEnteringNode(getNodeAfter().getNodeAfter());
            carExitingNode();
        }
        else if(nextNodes[0] == CarStatus.noCar){
            setStatus(CarStatus.movingSlowly);
            moveCarSlowSpeed();
        }
        else{
            setStatus(CarStatus.waiting);
            carWaiting();
        }
    }

    void moveCarSlowSpeed(){
        CarStatus nextNode = getNodeAfter().nodeStatus;
        if(nextNode == CarStatus.noCar){
            carEnteringNode(getNodeAfter());
            carExitingNode();
        }
        else{
            setStatus(CarStatus.waiting);
            carWaiting();
        }
    }

    void carWaiting(){
        CarStatus nextNode = getNodeAfter().nodeStatus;
        if(nextNode == CarStatus.noCar){
            setStatus(CarStatus.movingSlowly);
            moveCarSlowSpeed();
        }
    }

    void carAnnoyed(){

    }

    void carEnteringNode(Node entryPoint){
        if(entryPoint == getNodeBefore()){
            nodeStatus = CarStatus.movingSlowly;
        }
        else{
            nodeStatus = CarStatus.movingFullSpeed;
        }
    }

    void carExitingNode(){
        nodeStatus = CarStatus.noCar;
    }

    public void setStatus(CarStatus status){
        nodeStatus = status;
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

    float getPressure(){
        return pressure;
    }

    void setPressure(float p){
        pressure = p;
    }

    public enum CarStatus{
        annoyed,
        waiting,
        movingSlowly,
        movingFullSpeed,
        noCar
    }
}
