package com.trafficAnalysis;

import org.apache.commons.lang3.tuple.ImmutablePair;

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
    Map<ImmutablePair<Integer, Integer>, UUID[]> worldMap;
    Map<ImmutablePair<Integer, Integer>, Util.WorldBuilderUtil.DirNum[]> intersectionMapping;

    GridBuilder(GridManager gm) {
        setup(gm, DEFAULT_MAPFILE);
    }

    GridBuilder(GridManager gm, String filename) {
        setup(gm, filename);
    }

    void setup(GridManager gm, String filename) {
        mapFile = new File(filename);
        gridManager = gm;
        worldMap = new HashMap<>();
        intersectionMapping = new HashMap<>();
        if (!mapFile.exists()) {
            Util.Logging.log("No mapfile found with name [" + mapFile.getName() + "] please check that this is the correct mapfile before restarting. Exiting.", Util.Logging.LogLevel.CRITICAL);
            System.exit(0);
        }
    }

    void fileToIntersectionMapping() {
        String[] fileLines = Util.FileManager.readFile(mapFile);
        for (int i = 0; i < Objects.requireNonNull(fileLines).length; i++) {
            String[] parts = Util.WorldBuilderUtil.lineToParts(fileLines[i]);
            for (int j = 0; j < parts.length; j++) {
                Util.WorldBuilderUtil.DirNum[] dirNums = Util.WorldBuilderUtil.stringToDirNumArray(parts[j]);
                ImmutablePair<Integer, Integer> tempPair = new ImmutablePair<>(i, j);
                intersectionMapping.put(tempPair, dirNums);
            }
        }
    }

    void intersectionMappingToWorldMap() {
        //TODO: STEP 1 - FIND THE SIZE OF THE MAP
        int maxWidth = -1;
        int maxHeight = -1;
        for (ImmutablePair<Integer, Integer> keys : intersectionMapping.keySet()) {
            if (keys.left > maxWidth) {
                maxWidth = keys.left;
            }
            if (keys.right > maxHeight) {
                maxHeight = keys.right;
            }
        }
        //TODO: STEP 2 - FIND THE DIRECTIONS OF INTERSECTION AT EACH POINT
        for (int i = 0; i <= maxWidth; i++) {
            for (int j = 0; j <= maxHeight; j++) {
                ImmutablePair<Integer, Integer> tempPair = new ImmutablePair<>(i, j);
                Util.WorldBuilderUtil.DirNum[] dirNums = intersectionMapping.get(tempPair);
                Integer[] worldLocation = {(i * 2) + 1, (j * 2) + 1};
                //TODO: STEP 3 - CREATE THE ROADS NECESSARY
                if (dirNums.length != 0) {
                    for (Util.WorldBuilderUtil.DirNum dirNum : dirNums) {
                        Integer[] tempWorldLocation = getNewLocation(worldLocation, dirNum.dir);
                        Road[] newRoadPair = createRoadPair(tempWorldLocation, dirNum.num);
                        if (newRoadPair != null) {
                            UUID[] tempUuids = {newRoadPair[0].getUuid(), newRoadPair[1].getUuid()};
                            ImmutablePair<Integer, Integer> tempWorldPair = new ImmutablePair<>(tempWorldLocation[0], tempWorldLocation[1]);
                            worldMap.put(tempWorldPair, tempUuids);
                            Util.Logging.log("Created RoadPair [" + newRoadPair[0].getUuid() + "," + newRoadPair[1].getUuid() + "] at worldmap location [" + getNewLocation(worldLocation, dirNum.dir)[0] + "," + getNewLocation(worldLocation, dirNum.dir)[1] + "]", Util.Logging.LogLevel.INFO);
                        }
                    }
                }
            }
        }
        //TODO: STEP 5 - CREATE AND LINK INTERSECTIONS WITH ROADS
        for (int i = 0; i <= maxWidth; i++) {
            for (int j = 0; j <= maxHeight; j++) {
                Integer[] worldLocation = {(i * 2) + 1, (j * 2) + 1};
                Road[] roads = new Road[8];
                for (int count = 0; count < 4; count++) {
                    UpdateManager.Direction dir = UpdateManager.intToDirection(count);
                    Integer[] roadLocation = getNewLocation(worldLocation, dir);
                    UUID[] uuids = new UUID[2];
                    ImmutablePair<Integer, Integer> tempPair = new ImmutablePair<>(roadLocation[0], roadLocation[1]);
                    if (worldMap.containsKey(tempPair)) {
                        uuids = worldMap.get(tempPair);
                    }
                    if (uuids != null) {
                        if(dir == UpdateManager.Direction.north || dir == UpdateManager.Direction.east){
                            roads[count] = gridManager.getRoad(uuids[0]);
                            roads[count + 4] = gridManager.getRoad(uuids[1]);
                        }
                        else {
                            roads[count] = gridManager.getRoad(uuids[1]);
                            roads[count + 4] = gridManager.getRoad(uuids[0]);
                        }
                    } else {
                        roads[count] = null;
                        roads[count + 4] = null;
                    }
                }
                Intersection intersection = gridManager.createIntersection(roads, new int[]{i, j});
                ImmutablePair<Integer, Integer> tempWorldPair = new ImmutablePair<>(worldLocation[0], worldLocation[1]);
                UUID[] tempUUID = new UUID[]{intersection.getUuid()};
                worldMap.put(tempWorldPair, tempUUID);
                Util.Logging.log("Created Intersection [" + intersection.getUuid() + "] at worldmap location [" + worldLocation[0] + "," + worldLocation[1] + "]", Util.Logging.LogLevel.INFO);
                for (int count = 0; count < roads.length; count++) {
                    if (roads[count] != null) {
                        if (count < 4) {
                            roads[count].setOutIntersection(intersection);
                            Util.Logging.log("Road ["+roads[count].getUuid()+"] linked to intersection ["+intersection.getUuid()+"] as an output intersection", Util.Logging.LogLevel.INFO);
                        } else {
                            roads[count].setInIntersection(intersection);
                            Util.Logging.log("Road ["+roads[count].getUuid()+"] linked to intersection ["+intersection.getUuid()+"] as an input intersection", Util.Logging.LogLevel.INFO);
                        }
                    }
                }
            }
        }
        if (Main.outputMapToFile) {
            writeMapToFile();
        }
    }

    void writeMapToFile() {
        int maxWidth = -1;
        int maxHeight = -1;
        for (ImmutablePair<Integer, Integer> keys : worldMap.keySet()) {
            if (keys.left > maxWidth) {
                maxWidth = keys.left;
            }
            if (keys.right > maxHeight) {
                maxHeight = keys.right;
            }
        }
        List<String> lines = new ArrayList<>();
        for (int i = 0; i <= maxWidth; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j <= maxHeight; j++) {
                ImmutablePair<Integer, Integer> tempWorldPair = new ImmutablePair<>(i, j);
                String output = "x";
                if (worldMap.containsKey(tempWorldPair)) {
                    UUID uuid = worldMap.get(tempWorldPair)[0];
                    if (gridManager.updateManager.roadMap.containsKey(uuid)) {
                        output = "r";
                    } else if (gridManager.updateManager.intersectionMap.containsKey(uuid)) {
                        output = "i";
                    }
                }
                sb.append(output);
                sb.append(Util.FileManager.DEFAULT_DELIMITER);
            }
            lines.add(sb.toString());
        }
        UUID uuid = UUID.randomUUID();
        Util.FileManager.writeFile("output/" + Util.FileManager.FOLDER_NAME + "/map.csv", lines.toArray(new String[0]), false);
        Util.Logging.log("Writing mapfile to [output/" + Util.FileManager.FOLDER_NAME + "/map.csv]", Util.Logging.LogLevel.INFO);
    }

    Road[] createRoadPair(Integer[] location, int nodes) {
        ImmutablePair<Integer, Integer> tempWorldPair = new ImmutablePair<>(location[0], location[1]);
        if (!worldMap.containsKey(tempWorldPair)) {
            Road in = gridManager.createRoad(nodes);
            Road out = gridManager.createRoad(nodes);
            return new Road[]{in, out};
        }
        return null;
    }

    Integer[] getNewLocation(Integer[] location, UpdateManager.Direction direction) {
        Integer[] newLocation = new Integer[2];
        switch (direction) {
            case north:
                newLocation[0] = location[0] - 1;
                newLocation[1] = location[1];
                break;
            case east:
                newLocation[0] = location[0];
                newLocation[1] = location[1] + 1;
                break;
            case south:
                newLocation[0] = location[0] + 1;
                newLocation[1] = location[1];
                break;
            case west:
                newLocation[0] = location[0];
                newLocation[1] = location[1] - 1;
                break;
        }
        return newLocation;
    }

    Integer[] getRoadLocation(UUID roadUUID) {
        for (ImmutablePair<Integer, Integer> pair : worldMap.keySet()) {
            for (UUID uuid : worldMap.get(pair)) {
                if (roadUUID == uuid) {
                    return new Integer[]{pair.left, pair.right};
                }
            }
        }
        return new Integer[]{-1, -1};
    }
}
