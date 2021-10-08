package com.trafficAnalysis;

import java.util.ArrayList;
import java.util.List;

public class Intersection4Way extends Intersection{

    Intersection4Way(IntersectionBuilder builder, QuantumGenerator qg) {
        super(builder,qg);
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
        if (quantumGenerator.getNextBoolean()) { // First, go straight
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
        } else if (quantumGenerator.getNextBoolean()) { // Second, left turn
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
        if (firstTime) {
            if(quantumGenerator.getNextBoolean()){
                lights.add(UpdateManager.Direction.north);
                lights.add(UpdateManager.Direction.south);
            }
            else{
                lights.add(UpdateManager.Direction.east);
                lights.add(UpdateManager.Direction.west);
            }
        } else {
            if (greenLights[UpdateManager.Direction.north.ordinal()]) {
                lights.add(UpdateManager.Direction.east);
                lights.add(UpdateManager.Direction.west);
            } else {
                lights.add(UpdateManager.Direction.north);
                lights.add(UpdateManager.Direction.south);
            }
        }
        setGreenLights(lights.toArray(new UpdateManager.Direction[0]));
    }

}