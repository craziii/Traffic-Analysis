package com.trafficAnalysis;

import java.util.UUID;

public class Node{

    Node[] beforeAfter = new Node[2];
    Road parentRoad;
    UUID uuid;
    double pressure;
    CarStatus nodeStatus;

    public Node(){
        createUuid();
        pressure = GridManager.LOWEST_PRESSURE;
        nodeStatus = CarStatus.noCar;
    }

    public Node(Road parent){
        pressure = GridManager.LOWEST_PRESSURE;
        createUuid();
        parentRoad = parent;
        nodeStatus = CarStatus.noCar;
    }

    public Node(Road parent, float pressureOfNode){
        pressure = pressureOfNode;
        createUuid();
        parentRoad = parent;
        nodeStatus = CarStatus.noCar;
    }

    public Node(Road parent, Node nodeBeforeOrAfter, boolean isAfter){
        if(isAfter){
            setNodeAfter(nodeBeforeOrAfter);
        }
        else{
            setNodeBefore(nodeBeforeOrAfter);
        }
        createUuid();
        parentRoad = parent;
        nodeStatus = CarStatus.noCar;
    }

    public Node(Road parent, Node nodeBefore, Node nodeAfter){
        setNodeBefore(nodeBefore);
        setNodeAfter(nodeAfter);
        createUuid();
        parentRoad = parent;
        nodeStatus = CarStatus.noCar;
    }

    UpdateManager.NodeMove getNextMove(){
        UpdateManager.NodeMove output = new UpdateManager.NodeMove(this, UpdateManager.NodeMoveEnum.noCar);
        raiseStatus();
        switch (nodeStatus){
            case movingFullSpeed: output.move = nextMoveMovingFullSpeed(); break;
            case movingSlowly: output.move = nextMoveMovingSlowSpeed(); break;
            case waiting: output.move = nextMoveWaiting(); break;
            case annoyed: output.move = nextMoveAnnoyed(); break;
            default: output.move = nextMoveNoCar(); break;
        }
        changePressure();
        return output;
    }

    UpdateManager.NodeMoveEnum nextMoveMovingFullSpeed(){
        //if the intersection is right ahead
        if(getNodeAfter() == null){
            return UpdateManager.NodeMoveEnum.moveI1;
        }
        Node[] nextNodes = new Node[2];
        nextNodes[0] = getNodeAfter();
        if(nextNodes[0].nodeStatus == CarStatus.noCar && nextNodes[0].getNodeAfter() == null){
            return UpdateManager.NodeMoveEnum.moveI2;
        }
        nextNodes[1] = getNodeAfter().getNodeAfter();
        //check 2 cars in front
        if(nextNodes[0].nodeStatus == CarStatus.noCar && nextNodes[1].nodeStatus == CarStatus.noCar){
            return UpdateManager.NodeMoveEnum.move2;
        }
        //check 1 car in front
        if(nextNodes[0].nodeStatus == CarStatus.noCar){
            setStatus(CarStatus.movingSlowly);
            return nextMoveMovingSlowSpeed();
        }
        //force car to wait
        else{
            setStatus(CarStatus.waiting);
            return nextMoveWaiting();
        }
    }

    UpdateManager.NodeMoveEnum nextMoveMovingSlowSpeed(){
        if(getNodeAfter() == null && checkGreenLight()){
            return UpdateManager.NodeMoveEnum.moveI1;
        }
        else if(getNodeAfter().nodeStatus == CarStatus.noCar){
            return UpdateManager.NodeMoveEnum.move1;
        }
        else{
            setStatus(CarStatus.waiting);
            return nextMoveWaiting();
        }
    }

    UpdateManager.NodeMoveEnum nextMoveWaiting(){
        if(getNodeAfter() == null && checkGreenLight()){
            setStatus(CarStatus.movingSlowly);
            return nextMoveMovingSlowSpeed();
        }
        else if(getNodeAfter().nodeStatus == CarStatus.noCar){
            setStatus(CarStatus.movingSlowly);
            return nextMoveMovingSlowSpeed();
        }
        else if(getPressure() > GridManager.WAITING_THRESHOLD_PRESSURE){
            setStatus(CarStatus.annoyed);
            return nextMoveAnnoyed();
        }
        else{
            return UpdateManager.NodeMoveEnum.noMove;
        }
    }

    UpdateManager.NodeMoveEnum nextMoveAnnoyed(){
        if(getNodeAfter() == null && checkGreenLight()){
            setStatus(CarStatus.movingSlowly);
            return nextMoveMovingSlowSpeed();
        }
        else if(getNodeAfter().nodeStatus == CarStatus.noCar){
            setStatus(CarStatus.movingSlowly);
            return nextMoveMovingSlowSpeed();
        }
        else{
            return UpdateManager.NodeMoveEnum.noMove;
        }
    }

    UpdateManager.NodeMoveEnum nextMoveNoCar(){
        return UpdateManager.NodeMoveEnum.noCar;
    }

    void raiseStatus(){
        switch(nodeStatus){
            case movingSlowly: setStatus(CarStatus.movingFullSpeed); break;
            case waiting: setStatus(CarStatus.movingSlowly); break;
        }
    }

    private void changePressure(){
        switch(nodeStatus){
            case movingFullSpeed: setPressure(movingFullSpeedPressure(getPressure())); break;
            case movingSlowly: setPressure(movingSlowSpeedPressure(getPressure())); break;
            case waiting: setPressure(waitingPressure(getPressure())); break;
            case annoyed: setPressure(annoyedPressure(getPressure())); break;
            case noCar: setPressure(noCarPressure(getPressure())); break;
        }
        if(getPressure() > GridManager.HIGHEST_PRESSURE){
            setPressure(GridManager.HIGHEST_PRESSURE);
        }
    }

    double movingFullSpeedPressure(double input){
        return (input*GridManager.FULL_SPEED_PRESSURE_RATE);
    }

    double movingSlowSpeedPressure(double input){
        return (input*GridManager.SLOW_SPEED_PRESSURE_RATE);
    }

    double waitingPressure(double input){
        return input*GridManager.WAITING_PRESSURE_RATE;
    }

    double annoyedPressure(double input){
        return (Math.log(Math.exp(input)*input)*GridManager.ANNOYED_PRESSURE_RATE) + input;
    }

    double noCarPressure(double input){
        return (input*GridManager.NO_CAR_PRESSURE_RATE);
    }

    boolean addCar(){
        if(nodeStatus != CarStatus.noCar){
            return false;
        }
        else{
            carEnteringNode();
        }
        return true;
    }

    boolean checkGreenLight(){
        return parentRoad.outIntersection.isLightGreen(parentRoad);
    }

    void carEnteringNode(){
        carEnteringNode(CarStatus.movingFullSpeed);
    }

    void carEnteringNode(CarStatus type){
        setStatus(type);
    }

    void carExitingNode(){
        setStatus(CarStatus.noCar);
    }

    public CarStatus getStatus(){
        return nodeStatus;
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

    double getPressure(){
        return pressure;
    }

    void setPressure(double p){
        if(p < GridManager.LOWEST_PRESSURE){
            pressure = GridManager.LOWEST_PRESSURE;
        }
        else{
            pressure = p;
        }
    }

    public enum CarStatus{
        annoyed,
        waiting,
        movingSlowly,
        movingFullSpeed,
        noCar
    }
}
