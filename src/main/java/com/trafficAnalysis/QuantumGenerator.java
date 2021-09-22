package com.trafficAnalysis;
import org.redfx.strange.Program;
import org.redfx.strange.Qubit;
import org.redfx.strange.Result;
import org.redfx.strange.local.SimpleQuantumExecutionEnvironment;

public class QuantumGenerator {

    SimpleQuantumExecutionEnvironment sqee;
    Program programBoolean;
    Program programFloat;

    QuantumGenerator(){
        sqee = new SimpleQuantumExecutionEnvironment();
        createQuantumBooleanCircuit();
        createQuantumFloatCircuit();
    }

    private void createQuantumBooleanCircuit() {
        programBoolean = new Program(1);
    }

    private void createQuantumFloatCircuit() {
        programFloat = new Program(4);
    }

    public boolean getNextBoolean(){
        Result result = sqee.runProgram(programBoolean);
        Qubit qubit = result.getQubits()[0];
        switch (qubit.measure()){
            case 0: return false;
            case 1: return true;
        }
        return false;
    }

    public float getNextFloat(){
        return Float.MAX_VALUE;
    }
}
