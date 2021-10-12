package com.trafficAnalysis;
import org.redfx.strange.Program;
import org.redfx.strange.Qubit;
import org.redfx.strange.Result;
import org.redfx.strange.Step;
import org.redfx.strange.local.SimpleQuantumExecutionEnvironment;

public class QuantumGenerator {

    public static double DEFAULT_INTERSECTION_CHANCE = 0.5;
    public static double DEFAULT_CAR_CHANCE = 0.2;

    SimpleQuantumExecutionEnvironment sqee;
    Program[] programs;

    public QuantumGenerator(){
        setup(chanceToAngle(DEFAULT_INTERSECTION_CHANCE),chanceToAngle(DEFAULT_CAR_CHANCE));
    }

    public QuantumGenerator(double intersectionChance, double carChance){
        setup(chanceToAngle(intersectionChance),chanceToAngle(carChance));
    }

    double chanceToAngle(double chance){
        return Math.asin((chance*2)-1)+(Math.PI/2);
    }

    void setup(double angleIntersection, double angleCar){
        sqee = new SimpleQuantumExecutionEnvironment();
        programs = new Program[2];
        createQuantumBooleanCircuits(angleIntersection,angleCar);
    }

    private void createQuantumBooleanCircuits(double angleIntersection, double angleCar) {
        programs[0] = new Program(1);
        Step step = new Step(new Rotation(angleIntersection, Rotation.Axes.XAxis,0));
        programs[0].addStep(step);
        programs[1] = new Program(1);
        step = new Step(new Rotation(angleCar, Rotation.Axes.XAxis,0));
        programs[1].addStep(step);
    }


    public boolean getNextBoolean(int circuit){
        return getNextBoolean(circuit,0);
    }

    public boolean getNextBoolean(int circuit,int count){
        try {
            Result result = sqee.runProgram(programs[circuit]);
            Qubit qubit = result.getQubits()[0];
            switch (qubit.measure()) {
                case 0:
                    return false;
                case 1:
                    return true;
            }
        }
        catch(Exception e){
            //e.printStackTrace();
        }
        if(count > 5){
            return false;
        }
        else{
            return getNextBoolean(circuit,count+1);
        }
    }

    public float getNextFloat(){
        return Float.MAX_VALUE;
    }
}
