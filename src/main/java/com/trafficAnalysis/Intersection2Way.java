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
        if(temp.in == outputDirections[0]){
            temp.out = outputDirections[1];
        }
        else{
            temp.out = outputDirections[0];
        }
        if(validIntersectionOutput(temp.out)){
            carsInIntersection.remove();
            return temp;
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
        if(stepCountdown > 0){
            return;
        }
        if(firstTime){
            List<UpdateManager.Direction> lights = new ArrayList<>();
            if(quantumGenerator.getNextBoolean(2)){
                lights.add(outputDirections[0]);
            }
            else{
                lights.add(outputDirections[1]);
            }
            setGreenLights(lights.toArray(new UpdateManager.Direction[0]));
        }
        if(pressureSystem && !firstTime){
            updateGreenLightsPressure();
        }
        else{
            updateGreenLightsNormal();
        }
    }
}
