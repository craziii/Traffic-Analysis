package com.trafficAnalysis;

import java.util.ArrayList;
import java.util.List;

public class Intersection3Way extends Intersection{

    Intersection3Way(IntersectionBuilder builder, QuantumGenerator qg) {
        super(builder,qg);
    }

    @Override
    UpdateManager.IntersectionMove getNextIntersectionOutput(QuantumGenerator quantumGenerator) {
        UpdateManager.IntersectionMove temp = super.getNextIntersectionOutput(quantumGenerator);
        List<UpdateManager.Direction> options = new ArrayList<>();
        for(int i = 0; i < inRoads.length; i++){
            if(inRoads[i] != null){
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
        for(int i = 0; i < directions.length; i++){
            if(temp.in == directions[i]){
                offset = i;
            }
        }
        if(quantumGenerator.getNextBoolean(0)){ // First, next clockwise option
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
            carsInIntersection.remove();
            return temp;
        }
        return null;
    }

    @Override
    void updateGreenLightsNormal() {
        List<UpdateManager.Direction> lights = new ArrayList<>();
        //TODO: ADD CODE FOR DECIDING THE OUTPUT OF 3 WAY INTERSECTIONS
        int currentOutput = -1;
        for (int i = 0; i < greenLights.length; i++) {
            if (greenLights[i]) {
                currentOutput = i;
            }
        }
        switch (currentOutput) {
            case 0:
                if (quantumGenerator.getNextBoolean(2)) {
                    lights.add(outputDirections[1]);
                } else {
                    lights.add(outputDirections[2]);
                }
                break;
            case 1:
                if (quantumGenerator.getNextBoolean(2)) {
                    lights.add(outputDirections[0]);
                } else {
                    lights.add(outputDirections[2]);
                }
                break;
            case 2:
                if (quantumGenerator.getNextBoolean(2)) {
                    lights.add(outputDirections[0]);
                } else {
                    lights.add(outputDirections[1]);
                }
                break;
        }
        setGreenLights(lights.toArray(new UpdateManager.Direction[0]));
    }

    @Override
    void updateGreenLightsPressure(){
        List<UpdateManager.Direction> lights = new ArrayList<>();
        UpdateManager.Direction currentOutput = UpdateManager.Direction.none;
        for (int i = 0; i < greenLights.length; i++) {
            if (greenLights[i]) {
                currentOutput = UpdateManager.intToDirection(i);
            }
        }
        if(currentOutput == UpdateManager.Direction.none){
            currentOutput = outputDirections[0];
            greenLights[outputDirections[0].ordinal()] = true;
        }
        double[] pressures = {0,0,0};
        UpdateManager.Direction[] possibleOutputs = {UpdateManager.Direction.none, UpdateManager.Direction.none, UpdateManager.Direction.none};
        pressures[0] = inRoads[currentOutput.ordinal()].getTotalPressure();
        possibleOutputs[0] = currentOutput;
        List<UpdateManager.Direction> otherOutputDirections = new ArrayList<>();
        for(UpdateManager.Direction dir:outputDirections){
            if(dir != currentOutput){
                otherOutputDirections.add(dir);
            }
        }
        UpdateManager.Direction[] otherOutputDirectionsArray = otherOutputDirections.toArray(new UpdateManager.Direction[0]);
        for(int i = 1; i < 3; i++){
            pressures[i] = inRoads[otherOutputDirectionsArray[i-1].ordinal()].getTotalPressure() + previousRedLightPressure[otherOutputDirectionsArray[i-1].ordinal()];
            possibleOutputs[i] = otherOutputDirectionsArray[i-1];
        }
        double maxPressure = -1;
        UpdateManager.Direction output = UpdateManager.Direction.none;
        for(int i = 0; i < 3; i++){
            if(pressures[i] > maxPressure){
                maxPressure = pressures[i];
            }
            output = possibleOutputs[i];
        }
        lights.add(output);
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
            Util.Logging.log("Intersection ["+this.getUuid()+"] running first time setup", Util.Logging.LogLevel.INFO);
            List<UpdateManager.Direction> lights = new ArrayList<>();
            if (quantumGenerator.getNextBoolean(3)) {
                lights.add(outputDirections[0]);
            } else if (quantumGenerator.getNextBoolean(2)) {
                lights.add(outputDirections[1]);
            } else {
                lights.add(outputDirections[2]);
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
