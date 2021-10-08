package com.trafficAnalysis;

public class Intersection2Way extends Intersection{

    Intersection2Way(IntersectionBuilder builder) {
        super(builder);
    }

    @Override
    UpdateManager.IntersectionMove getNextIntersectionOutput(QuantumGenerator quantumGenerator) {
        UpdateManager.Direction tempInput = carsInIntersection.remove();
        UpdateManager.IntersectionMove temp = new UpdateManager.IntersectionMove(this, UpdateManager.Direction.none, UpdateManager.Direction.none);
        switch (tempInput) {
            case north:
                temp.in = UpdateManager.Direction.north;
                break;
            case east:
                temp.in = UpdateManager.Direction.east;
                break;
            case south:
                temp.in = UpdateManager.Direction.south;
                break;
            case west:
                temp.in = UpdateManager.Direction.west;
                break;
        }
        switch(temp.in) {
            case north:
                temp.out = UpdateManager.Direction.south;
                break;
            case east:
                temp.out = UpdateManager.Direction.west;
                break;
            case south:
                temp.out = UpdateManager.Direction.north;
                break;
            case west:
                temp.out = UpdateManager.Direction.east;
                break;
        }
        if(validIntersectionOutput(temp.out)){
            return temp;
        }
        else{
            carsInIntersection.add(tempInput);
        }
        return null;
    }

    @Override
    void updateGreenLights(boolean firstTime){
        if(stepCountdown > 0){
            stepCountdown--;
            return;
        }
        if(firstTime){
            if(Math.random() > 0.5){

            }
        }
        else{

        }
    }

}
