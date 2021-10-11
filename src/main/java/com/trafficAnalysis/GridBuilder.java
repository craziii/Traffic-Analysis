package com.trafficAnalysis;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class GridBuilder {

    public static String DEFAULT_MAPFILE = "Map.csv";

    File mapFile;
    GridManager gridManager;
    Map<int[], UUID[]> worldMap;
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
        //TODO: STEP 1 - FIND THE SIZE OF THE MAP
        int maxWidth = -1;
        int maxHeight = -1;
        for(int[] keys:intersectionMapping.keySet()){
            if(keys[0] > maxWidth){
                maxWidth = keys[0];
            }
            if(keys[1] > maxHeight){
                maxHeight = keys[1];
            }
        }
        //TODO: STEP 2 - FIND THE DIRECTIONS OF INTERSECTION AT EACH POINT
        for(int i = 0; i < maxWidth; i++){
            for(int j = 0; j < maxHeight; j++) {
                Util.WorldBuilderUtil.DirNum[] dirNums = intersectionMapping.get(new int[]{i, j});
                int[] worldLocation = new int[]{(i * 2) + 1, (j * 2) + 1};
                //TODO: STEP 3 - CREATE THE ROADS NECESSARY
                for(Util.WorldBuilderUtil.DirNum dirNum:dirNums){
                    Road[] newRoadPair = createRoadPair(getNewLocation(worldLocation,dirNum.dir),dirNum.num);
                    if(newRoadPair != null){
                        worldMap.put(getNewLocation(worldLocation, dirNum.dir),new UUID[]{newRoadPair[0].getUuid(),newRoadPair[1].getUuid()});
                        Util.Logging.log("Created RoadPair ["+newRoadPair[0].getUuid()+","+newRoadPair[1].getUuid()+"] at worldmap location ["+getNewLocation(worldLocation,dirNum.dir)[0]+","+getNewLocation(worldLocation,dirNum.dir)[1]+"]", Util.Logging.LogLevel.INFO);
                    }
                }
            }
        }
        //TODO: STEP 5 - CREATE AND LINK INTERSECTIONS WITH ROADS
        for(int i = 0; i < maxWidth; i++) {
            for (int j = 0; j < maxHeight; j++) {
                int[] worldLocation = new int[]{(i * 2) + 1, (j * 2) + 1};
                Road[] roads = new Road[8];
                for(int count = 0; count < 4; count++){
                    int[] roadLocation = getNewLocation(worldLocation,UpdateManager.intToDirection(count));
                    UUID[] uuids = new UUID[2];
                    if(worldMap.containsKey(roadLocation)){
                        uuids = worldMap.get(roadLocation);
                    }
                    if(uuids != null){
                        roads[count] = gridManager.getRoad(uuids[0]);
                        roads[count+4] = gridManager.getRoad(uuids[1]);
                    }
                    else{
                        roads[count] = null;
                        roads[count+4] = null;
                    }
                }
                Intersection intersection = gridManager.createIntersection(roads);
                worldMap.put(worldLocation,new UUID[]{intersection.getUuid()});
                Util.Logging.log("Created Intersection ["+intersection.getUuid()+"] at worldmap location ["+worldLocation[0]+","+worldLocation[1]+"]", Util.Logging.LogLevel.INFO);
            }
        }
        if(Main.outputMapToFile){
            writeMapToFile();
        }
    }

    void writeMapToFile(){
        int maxWidth = -1;
        int maxHeight = -1;
        for(int[] keys:worldMap.keySet()){
            if(keys[0] > maxWidth){
                maxWidth = keys[0];
            }
            if(keys[1] > maxHeight){
                maxHeight = keys[1];
            }
        }
        List<String> lines = new ArrayList<>();
        for(int i = 0; i < maxWidth; i++){
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < maxHeight; j++){
                if(worldMap.containsKey(new int[]{i,j})){
                    UUID uuid = worldMap.get(new int[]{i,j})[0];
                    String output;
                    if(gridManager.updateManager.roadMap.containsKey(uuid)){
                        output = "r";
                    }
                    else if(gridManager.updateManager.intersectionMap.containsKey(uuid)){
                        output = "i";
                    }
                    else{
                        output = "x";
                    }
                    sb.append(output);
                    sb.append(Util.FileManager.DEFAULT_DELIMITER);
                }
            }
            lines.add(sb.toString());
        }
        Util.FileManager.writeFile("map-"+Util.Logging.getTimestamp(),lines.toArray(new String[0]),false);
    }

    Road[] createRoadPair(int[] location, int nodes){
        if(!worldMap.containsKey(location)){
            Road in = gridManager.createRoad(nodes);
            Road out = gridManager.createRoad(nodes);
            return new Road[]{in,out};
        }
        return null;
    }

    int[] getNewLocation(int[] location, UpdateManager.Direction direction){
        int[] newLocation = new int[2];
        switch(direction) {
            case north:
                newLocation[0] = location[0];
                newLocation[1] = location[1] - 1;
                break;
            case east:
                newLocation[0] = location[0] + 1;
                newLocation[1] = location[1];
                break;
            case south:
                newLocation[0] = location[0];
                newLocation[1] = location[1] + 1;
                break;
            case west:
                newLocation[0] = location[0] - 1;
                newLocation[1] = location[1];
                break;
        }
        return newLocation;
    }

}
