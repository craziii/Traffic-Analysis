package com.trafficAnalysis;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

public class Intersection {
    protected final UUID uuid;

    protected final Road[] inRoads;
    protected final Road[] outRoads;

    protected boolean[] greenLights = {false,false,false,false};

    protected int maxCars = 8;
    protected Queue<CarInput> carsInIntersection = new ArrayDeque<>();

    protected int stepCountdown = 0;

    protected final IntersectionType intersectionType;

    Intersection(IntersectionBuilder builder){
        this.uuid = builder.uuidBuilder;
        this.intersectionType = builder.intersectionTypeBuilder;
        this.inRoads = builder.inRoadsBuilder;
        this.outRoads = builder.outRoadsBuilder;
        updateGreenLights(true);
    }

    public boolean isLightGreen(Road parentRoad) {
        for(int i = 0; i < inRoads.length; i++){
            if(inRoads[i] == parentRoad){
                return greenLights[i];
            }
        }
        System.console().writer().println("ERROR: Road accessed incorrect intersection, road: "+parentRoad.getUuid()+ " is not connected to intersection: " + getUuid());
        return false;
    }

    public boolean hasSpaceForCars(){
        return carsInIntersection.size() < maxCars;
    }

    public boolean isEmpty(){
        return carsInIntersection.size() == 0;
    }

    public void addCar(Road inputDirection){
        int dir = -1;
        for(int i = 0; i < inRoads.length; i++){
            if(inRoads[i] == inputDirection){
                dir = i;
            }
        }
        switch(dir) {
            case 0:
                carsInIntersection.add(CarInput.north);
                break;
            case 1:
                carsInIntersection.add(CarInput.east);
                break;
            case 2:
                carsInIntersection.add(CarInput.south);
                break;
            case 3:
                carsInIntersection.add(CarInput.west);
                break;
        }
    }

    UpdateManager.IntersectionMove getNextIntersectionOutput(QuantumGenerator quantumGenerator){
        return new UpdateManager.IntersectionMove(this, UpdateManager.IntersectionMoveEnum.none, UpdateManager.IntersectionMoveEnum.none);
    }

    void updateGreenLights(boolean firstTime){
        if(stepCountdown > 0){
            stepCountdown--;
            return;
        }
        if(firstTime){

        }
        else{

        }
    }

    boolean validIntersectionOutput(UpdateManager.IntersectionMoveEnum output){
        switch(output){
            case north:
                if (setCarAtNode(outRoads[0])) return true;
                break;
            case east:
                if (setCarAtNode(outRoads[1])) return true;
                break;
            case south:
                if (setCarAtNode(outRoads[2])) return true;
                break;
            case west:
                if (setCarAtNode(outRoads[3])) return true;
                break;
        }
        return false;
    }

    private boolean setCarAtNode(Road outRoad) {
        if(outRoad.firstNode.nodeStatus == Node.CarStatus.noCar && outRoad.firstNode.getNodeAfter().nodeStatus == Node.CarStatus.noCar){
            outRoad.firstNode.getNodeAfter().setStatus(Node.CarStatus.waiting);
            return true;
        }
        else if(outRoad.firstNode.nodeStatus == Node.CarStatus.noCar){
            outRoad.firstNode.setStatus(Node.CarStatus.waiting);
            return true;
        }
        return false;
    }

    Road[] getRoads(boolean in){
        if(in){
            return inRoads;
        }
        else{
            return outRoads;
        }
    }

    public UUID getUuid(){ return uuid; }

    public IntersectionType getIntersectionType() {
        return intersectionType;
    }

    public enum CarInput{
        north,
        east,
        south,
        west
    }

    public enum IntersectionType{
        none,
        fourWay,
        threeWay,
        threeWayMinor,
        twoWay
    }

    public static class IntersectionBuilder{
        private final UUID uuidBuilder;
        private Road[] inRoadsBuilder;
        private Road[] outRoadsBuilder;
        
        private IntersectionType intersectionTypeBuilder;

        public IntersectionBuilder(){
            uuidBuilder = UUID.randomUUID();
            intersectionTypeBuilder = IntersectionType.none;
        }

        public IntersectionBuilder in(Road inN, Road inE, Road inS, Road inW){
            this.inRoadsBuilder = new Road[]{inN, inE, inS, inW};
            return this;
        }

        public IntersectionBuilder out(Road outN, Road outE, Road outS, Road outW){
            this.outRoadsBuilder = new Road[]{outN, outE, outS, outW};
            return this;
        }

        public IntersectionBuilder intersectionType(IntersectionType intersectionType){
            this.intersectionTypeBuilder = intersectionType;
            return this;
        }

        public Intersection build(){
            if(intersectionTypeBuilder == IntersectionType.none){
                generateType();
            }
            Intersection intersection;
            switch (intersectionTypeBuilder){
                case twoWay: intersection = new Intersection2Way(this); break;
                case threeWay: intersection = new Intersection3Way(this); break;
                case threeWayMinor: intersection = new Intersection3WayMinor(this); break;
                case fourWay: intersection = new Intersection4Way(this); break;
                default:
                    throw new IllegalStateException("Unexpected value: " + intersectionTypeBuilder);
            }
            return intersection;
        }

        private void generateType() {
            int nulls = 0;
            nulls = getNulls(nulls, inRoadsBuilder);
            nulls = getNulls(nulls, outRoadsBuilder);
            switch (nulls) {
                case 4:
                    intersectionType(IntersectionType.twoWay);
                    break;
                case 2:
                    intersectionType(IntersectionType.threeWay);
                    break;
                case 0:
                    intersectionType(IntersectionType.fourWay);
                    break;
                default:
                    break;
            }
        }

        private int getNulls(int nulls, Road[] roads) {
            int temp = nulls;
            for (Road road : roads) {
                if (road == null) {
                    temp++;
                }
            }
            return temp;
        }
    }

}
