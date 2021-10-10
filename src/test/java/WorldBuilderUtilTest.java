import com.trafficAnalysis.UpdateManager;
import com.trafficAnalysis.Util;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WorldBuilderUtilTest {

    @Test
    void stringToDirNumTest(){
        String s = "n-10";
        Util.WorldBuilderUtil.DirNum dirNum = Util.WorldBuilderUtil.stringToDirNum(s);
        assertSame(dirNum.getDir(), UpdateManager.Direction.north);
        assertEquals(10, dirNum.getNum());
    }

    @Test
    void dirNumToStringTest(){
        Util.WorldBuilderUtil.DirNum dirNum = new Util.WorldBuilderUtil.DirNum(UpdateManager.Direction.north,10);
        assertEquals("n-10", Util.WorldBuilderUtil.dirNumToString(dirNum));
    }

    @Test
    void dirNumArrayToStringTest(){
        List<Util.WorldBuilderUtil.DirNum> dirNumList = new ArrayList<>();
        dirNumList.add(new Util.WorldBuilderUtil.DirNum(UpdateManager.Direction.north,10));
        dirNumList.add(new Util.WorldBuilderUtil.DirNum(UpdateManager.Direction.east,5));
        dirNumList.add(new Util.WorldBuilderUtil.DirNum(UpdateManager.Direction.south,7));
        dirNumList.add(new Util.WorldBuilderUtil.DirNum(UpdateManager.Direction.west,5));
        String output = Util.WorldBuilderUtil.dirNumArrayToString(dirNumList.toArray(new Util.WorldBuilderUtil.DirNum[0]));
        assertEquals("n-10:e-5:s-7:w-5", output);
    }

    @Test
    void stringToDirNumArrayTest(){
        String s = "n-10:e-5:s-7:w-5";
        Util.WorldBuilderUtil.DirNum[] dirNums = Util.WorldBuilderUtil.stringToDirNumArray(s);
        assertSame(dirNums[0].getDir(), UpdateManager.Direction.north);
        assertEquals(10, dirNums[0].getNum());
        assertSame(dirNums[1].getDir(), UpdateManager.Direction.east);
        assertEquals(5, dirNums[1].getNum());
        assertSame(dirNums[2].getDir(), UpdateManager.Direction.south);
        assertEquals(7, dirNums[2].getNum());
        assertSame(dirNums[3].getDir(), UpdateManager.Direction.west);
        assertEquals(5, dirNums[3].getNum());
    }

    @Test
    void lineToDirNumsAndBackTest(){
        String line = "n-5:s-7,w-13:e-15:n-9,n-1:w-5:e-2,s-10";
        String[] lineParts = Util.WorldBuilderUtil.lineToParts(line);
        List<Util.WorldBuilderUtil.DirNum[]> dirNumsArrayList = new ArrayList<>();
        for(String part:lineParts){
            dirNumsArrayList.add(Util.WorldBuilderUtil.stringToDirNumArray(part));
        }
        dirNumsArrayList.toArray(new Util.WorldBuilderUtil.DirNum[0][0]);
        List<String> partsList = new ArrayList<>();
        for(Util.WorldBuilderUtil.DirNum[] dirNums:dirNumsArrayList){
            partsList.add(Util.WorldBuilderUtil.dirNumArrayToString(dirNums));
        }
        String[] parts = partsList.toArray(new String[0]);
        String newLine = Util.WorldBuilderUtil.partsToLine(parts);
        assertEquals(newLine, line);
    }

}
