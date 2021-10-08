package com.trafficAnalysis;

public class Intersection4Way extends Intersection{

    Intersection4Way(IntersectionBuilder builder) {
        super(builder);
    }

    @Override
    UpdateManager.IntersectionMove getNextIntersectionOutput(QuantumGenerator quantumGenerator) {
        CarInput tempInput = carsInIntersection.remove();
        UpdateManager.IntersectionMove temp = new UpdateManager.IntersectionMove(this, UpdateManager.IntersectionMoveEnum.none, UpdateManager.IntersectionMoveEnum.none);
        switch (tempInput) {
            case north:
                temp.in = UpdateManager.IntersectionMoveEnum.north;
                break;
            case east:
                temp.in = UpdateManager.IntersectionMoveEnum.east;
                break;
            case south:
                temp.in = UpdateManager.IntersectionMoveEnum.south;
                break;
            case west:
                temp.in = UpdateManager.IntersectionMoveEnum.west;
                break;
        }
        if (quantumGenerator.getNextBoolean()) { // First, go straight
            switch (temp.in) {
                case north:
                    temp.out = UpdateManager.IntersectionMoveEnum.south;
                    break;
                case east:
                    temp.out = UpdateManager.IntersectionMoveEnum.west;
                    break;
                case south:
                    temp.out = UpdateManager.IntersectionMoveEnum.north;
                    break;
                case west:
                    temp.out = UpdateManager.IntersectionMoveEnum.east;
                    break;
            }
        } else if (quantumGenerator.getNextBoolean()) { // Second, left turn
            switch (temp.in) {
                case north:
                    temp.out = UpdateManager.IntersectionMoveEnum.east;
                    break;
                case east:
                    temp.out = UpdateManager.IntersectionMoveEnum.south;
                    break;
                case south:
                    temp.out = UpdateManager.IntersectionMoveEnum.west;
                    break;
                case west:
                    temp.out = UpdateManager.IntersectionMoveEnum.north;
                    break;
            }
        } else { // Finally, right turn
            switch (temp.in) {
                case north:
                    temp.out = UpdateManager.IntersectionMoveEnum.west;
                    break;
                case east:
                    temp.out = UpdateManager.IntersectionMoveEnum.north;
                    break;
                case south:
                    temp.out = UpdateManager.IntersectionMoveEnum.east;
                    break;
                case west:
                    temp.out = UpdateManager.IntersectionMoveEnum.south;
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
        if(firstTime){

        }
        else{

        }
    }

}
