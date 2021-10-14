package com.trafficAnalysis;

import java.util.UUID;

import static com.trafficAnalysis.Util.ArgumentHandler.*;

public class Main {

    static double intersectionChance = 0.5;
    static double carSpawnChance = 0.2;
    static String mapFile;
    public static boolean logToFile = true;
    static boolean helpRequested = false;
    static long stepsToSimulate = 0;
    public static boolean outputMapToFile = false;
    public static long updateRate = 1;
    public static boolean verboseLogging = false;
    public static boolean pressureBasedAssessment = false;
    public static boolean runInEditor = false;
    public static int maxIntersectionSteps = 0;
    public static UUID globalUUID;

    static Argument[] options = {
            new Argument("i","intersection","the chance between 0 and 1 for intersections to use","0 - 1 inclusive"),
            new Argument("c","car","the chance between 0 and 1 for a car to spawn on each road", "0 - 1 inclusive"),
            new Argument("m","map","the filename / path to the project mapfile", "filename.extension"),
            new Argument("l", "log","whether to log all console outputs to the log.txt file or not","TRUE/FALSE"),
            new Argument("h","help","prints this help option","N/A"),
            new Argument("s","steps","Steps to be simulated by the program","Any whole number > 0"),
            new Argument("o", "output","output map to file in a rudimentary format once mapping has been completed", "TRUE/FALSE"),
            new Argument("u","update","number of steps per information update","Any whole number > 0"),
            new Argument("v","verbose","enable verbose logging","TRUE/FALSE"),
            new Argument("a","assessment","enable the new method of traffic light assessment utilising the traffic pressure system", "TRUE/FALSE"),
            new Argument("x", "max","set the maximum number of cycles each intersection must wait before changing lights", "Any whole number > 0")
    };

    public static void main(String[] args){
        globalUUID = UUID.randomUUID();
        Util.FileManager.createFolder("output");
        Util.FileManager.createFolder("output/"+globalUUID.toString());
        Util.Logging.log("<NEW PROCESS "+globalUUID+" STARTED>", Util.Logging.LogLevel.INFO);
        if(args.length == 0){
            forceArguments();
        }
        else{
            searchArguments(args);

        }
        if(helpRequested){
            Util.ArgumentHandler.getOptions(options);
            System.exit(0);
        }
        GridManager gridManager = new GridManager(intersectionChance,carSpawnChance,mapFile);
        gridManager.createWorld();
        gridManager.simulateSteps(stepsToSimulate);
        System.exit(0);
    }

    static void forceArguments(){
        intersectionChance = 0.5;
        carSpawnChance = 0.2;
        mapFile = "Map.csv";
        logToFile = true;
        stepsToSimulate = 100;
        updateRate = 1;
        runInEditor = true;
        verboseLogging = true;
        pressureBasedAssessment = true;
    }

    static void searchArguments(String[] args){
        intersectionChance = searchArgDouble(options[0], args);
        carSpawnChance = searchArgDouble(options[1],args);
        mapFile = searchArgString(options[2],args);
        logToFile = searchArgBoolean(options[3],args);
        helpRequested = searchArgNone(options[4],args);
        stepsToSimulate = searchArgLong(options[5],args);
        outputMapToFile = searchArgBoolean(options[6],args);
        updateRate = searchArgLong(options[7],args);
        verboseLogging = searchArgBoolean(options[8],args);
        pressureBasedAssessment = searchArgBoolean(options[9],args);
        maxIntersectionSteps = searchArgInt(options[10],args);
        if(args.length == 0){
            helpRequested = true;
            logToFile = true;
        }
    }

    static void interpretArguments(){
        Util.Logging.log("Interpreted ["+options[0].name+"] to be ["+intersectionChance+"]", Util.Logging.LogLevel.INFO);
        Util.Logging.log("Interpreted ["+options[1].name+"] to be ["+mapFile+"]", Util.Logging.LogLevel.INFO);
        Util.Logging.log("Interpreted ["+options[2].name+"] to be ["+logToFile+"]", Util.Logging.LogLevel.INFO);
        Util.Logging.log("Interpreted ["+options[3].name+"] to be ["+helpRequested+"]", Util.Logging.LogLevel.INFO);
        Util.Logging.log("Interpreted ["+options[4].name+"] to be ["+stepsToSimulate+"]", Util.Logging.LogLevel.INFO);
    }

}
