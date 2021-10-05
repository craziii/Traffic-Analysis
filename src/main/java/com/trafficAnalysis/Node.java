package com.trafficAnalysis;

import java.util.UUID;

public class Node{

    Node[] beforeAfter = new Node[2];
    Road parentRoad;
    UUID uuid;
    float pressure;
    CarStatus nodeStatus;

    public Node(){
        createUuid();
        pressure = 0;
        nodeStatus = CarStatus.noCar;
    }

    public Node(Road parent, float pressureOfNode){
        pressure = pressureOfNode;
        createUuid();
        parentRoad = parent;
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
    }

    public Node(Road parent, Node nodeBefore, Node nodeAfter){
        setNodeBefore(nodeBefore);
        setNodeAfter(nodeAfter);
        createUuid();
        parentRoad = parent;
    }

    //<editor-fold desc="old code">

    /*

    UpdateManager.NodeMove updatePressure(){
        switch (nodeStatus){
            case movingFullSpeed: return moveCarFullSpeed();
            case movingSlowly: return moveCarSlowSpeed();
            case waiting: return carWaiting();
            case annoyed: return carAnnoyed();
            default: return new UpdateManager.NodeMove(this, UpdateManager.NodeMoveEnum.noMove);
        }
    }

    UpdateManager.NodeMove moveCarFullSpeed(){
        UpdateManager.NodeMove move = new UpdateManager.NodeMove(this, UpdateManager.NodeMoveEnum.noMove);
        CarStatus[] nextNodes = {getNodeAfter().nodeStatus,getNodeAfter().getNodeAfter().nodeStatus};
        if(nextNodes[0] == CarStatus.noCar && nextNodes[1] == CarStatus.noCar){
            carEnteringNode(getNodeAfter().getNodeAfter());
            carExitingNode();
            move.move = UpdateManager.NodeMoveEnum.move2;
            return move;
        }
        else if(nextNodes[0] == CarStatus.noCar){
            setStatus(CarStatus.movingSlowly);
            return moveCarSlowSpeed();
        }
        else{
            setStatus(CarStatus.waiting);
            return carWaiting();
        }
    }

    UpdateManager.NodeMove moveCarSlowSpeed(){
        UpdateManager.NodeMove move = new UpdateManager.NodeMove(this, UpdateManager.NodeMoveEnum.noMove);
        CarStatus nextNode = getNodeAfter().nodeStatus;
        if(nextNode == CarStatus.noCar){
            carEnteringNode(getNodeAfter());
            carExitingNode();
            move.move = UpdateManager.NodeMoveEnum.move1;
            return move;
        }
        else{
            setStatus(CarStatus.waiting);
            return carWaiting();
        }
    }

    void carWaiting(){
        CarStatus nextNode = getNodeAfter().nodeStatus;
        if(nextNode == CarStatus.noCar){
            setStatus(CarStatus.movingSlowly);
            return moveCarSlowSpeed();
        }
        else if(pressure < )
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

     */

    //</editor-fold>

    //<editor-fold desc="new code">

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
            return UpdateManager.NodeMoveEnum.move2;
        }
        Node[] nextNodes = new Node[2];
        nextNodes[0] = getNodeAfter();
        if(nextNodes[0].nodeStatus == CarStatus.noCar && nextNodes[0].getNodeAfter() == null){
            return UpdateManager.NodeMoveEnum.move2;
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
            return UpdateManager.NodeMoveEnum.move1;
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

    private void changePressure() {
        switch(nodeStatus){
            case movingFullSpeed: setPressure(getPressure()+GridManager.FULL_SPEED_PRESSURE_RATE); break;
            case movingSlowly: setPressure(getPressure()+GridManager.SLOW_SPEED_PRESSURE_RATE); break;
            case waiting: setPressure(getPressure()+GridManager.WAITING_PRESSURE_RATE); break;
            case annoyed: setPressure(getPressure()+GridManager.ANNOYED_PRESSURE_RATE); break;
            case noCar: setPressure(getPressure()+GridManager.NO_CAR_PRESSURE_RATE); break;
        }
    }



    boolean checkGreenLight(){
        return parentRoad.outIntersection.isLightGreen(parentRoad);
    }

    //</editor-fold>

    void carEnteringNode(){
        carEnteringNode(CarStatus.movingFullSpeed);
    }

    void carEnteringNode(CarStatus type){
        setStatus(type);
    }

    void carExitingNode(){
        setStatus(CarStatus.noCar);
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
        if(p < GridManager.LOWEST_PRESSURE){
            pressure = 0;
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
