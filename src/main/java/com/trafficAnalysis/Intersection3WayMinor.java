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
        List<UpdateManager.Direction> options = new ArrayList<>();
        for (int i = 0; i < inRoads.length; i++) {
            if (inRoads[i] != null) {
                switch (i) {
                    case 0:
                        options.add(UpdateManager.Direction.north);
                        break;
                    case 1:
                        options.add(UpdateManager.Direction.east);
                        break;
                    case 2:
                        options.add(UpdateManager.Direction.south);
                        break;
                    case 3:
                        options.add(UpdateManager.Direction.west);
                        break;
                }
            }
        }
        UpdateManager.Direction[] directions = options.toArray(new UpdateManager.Direction[0]);
        int offset = -3;
        for (int i = 0; i < directions.length; i++) {
            if (temp.in == directions[i]) {
                offset = i;
            }
        }
        if (quantumGenerator.getNextBoolean(0)) { // First, next clockwise option
            switch (offset) {
                case 0:
                    temp.out = directions[1];
                    break;
                case 1:
                    temp.out = directions[2];
                    break;
                case 2:
                    temp.out = directions[0];
                    break;
            }
        } else { // Else, other option
            switch (offset) {
                case 0:
                    temp.out = directions[2];
                    break;
                case 1:
                    temp.out = directions[0];
                    break;
                case 2:
                    temp.out = directions[1];
                    break;
            }
        }
        if (validIntersectionOutput(temp.out)) {
            return temp;
        } else {
            carsInIntersection.add(tempInput);
        }
        return null;
    }

    @Override
    void updateGreenLights(boolean firstTime) {
        if (stepCountdown > 0) {
            stepCountdown--;
            return;
        }
        List<UpdateManager.Direction> lights = new ArrayList<>();
        if (firstTime) {
            int northSouthCount = 0;
            List<UpdateManager.Direction> outputDirs = new ArrayList<>();
            for(int i = 0; i < inRoads.length; i++){
                if(inRoads[i] != null){
                    outputDirs.add(UpdateManager.Direction.values()[i]);
                    if(i == 0 || i == 2){
                        northSouthCount++;
                    }
                }
            }
            outputDirections = outputDirs.toArray(new UpdateManager.Direction[0]);
            northSouthMajor = northSouthCount == 2;
            if(northSouthMajor){
                for(UpdateManager.Direction dir: outputDirections){
                    if(dir != UpdateManager.Direction.north && dir != UpdateManager.Direction.south){
                        minorRoad = dir;
                    }
                }
                if(Math.random() > 0.5){
                    lights.add(UpdateManager.Direction.north);
                    lights.add(UpdateManager.Direction.south);
                }
                else{
                    lights.add(minorRoad);
                }
            }
            else{
                if(Math.random() > 0.5){
                    lights.add(UpdateManager.Direction.east);
                    lights.add(UpdateManager.Direction.west);
                }
                else{
                    lights.add(minorRoad);
                }
            }
        } else {
            if(greenLights[minorRoad.ordinal()]){
                if(northSouthMajor){
                    lights.add(UpdateManager.Direction.north);
                    lights.add(UpdateManager.Direction.south);
                }
                else{
                    lights.add(UpdateManager.Direction.east);
                    lights.add(UpdateManager.Direction.west);
                }
            }
            else{
                lights.add(minorRoad);
            }
        }
        setGreenLights(lights.toArray(new UpdateManager.Direction[0]));
    }

}
