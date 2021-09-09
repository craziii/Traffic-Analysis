package com.trafficAnalysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Intersection {
    private final UUID uuid;
    private final Road inNorth;
    private final Road inEast;
    private final Road inSouth;
    private final Road inWest;
    private final Road outNorth;
    private final Road outEast;
    private final Road outSouth;
    private final Road outWest;

    private final Road[] inRoads;
    private final Road[] outRoads;
    
    private boolean[] greenLights = {false,false,false,false};

    private int stepCountdown = 0;
    
    private final IntersectionType intersectionType;

    Intersection(IntersectionBuilder builder){
        this.uuid = builder.uuidBuilder;
        this.inNorth = builder.inNorthBuilder;
        this.inEast = builder.inEastBuilder;
        this.inSouth = builder.inSouthBuilder;
        this.inWest = builder.inWestBuilder;
        this.outNorth = builder.outNorthBuilder;
        this.outEast = builder.outEastBuilder;
        this.outSouth = builder.outSouthBuilder;
        this.outWest = builder.outWestBuilder;
        this.intersectionType = builder.intersectionTypeBuilder;
        this.inRoads = getRoads(true);
        this.outRoads = getRoads(false);
        updateGreenLights();
    }



    UpdateManager.IntersectionMove[] simulate(QuantumGenerator quantumGenerator){

    }

    UpdateManager.IntersectionMove[] greenLight(){
        
    }

    UpdateManager.IntersectionMove createMove(Node node, char inDirection){
        UpdateManager.IntersectionMoveEnum intersectionMoveEnum = UpdateManager.IntersectionMoveEnum.none;
        switch(intersectionType){
            case fourWay:  break;
            case threeWay:  break;
            case twoWay:  break;
            default:  break;
        }
        switch (inDirection){
            case 'n': break;
            case 'e': break;
            case 's': break;
            case 'w': break;
        }
        return new UpdateManager.IntersectionMove(this, intersectionMoveEnum);
    }

    void updateGreenLights(){
        switch (intersectionType){
            case fourWay: break;
            case threeWay: break;
            case twoWay: break;
        }
        
    }

    Road[] getRoads(boolean inRoads){
        Road[] temp = new Road[4];
        if(inRoads){
            temp[0] = getInNorth();
            temp[1] = getInEast();
            temp[2] = getInSouth();
            temp[3] = getInWest();
        }
        else{
            temp[0] = getOutNorth();
            temp[1] = getOutEast();
            temp[2] = getOutSouth();
            temp[3] = getOutWest();
        }
        return (Road[])Arrays.stream(temp).filter(Objects::nonNull).toArray();
    }

    public UUID getUuid(){ return uuid; }

    public Road getInNorth() {
        return inNorth;
    }

    public Road getInEast() {
        return inEast;
    }

    public Road getInSouth() {
        return inSouth;
    }

    public Road getInWest() {
        return inWest;
    }

    public Road getOutNorth() {
        return outNorth;
    }

    public Road getOutEast() {
        return outEast;
    }

    public Road getOutSouth() {
        return outSouth;
    }

    public Road getOutWest() {
        return outWest;
    }
    
    public enum IntersectionType{
        none,
        fourWay,
        threeWay,
        twoWay
    }

    public static class IntersectionBuilder{
        private final UUID uuidBuilder;
        private Road inNorthBuilder;
        private Road inEastBuilder;
        private Road inSouthBuilder;
        private Road inWestBuilder;
        private Road outNorthBuilder;
        private Road outEastBuilder;
        private Road outSouthBuilder;
        private Road outWestBuilder;
        
        private IntersectionType intersectionTypeBuilder;

        public IntersectionBuilder(){
            uuidBuilder = UUID.randomUUID();
            intersectionTypeBuilder = IntersectionType.none;
        }

        public IntersectionBuilder inN(Road inN){
            this.inNorthBuilder = inN;
            return this;
        }

        public IntersectionBuilder inE(Road inE){
            this.inEastBuilder = inE;
            return this;
        }

        public IntersectionBuilder inS(Road inS){
            this.inSouthBuilder = inS;
            return this;
        }

        public IntersectionBuilder inW(Road inW){
            this.inWestBuilder = inW;
            return this;
        }

        public IntersectionBuilder outN(Road outN){
            this.outNorthBuilder = outN;
            return this;
        }

        public IntersectionBuilder outE(Road outE){
            this.outEastBuilder = outE;
            return this;
        }

        public IntersectionBuilder outS(Road outS){
            this.outSouthBuilder = outS;
            return this;
        }

        public IntersectionBuilder outW(Road outW){
            this.outWestBuilder = outW;
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
            Intersection intersection = new Intersection(this);
            validateIntersection(intersection);
            return intersection;
        }

        private void generateType(){
            int nulls = 0;
            nulls = getNulls(nulls, inNorthBuilder, inEastBuilder, inSouthBuilder, inWestBuilder);
            nulls = getNulls(nulls, outNorthBuilder, outEastBuilder, outSouthBuilder, outWestBuilder);
            switch (nulls){
                case 4: intersectionType(IntersectionType.twoWay); break;
                case 2: intersectionType(IntersectionType.threeWay); break;
                case 0: intersectionType(IntersectionType.fourWay); break;
                default: break;
            }
        }

        private int getNulls(int nulls, Road n, Road e, Road s, Road w) {
            if(n == null){
                nulls++;
            }
            if(e == null){
                nulls++;
            }
            if(s == null){
                nulls++;
            }
            if(w == null){
                nulls++;
            }
            return nulls;
        }

        private void validateIntersection(Intersection intersection){
            StringBuilder stringBuilder = new StringBuilder();


            if(stringBuilder.toString().equals("")){
                System.out.println("Intersection validated correctly");
            }
            else{
                System.out.println(stringBuilder);
            }

        }
    }

}
