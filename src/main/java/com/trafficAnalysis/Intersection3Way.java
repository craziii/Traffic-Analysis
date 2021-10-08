package com.trafficAnalysis;

import java.util.ArrayList;
import java.util.List;

public class Intersection3Way extends Intersection{

    Intersection3Way(IntersectionBuilder builder) {
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
        List<UpdateManager.IntersectionMoveEnum> options = new ArrayList<>();
        for(int i = 0; i < inRoads.length; i++){
            if(inRoads[i] != null){
                switch (i) {
                    case 0:
                        options.add(UpdateManager.IntersectionMoveEnum.north);
                        break;
                    case 1:
                        options.add(UpdateManager.IntersectionMoveEnum.east);
                        break;
                    case 2:
                        options.add(UpdateManager.IntersectionMoveEnum.south);
                        break;
                    case 3:
                        options.add(UpdateManager.IntersectionMoveEnum.west);
                        break;
                }
            }
        }
        UpdateManager.IntersectionMoveEnum[] directions = options.toArray(new UpdateManager.IntersectionMoveEnum[0]);
        int offset = -3;
        for(int i = 0; i < directions.length; i++){
            if(temp.in == directions[i]){
                offset = i;
            }
        }
        if(quantumGenerator.getNextBoolean()){ // First, next clockwise option
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
        }
        else{ // Else, other option
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
