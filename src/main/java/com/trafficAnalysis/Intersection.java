package com.trafficAnalysis;

import javax.lang.model.type.IntersectionType;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.UUID;
import java.util.logging.Logger;

public class Intersection {
    private final UUID uuid;

    private final Road[] inRoads;
    private final Road[] outRoads;
    
    private boolean[] greenLights = {false,false,false,false};

    private int maxCars = 8;
    private Queue<CarInput> carsInIntersection = new ArrayDeque<>();

    private int stepCountdown = 0;

    private final IntersectionType intersectionType;

    Intersection(IntersectionBuilder builder){
        this.uuid = builder.uuidBuilder;
        this.intersectionType = builder.intersectionTypeBuilder;
        this.inRoads = builder.inRoadsBuilder;
        this.outRoads = builder.outRoadsBuilder;
        updateGreenLights(true);
    }

    //<editor-fold desc="old code">

    /*

    UpdateManager.IntersectionMove[] simulate(QuantumGenerator quantumGenerator, int[] cars){
        List<UpdateManager.IntersectionMove> outputs = new ArrayList<>();
        for(int i = 0; i < cars.length; i++){
            for(int j = 0; j < cars[i]; j++){
                UpdateManager.IntersectionMove temp = new UpdateManager.IntersectionMove(this, UpdateManager.IntersectionMoveEnum.northToEast);
                if(quantumGenerator.getNextBoolean()){ // First, go straight
                    switch (i){
                        case 0: temp.move = UpdateManager.IntersectionMoveEnum.northToSouth; break;
                        case 1: temp.move = UpdateManager.IntersectionMoveEnum.eastToWest; break;
                        case 2: temp.move = UpdateManager.IntersectionMoveEnum.southToNorth; break;
                        case 3: temp.move = UpdateManager.IntersectionMoveEnum.westToEast; break;
                    }
                }
                else if(quantumGenerator.getNextBoolean()){ // Second, left turn
                    switch (i){
                        case 0: temp.move = UpdateManager.IntersectionMoveEnum.northToEast; break;
                        case 1: temp.move = UpdateManager.IntersectionMoveEnum.eastToSouth; break;
                        case 2: temp.move = UpdateManager.IntersectionMoveEnum.southToWest; break;
                        case 3: temp.move = UpdateManager.IntersectionMoveEnum.westToNorth; break;
                    }
                }
                else{ // Finally, right turn
                    switch (i){
                        case 0: temp.move = UpdateManager.IntersectionMoveEnum.northToWest; break;
                        case 1: temp.move = UpdateManager.IntersectionMoveEnum.eastToNorth; break;
                        case 2: temp.move = UpdateManager.IntersectionMoveEnum.southToEast; break;
                        case 3: temp.move = UpdateManager.IntersectionMoveEnum.westToSouth; break;
                    }
                }
                outputs.add(temp);
            }
        }
        return outputs.toArray(new UpdateManager.IntersectionMove[0]);
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
     */

    //</editor-fold>

    //<editor-fold desc="new code">




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
        switch(dir){
            case 0: carsInIntersection.add(CarInput.north); break;
            case 1: carsInIntersection.add(CarInput.east); break;
            case 2: carsInIntersection.add(CarInput.south); break;
            case 3: carsInIntersection.add(CarInput.west); break;
        }
    }

    UpdateManager.IntersectionMove getNextIntersectionOutput(QuantumGenerator quantumGenerator){
        CarInput tempInput = carsInIntersection.remove();
        UpdateManager.IntersectionMove temp = new UpdateManager.IntersectionMove(this, UpdateManager.IntersectionMoveEnum.none, UpdateManager.IntersectionMoveEnum.none);
        switch (tempInput){
            case north: temp.in = UpdateManager.IntersectionMoveEnum.north; break;
            case east: temp.in = UpdateManager.IntersectionMoveEnum.east; break;
            case south: temp.in = UpdateManager.IntersectionMoveEnum.south; break;
            case west: temp.in = UpdateManager.IntersectionMoveEnum.west; break;
        }
        switch(getIntersectionType()){
            case fourWay: temp = getNextIntersectionOutput4Way(quantumGenerator, temp); break;
            case threeWay: temp = getNextIntersectionOutput3Way(quantumGenerator, temp); break;
            case twoWay: temp = getNextIntersectionOutput2Way(temp); break;
        }
        if(validIntersectionOutput(temp.out)){
            return temp;
        }
        else{
            carsInIntersection.add(tempInput);
        }
        return null;
    }

    UpdateManager.IntersectionMove getNextIntersectionOutput2Way(UpdateManager.IntersectionMove output) {
        UpdateManager.IntersectionMoveEnum result = UpdateManager.IntersectionMoveEnum.none;
        switch(output.in){
            case north: result = UpdateManager.IntersectionMoveEnum.south; break;
            case east: result = UpdateManager.IntersectionMoveEnum.west; break;
            case south: result = UpdateManager.IntersectionMoveEnum.north; break;
            case west: result = UpdateManager.IntersectionMoveEnum.east; break;
        }
        return new UpdateManager.IntersectionMove(this, output.in, result);
    }

    UpdateManager.IntersectionMove getNextIntersectionOutput3Way(QuantumGenerator quantumGenerator, UpdateManager.IntersectionMove output) {
        List<UpdateManager.IntersectionMoveEnum> options = new ArrayList<>();
        for(int i = 0; i < inRoads.length; i++){
            if(inRoads[i] != null){
                switch (i){
                    case 0: options.add(UpdateManager.IntersectionMoveEnum.north); break;
                    case 1: options.add(UpdateManager.IntersectionMoveEnum.east); break;
                    case 2: options.add(UpdateManager.IntersectionMoveEnum.south); break;
                    case 3: options.add(UpdateManager.IntersectionMoveEnum.west); break;
                }
            }
        }
        UpdateManager.IntersectionMoveEnum[] directions = options.toArray(new UpdateManager.IntersectionMoveEnum[0]);
        UpdateManager.IntersectionMove temp = output;
        int offset = -3;
        for(int i = 0; i < directions.length; i++){
            if(temp.in == directions[i]){
                offset = i;
            }
        }
        if(quantumGenerator.getNextBoolean()){ // First, next clockwise option
            switch (offset){
                case 0: temp.out = directions[1]; break;
                case 1: temp.out = directions[2]; break;
                case 2: temp.out = directions[0]; break;
            }
        }
        else{ // Else, other option
            switch (offset){
                case 0: temp.out = directions[2]; break;
                case 1: temp.out = directions[0]; break;
                case 2: temp.out = directions[1]; break;
            }
        }
        return temp;
    }

    UpdateManager.IntersectionMove getNextIntersectionOutput4Way(QuantumGenerator quantumGenerator, UpdateManager.IntersectionMove output) {
        UpdateManager.IntersectionMove temp = output;
        if(quantumGenerator.getNextBoolean()){ // First, go straight
            switch (temp.in){
                case north: temp.out = UpdateManager.IntersectionMoveEnum.south; break;
                case east: temp.out = UpdateManager.IntersectionMoveEnum.west; break;
                case south: temp.out = UpdateManager.IntersectionMoveEnum.north; break;
                case west: temp.out = UpdateManager.IntersectionMoveEnum.east; break;
            }
        }
        else if(quantumGenerator.getNextBoolean()){ // Second, left turn
            switch (temp.in){
                case north: temp.out = UpdateManager.IntersectionMoveEnum.east; break;
                case east: temp.out = UpdateManager.IntersectionMoveEnum.south; break;
                case south: temp.out = UpdateManager.IntersectionMoveEnum.west; break;
                case west: temp.out = UpdateManager.IntersectionMoveEnum.north; break;
            }
        }
        else{ // Finally, right turn
            switch (temp.in){
                case north: temp.out = UpdateManager.IntersectionMoveEnum.west; break;
                case east: temp.out = UpdateManager.IntersectionMoveEnum.north; break;
                case south: temp.out = UpdateManager.IntersectionMoveEnum.east; break;
                case west: temp.out = UpdateManager.IntersectionMoveEnum.south; break;
            }
        }
        return temp;
    }

    void updateGreenLights(boolean firstTime){
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

    //</editor-fold>

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

    private enum CarInput{
        north,
        east,
        south,
        west
    }

    public enum IntersectionType{
        none,
        fourWay,
        threeWay,
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
            Intersection intersection = new Intersection(this);
            validateIntersection(intersection);
            return intersection;
        }

        private void generateType(){
            int nulls = 0;
            nulls = getNulls(nulls, inRoadsBuilder);
            nulls = getNulls(nulls, outRoadsBuilder);
            switch (nulls){
                case 4: intersectionType(IntersectionType.twoWay); break;
                case 2: intersectionType(IntersectionType.threeWay); break;
                case 0: intersectionType(IntersectionType.fourWay); break;
                default: break;
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

        private void validateIntersection(Intersection intersection){
            StringBuilder stringBuilder = new StringBuilder();


            if(stringBuilder.toString().equals("")){
                System.out.println("Intersection: " + uuidBuilder + " validated correctly");
            }
            else{
                System.out.println(stringBuilder);
            }

        }
    }

}
