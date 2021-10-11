package com.trafficAnalysis;

import static com.trafficAnalysis.Util.ArgumentHandler.*;

public class Main {

    static double rngChance = 0.5;
    static String mapFile;
    public static boolean logToFile = true;
    static boolean helpRequested = false;
    static long stepsToSimulate = 0;
    public static boolean outputMapToFile = false;

    static Argument[] options = {
            new Argument("c","chance","the chance between 0 and 1 required for the quantum random number generator","0 - 1 inclusive"),
            new Argument("m","map","the filename / path to the project mapfile", "filename.extension"),
            new Argument("l", "log","whether to log all console outputs to the log.txt file or not","TRUE/FALSE"),
            new Argument("h","help","prints this help option","N/A"),
            new Argument("s","steps","Steps to be simulated by the program","Any whole number > 0"),
            new Argument("o", "output","output map to file in a rudimentary format once mapping has been completed", "TRUE/FALSE")
    };

    public static void main(String[] args){
        /*
        searchArguments(args);
        Util.Logging.log("<NEW PROCESS STARTED>", Util.Logging.LogLevel.INFO);
        if(helpRequested){
            Util.ArgumentHandler.getOptions(options);
            System.exit(0);
        }

         */
        forceArguments();
        GridManager gridManager = new GridManager(rngChance,mapFile);
        gridManager.createWorld();
        gridManager.simulateSteps(stepsToSimulate);
        System.exit(0);
    }

    static void forceArguments(){
        rngChance = 0.5;
        mapFile = "Map.csv";
        logToFile = true;
        stepsToSimulate = 100;
    }

    static void searchArguments(String[] args){
        rngChance = searchArgDouble(options[0], args);
        mapFile = searchArgString(options[1],args);
        logToFile = searchArgBoolean(options[2],args);
        helpRequested = searchArgNone(options[3],args);
        stepsToSimulate = searchArgLong(options[4],args);
        outputMapToFile = searchArgBoolean(options[5],args);
        if(args.length == 0){
            helpRequested = true;
            logToFile = true;
        }
    }

    static void interpretArguments(){
        Util.Logging.log("Interpreted ["+options[0].name+"] to be ["+rngChance+"]", Util.Logging.LogLevel.INFO);
        Util.Logging.log("Interpreted ["+options[1].name+"] to be ["+mapFile+"]", Util.Logging.LogLevel.INFO);
        Util.Logging.log("Interpreted ["+options[2].name+"] to be ["+logToFile+"]", Util.Logging.LogLevel.INFO);
        Util.Logging.log("Interpreted ["+options[3].name+"] to be ["+helpRequested+"]", Util.Logging.LogLevel.INFO);
        Util.Logging.log("Interpreted ["+options[4].name+"] to be ["+stepsToSimulate+"]", Util.Logging.LogLevel.INFO);
    }

}
