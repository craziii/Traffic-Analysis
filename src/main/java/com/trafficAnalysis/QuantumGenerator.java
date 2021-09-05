package com.trafficAnalysis;
import org.redfx.strange.local.SimpleQuantumExecutionEnvironment;

public class QuantumGenerator {

    SimpleQuantumExecutionEnvironment sqee;

    QuantumGenerator(){
        sqee = new SimpleQuantumExecutionEnvironment();
    }

    float getNextFloat(){
        return Float.MAX_VALUE;
    }

}
