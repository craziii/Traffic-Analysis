package com.trafficAnalysis;

import java.util.ArrayList;
import java.util.List;

public class Intersection3WayMinor extends Intersection {

    boolean northSouthMajor;
    UpdateManager.Direction minorRoad;

    Intersection3WayMinor(IntersectionBuilder builder, QuantumGenerator qg) {
        super(builder, qg);
    }

    @Override
    UpdateManager.IntersectionMove getNextIntersectionOutput(QuantumGenerator quantumGenerator) {
        UpdateManager.IntersectionMove temp = super.getNextIntersectionOutput(quantumGenerator);
        if(temp.in == minorRoad){
            if(northSouthMajor){
                if(quantumGenerator.getNextBoolean(0)){
                    switch (minorRoad){
                        case east: temp.out = UpdateManager.Direction.south;
                        case west: temp.out = UpdateManager.Direction.north;
                    }
                }
                else{
                    switch (minorRoad){
                        case east: temp.out = UpdateManager.Direction.north;
                        case west: temp.out = UpdateManager.Direction.south;
                    }
                }
            }
            else{
                if(quantumGenerator.getNextBoolean(0)){
                    switch (minorRoad){
                        case north: temp.out = UpdateManager.Direction.east;
                        case south: temp.out = UpdateManager.Direction.west;
                    }
                }
                else{
                    switch (minorRoad){
                        case north: temp.out = UpdateManager.Direction.west;
                        case south: temp.out = UpdateManager.Direction.east;
                    }
                }
            }
        }
        else{
            if(quantumGenerator.getNextBoolean(0)){
                if(northSouthMajor){
                    switch (temp.in){
                        case north: temp.out = UpdateManager.Direction.south;
                        case south: temp.out = UpdateManager.Direction.north;
                    }
                }
                else{
                    switch (temp.in){
                        case east: temp.out = UpdateManager.Direction.west;
                        case west: temp.out = UpdateManager.Direction.east;
                    }
                }
            }
            else{
                temp.out = minorRoad;
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
        if (greenLights[minorRoad.ordinal()]) {
            if (northSouthMajor) {
                lights.add(UpdateManager.Direction.north);
                lights.add(UpdateManager.Direction.south);
            } else {
                lights.add(UpdateManager.Direction.east);
                lights.add(UpdateManager.Direction.west);
            }
        } else {
            lights.add(minorRoad);
        }
        setGreenLights(lights.toArray(new UpdateManager.Direction[0]));
    }

    @Override
    void updateGreenLightsPressure(){
        List<UpdateManager.Direction> lights = new ArrayList<>();
        double greenPressure = 0;
        double redPressure = 0;
        if (greenLights[minorRoad.ordinal()]) {
            greenPressure = inRoads[minorRoad.ordinal()].getTotalPressure();
            if(northSouthMajor){
                redPressure = inRoads[UpdateManager.directionToInt(UpdateManager.Direction.north)].getTotalPressure() + inRoads[UpdateManager.directionToInt(UpdateManager.Direction.south)].getTotalPressure();
                redPressure += previousRedLightPressure[UpdateManager.directionToInt(UpdateManager.Direction.north)] + previousRedLightPressure[UpdateManager.directionToInt(UpdateManager.Direction.south)];
            }
            else{
                redPressure = inRoads[UpdateManager.directionToInt(UpdateManager.Direction.east)].getTotalPressure() + inRoads[UpdateManager.directionToInt(UpdateManager.Direction.west)].getTotalPressure();
                redPressure += previousRedLightPressure[UpdateManager.directionToInt(UpdateManager.Direction.east)] + previousRedLightPressure[UpdateManager.directionToInt(UpdateManager.Direction.west)];
            }
            if (greenPressure < redPressure) {
                if(northSouthMajor){
                    lights.add(UpdateManager.Direction.north);
                    lights.add(UpdateManager.Direction.south);
                }
                else{
                    lights.add(UpdateManager.Direction.east);
                    lights.add(UpdateManager.Direction.west);
                }
            } else {
                lights.add(minorRoad);
            }
        } else {
            if(northSouthMajor){
                greenPressure = inRoads[UpdateManager.directionToInt(UpdateManager.Direction.north)].getTotalPressure() + inRoads[UpdateManager.directionToInt(UpdateManager.Direction.south)].getTotalPressure();
            }
            else{
                greenPressure = inRoads[UpdateManager.directionToInt(UpdateManager.Direction.east)].getTotalPressure() + inRoads[UpdateManager.directionToInt(UpdateManager.Direction.west)].getTotalPressure();
            }
            redPressure = inRoads[minorRoad.ordinal()].getTotalPressure();
            redPressure += previousRedLightPressure[minorRoad.ordinal()];
            if (greenPressure < redPressure) {
                lights.add(minorRoad);
            } else {
                if(northSouthMajor){
                    lights.add(UpdateManager.Direction.north);
                    lights.add(UpdateManager.Direction.south);
                }
                else{
                    lights.add(UpdateManager.Direction.east);
                    lights.add(UpdateManager.Direction.west);
                }
            }
        }
        updateRedLightPressure(lights);
        setGreenLights(lights.toArray(new UpdateManager.Direction[0]));
    }

    @Override
    void updateGreenLights(boolean firstTime, boolean pressureSystem) {
        super.updateGreenLights(firstTime, pressureSystem);
        if(stepCountdown > 0){
            return;
        }
        if (firstTime) {
            List<UpdateManager.Direction> lights = new ArrayList<>();
            int northSouthCount = 0;
            for (int i = 0; i < inRoads.length; i++) {
                if (inRoads[i] != null) {
                    if (i == 0 || i == 2) {
                        northSouthCount++;
                    }
                }
            }
            northSouthMajor = northSouthCount == 2;
            if (northSouthMajor) {
                for (UpdateManager.Direction dir : outputDirections) {
                    if (dir != UpdateManager.Direction.north && dir != UpdateManager.Direction.south) {
                        minorRoad = dir;
                    }
                }
                if (quantumGenerator.getNextBoolean(2)) {
                    lights.add(UpdateManager.Direction.north);
                    lights.add(UpdateManager.Direction.south);
                } else {
                    lights.add(minorRoad);
                }
            } else {
                if (quantumGenerator.getNextBoolean(2)) {
                    lights.add(UpdateManager.Direction.east);
                    lights.add(UpdateManager.Direction.west);
                } else {
                    lights.add(minorRoad);
                }
            }
            setGreenLights(lights.toArray(new UpdateManager.Direction[0]));
        }
        if (pressureSystem && !firstTime) {
            updateGreenLightsPressure();
        } else {
            updateGreenLightsNormal();
        }
    }

}
