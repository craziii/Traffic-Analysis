package com.trafficAnalysis;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class GridBuilder {

    public static String DEFAULT_MAPFILE = "Map.csv";

    File mapFile;
    GridManager gridManager;
    Map<int[], UUID> worldMap;
    Map<int[], Util.WorldBuilderUtil.DirNum[]> intersectionMapping;

    GridBuilder(GridManager gm){
        setup(gm,DEFAULT_MAPFILE);
    }

    GridBuilder(GridManager gm, String filename){
        setup(gm,filename);
    }

    void setup(GridManager gm, String filename){
        mapFile = new File(filename);
        gridManager = gm;
        worldMap = new HashMap<>();
        intersectionMapping = new HashMap<>();
        if(!mapFile.exists()){
            Util.Logging.log("No mapfile found with name [" + mapFile.getName()+"] please check that this is the correct mapfile before restarting. Exiting.", Util.Logging.LogLevel.CRITICAL);
            System.exit(0);
        }
    }

    void fileToIntersectionMapping(){
        String[] fileLines = Util.FileManager.readFile(mapFile);
        for(int i = 0; i < Objects.requireNonNull(fileLines).length; i++){
            String[] parts = Util.WorldBuilderUtil.lineToParts(fileLines[i]);
            for(int j = 0; j < parts.length; j++){
                Util.WorldBuilderUtil.DirNum[] dirNums = Util.WorldBuilderUtil.stringToDirNumArray(parts[j]);
                intersectionMapping.put(new int[]{i,j},dirNums);
            }
        }
    }

    void intersectionMappingToWorldMap(){
        //TODO: STEP 1 - FIND THE DIRECTIONS OF INTERSECTION AT EACH POINT

        //TODO: STEP 2 - CREATE THE ROADS NECESSARY

        //TODO: STEP 3 - CREATE AND LINK INTERSECTIONS WITH ROADS

        //TODO: STEP 4 - CALL COMPLETION
        gridManager.onCreationComplete();
    }



}
