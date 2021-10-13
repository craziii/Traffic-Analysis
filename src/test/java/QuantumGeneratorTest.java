import com.trafficAnalysis.QuantumGenerator;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuantumGeneratorTest{

    @Test
    void quantumRNGTest(){
        float testLimit = 0.01f;
        int testCount = 100000;
        Map<Double,Boolean[]> booleanMap = new HashMap<>();
        for(double c = 0; c <= 1; c+=0.05){
            booleanMap.put(c,quantumRNGTest(c,testCount));
        }
        Map<Double,Integer> results = new HashMap<>();
        for(double c:booleanMap.keySet()){
            results.put(c,countTrue(booleanMap.get(c)));
        }
        for(double c: results.keySet()){
            assertTrue(results.get(c) >= (testCount * c) - testCount*testLimit && results.get(c) <= (testCount * c) + testCount*testLimit);
        }
    }

    Boolean[] quantumRNGTest(double chance, int count){
        QuantumGenerator qg = new QuantumGenerator(chance,chance);
        List<Boolean> bools = new ArrayList<>();
        for(int i = 0; i < count; i++){
            bools.add(qg.getNextBoolean(0));
        }
        return bools.toArray(bools.toArray(new Boolean[0]));
    }

    int countTrue(Boolean[] bools){
        int output = 0;
        for(boolean b:bools){
            if(b){
                output++;
            }
        }
        return output;
    }

}
