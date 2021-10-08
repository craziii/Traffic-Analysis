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

    protected UpdateManager.Direction[] outputDirections;

    protected boolean[] greenLights = {false,false,false,false};

    protected int maxCars = 8;
    protected Queue<UpdateManager.Direction> carsInIntersection = new ArrayDeque<>();

    protected int stepCountdown = 0;

    protected final IntersectionType intersectionType;

    protected QuantumGenerator quantumGenerator;

    Intersection(IntersectionBuilder builder, QuantumGenerator qg){
        this.uuid = builder.uuidBuilder;
        this.intersectionType = builder.intersectionTypeBuilder;
        this.inRoads = builder.inRoadsBuilder;
        this.outRoads = builder.outRoadsBuilder;
        quantumGenerator = qg;
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
                carsInIntersection.add(UpdateManager.Direction.north);
                break;
            case 1:
                carsInIntersection.add(UpdateManager.Direction.east);
                break;
            case 2:
                carsInIntersection.add(UpdateManager.Direction.south);
                break;
            case 3:
                carsInIntersection.add(UpdateManager.Direction.west);
                break;
        }
    }

    UpdateManager.IntersectionMove getNextIntersectionOutput(QuantumGenerator quantumGenerator){
        return new UpdateManager.IntersectionMove(this, UpdateManager.Direction.none, UpdateManager.Direction.none);
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
    
    void setGreenLights(UpdateManager.Direction[] direction){
        for(boolean b:greenLights){
            b = false;
        }
        for(UpdateManager.Direction dir:direction){
            greenLights[dir.ordinal()] = true;
        }
    }

    boolean validIntersectionOutput(UpdateManager.Direction output){
        return setCarAtNode(outRoads[output.ordinal()]);
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

        private QuantumGenerator quantumGeneratorBuilder;
        
        private IntersectionType intersectionTypeBuilder;

        public IntersectionBuilder(QuantumGenerator qg){
            uuidBuilder = UUID.randomUUID();
            intersectionTypeBuilder = IntersectionType.none;
            quantumGeneratorBuilder = qg;
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
                case twoWay: intersection = new Intersection2Way(this, quantumGeneratorBuilder); break;
                case threeWay: intersection = new Intersection3Way(this, quantumGeneratorBuilder); break;
                case threeWayMinor: intersection = new Intersection3WayMinor(this, quantumGeneratorBuilder); break;
                case fourWay: intersection = new Intersection4Way(this, quantumGeneratorBuilder); break;
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
