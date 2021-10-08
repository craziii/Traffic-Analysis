package com.trafficAnalysis;
import org.redfx.strange.Program;
import org.redfx.strange.Qubit;
import org.redfx.strange.Result;
import org.redfx.strange.Step;
import org.redfx.strange.local.SimpleQuantumExecutionEnvironment;

public class QuantumGenerator {

    public static double DEFAULT_CHANCE = 0.5f;

    SimpleQuantumExecutionEnvironment sqee;
    Program programBoolean;
    Program programFloat;

    QuantumGenerator(){
        setup(chanceToAngle(DEFAULT_CHANCE));
    }

    QuantumGenerator(double chance){
        setup(chanceToAngle(chance));
    }

    double chanceToAngle(double chance){
        return Math.asin((chance*2)-1)+(Math.PI/2);
    }

    void setup(double angle){
        sqee = new SimpleQuantumExecutionEnvironment();
        createQuantumBooleanCircuit(angle);
        createQuantumFloatCircuit();
    }

    private void createQuantumBooleanCircuit(double angle) {
        programBoolean = new Program(1);
        Step step = new Step(new Rotation(angle, Rotation.Axes.XAxis,0));
        programBoolean.addStep(step);
    }

    private void createQuantumFloatCircuit() {
        programFloat = new Program(4);
    }

    public boolean getNextBoolean(){
        return getNextBoolean(0);
    }

    public boolean getNextBoolean(int count){
        try {
            Result result = sqee.runProgram(programBoolean);
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
            return getNextBoolean(count+1);
        }
    }

    public float getNextFloat(){
        return Float.MAX_VALUE;
    }
}
