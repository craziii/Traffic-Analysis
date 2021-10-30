package com.trafficAnalysis;

import java.util.ArrayList;
import java.util.List;

public class Intersection4Way extends Intersection{

    Intersection4Way(IntersectionBuilder builder, QuantumGenerator qg) {
        super(builder,qg);
    }

    @Override
    UpdateManager.IntersectionMove getNextIntersectionOutput(QuantumGenerator quantumGenerator) {
        UpdateManager.IntersectionMove temp = super.getNextIntersectionOutput(quantumGenerator);
        if (quantumGenerator.getNextBoolean(0)) { // First, go straight
            switch (temp.in) {
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
        } else if (quantumGenerator.getNextBoolean(2)) { // Second, left turn
            switch (temp.in) {
                case north:
                    temp.out = UpdateManager.Direction.east;
                    break;
                case east:
                    temp.out = UpdateManager.Direction.south;
                    break;
                case south:
                    temp.out = UpdateManager.Direction.west;
                    break;
                case west:
                    temp.out = UpdateManager.Direction.north;
                    break;
            }
        } else { // Finally, right turn
            switch (temp.in) {
                case north:
                    temp.out = UpdateManager.Direction.west;
                    break;
                case east:
                    temp.out = UpdateManager.Direction.north;
                    break;
                case south:
                    temp.out = UpdateManager.Direction.east;
                    break;
                case west:
                    temp.out = UpdateManager.Direction.south;
                    break;
            }
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
        if (greenLights[UpdateManager.Direction.north.ordinal()]) {
            lights.add(UpdateManager.Direction.east);
            lights.add(UpdateManager.Direction.west);
        } else {
            lights.add(UpdateManager.Direction.north);
            lights.add(UpdateManager.Direction.south);
        }
        setGreenLights(lights.toArray(new UpdateManager.Direction[0]));
    }

    @Override
    void updateGreenLightsPressure() {
        List<UpdateManager.Direction> lights = new ArrayList<>();
        double greenPressure = 0;
        double redPressure = 0;
        if (greenLights[UpdateManager.Direction.north.ordinal()]) {
            greenPressure = inRoads[UpdateManager.directionToInt(UpdateManager.Direction.north)].getTotalPressure() + inRoads[UpdateManager.directionToInt(UpdateManager.Direction.south)].getTotalPressure();
            redPressure = inRoads[UpdateManager.directionToInt(UpdateManager.Direction.east)].getTotalPressure() + inRoads[UpdateManager.directionToInt(UpdateManager.Direction.west)].getTotalPressure();
            redPressure += previousRedLightPressure[UpdateManager.directionToInt(UpdateManager.Direction.east)] + previousRedLightPressure[UpdateManager.directionToInt(UpdateManager.Direction.west)];
            if (greenPressure < redPressure) {
                lights.add(UpdateManager.Direction.east);
                lights.add(UpdateManager.Direction.west);
            } else {
                lights.add(UpdateManager.Direction.north);
                lights.add(UpdateManager.Direction.south);
            }
        } else {
            greenPressure = inRoads[UpdateManager.directionToInt(UpdateManager.Direction.east)].getTotalPressure() + inRoads[UpdateManager.directionToInt(UpdateManager.Direction.west)].getTotalPressure();
            redPressure = inRoads[UpdateManager.directionToInt(UpdateManager.Direction.north)].getTotalPressure() + inRoads[UpdateManager.directionToInt(UpdateManager.Direction.south)].getTotalPressure();
            redPressure += previousRedLightPressure[UpdateManager.directionToInt(UpdateManager.Direction.north)] + previousRedLightPressure[UpdateManager.directionToInt(UpdateManager.Direction.south)];
            if (greenPressure < redPressure) {
                lights.add(UpdateManager.Direction.north);
                lights.add(UpdateManager.Direction.south);
            } else {
                lights.add(UpdateManager.Direction.east);
                lights.add(UpdateManager.Direction.west);
            }
        }
        updateRedLightPressure(lights);
        setGreenLights(lights.toArray(new UpdateManager.Direction[0]));
    }

    @Override
    void updateGreenLights(boolean firstTime, boolean pressureSystem){
        super.updateGreenLights(firstTime, pressureSystem);
        if(stepCountdown > 0){
            return;
        }
        if (firstTime) {
            List<UpdateManager.Direction> lights = new ArrayList<>();
            if(quantumGenerator.getNextBoolean(2)){
                lights.add(UpdateManager.Direction.north);
                lights.add(UpdateManager.Direction.south);
            }
            else{
                lights.add(UpdateManager.Direction.east);
                lights.add(UpdateManager.Direction.west);
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
