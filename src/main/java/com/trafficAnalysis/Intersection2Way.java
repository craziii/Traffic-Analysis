package com.trafficAnalysis;

import java.util.ArrayList;
import java.util.List;

public class Intersection2Way extends Intersection{

    Intersection2Way(IntersectionBuilder builder, QuantumGenerator qg) {
        super(builder, qg);
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
        List<UpdateManager.Direction> lights = new ArrayList<>();
        if(firstTime){
            List<UpdateManager.Direction> outputDirs = new ArrayList<>();
            for(int i = 0; i < inRoads.length; i++){
                if(inRoads[i] != null){
                    outputDirs.add(UpdateManager.Direction.values()[i]);
                }
            }
            outputDirections = outputDirs.toArray(new UpdateManager.Direction[0]);
            if(quantumGenerator.getNextBoolean()){
                lights.add(outputDirections[0]);
            }
            else{
                lights.add(outputDirections[1]);
            }
        }
        else{
            if(greenLights[outputDirections[0].ordinal()]){
                lights.add(outputDirections[1]);
            }
            else{
                lights.add(outputDirections[0]);
            }
        }
        setGreenLights(lights.toArray(new UpdateManager.Direction[0]));
    }
}
