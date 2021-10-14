package com.trafficAnalysis;

import java.util.ArrayList;
import java.util.List;

public class Intersection2Way extends Intersection{

    Intersection2Way(IntersectionBuilder builder, QuantumGenerator qg) {
        super(builder, qg);
    }

    @Override
    UpdateManager.IntersectionMove getNextIntersectionOutput(QuantumGenerator quantumGenerator) {
        UpdateManager.IntersectionMove temp = super.getNextIntersectionOutput(quantumGenerator);
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
            carsInIntersection.add(temp.in);
        }
        return null;
    }

    @Override
    void updateGreenLightsNormal() {
        List<UpdateManager.Direction> lights = new ArrayList<>();
        if (greenLights[outputDirections[0].ordinal()]) {
            lights.add(outputDirections[1]);
        } else {
            lights.add(outputDirections[0]);
        }
        setGreenLights(lights.toArray(new UpdateManager.Direction[0]));
    }

    @Override
    void updateGreenLightsPressure() {
        List<UpdateManager.Direction> lights = new ArrayList<>();
        double redPressure = 0;
        double greenPressure = 0;
        if (greenLights[outputDirections[0].ordinal()]) {
            redPressure = inRoads[outputDirections[1].ordinal()].getTotalPressure();
            greenPressure = inRoads[outputDirections[0].ordinal()].getTotalPressure();
            redPressure += previousRedLightPressure[outputDirections[1].ordinal()];
            if (greenPressure < redPressure) {
                lights.add(outputDirections[1]);
            } else {
                lights.add(outputDirections[0]);
            }
        } else {
            redPressure = inRoads[outputDirections[0].ordinal()].getTotalPressure();
            greenPressure = inRoads[outputDirections[1].ordinal()].getTotalPressure();
            redPressure += previousRedLightPressure[outputDirections[0].ordinal()];
            if (greenPressure < redPressure) {
                lights.add(outputDirections[0]);
            } else {
                lights.add(outputDirections[1]);
            }
        }
        updateRedLightPressure(lights);
        setGreenLights(lights.toArray(new UpdateManager.Direction[0]));
    }

    @Override
    void updateGreenLights(boolean firstTime, boolean pressureSystem){
        super.updateGreenLights(firstTime,pressureSystem);
        if(firstTime){
            List<UpdateManager.Direction> lights = new ArrayList<>();
            List<UpdateManager.Direction> outputDirs = new ArrayList<>();
            for(int i = 0; i < inRoads.length; i++){
                if(inRoads[i] != null){
                    outputDirs.add(UpdateManager.Direction.values()[i]);
                }
            }
            outputDirections = outputDirs.toArray(new UpdateManager.Direction[0]);
            if(quantumGenerator.getNextBoolean(2)){
                lights.add(outputDirections[0]);
            }
            else{
                lights.add(outputDirections[1]);
            }
            setGreenLights(lights.toArray(new UpdateManager.Direction[0]));
        }
        if(pressureSystem){
            updateGreenLightsPressure();
        }
        else{
            updateGreenLightsNormal();
        }
    }
}
