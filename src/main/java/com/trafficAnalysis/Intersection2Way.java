package com.trafficAnalysis;

import java.util.ArrayList;
import java.util.List;

public class Intersection2Way extends Intersection{

    Intersection2Way(IntersectionBuilder builder) {
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
        switch(temp.in) {
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
