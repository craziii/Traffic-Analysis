package com.trafficAnalysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Util {

    public static class Logging{
        static String criticalLog = "CRITICAL:";
        static String errorLog = "ERROR:";
        static String warningLog = "WARNING:";
        static String infoLog = "INFO:";

        public static void log(String message, LogLevel level) {
            switch (level) {
                case CRITICAL:
                    System.console().writer().println(Arrays.toString((criticalLog + message).getBytes(StandardCharsets.UTF_8)));
                    break;
                case ERROR:
                    System.console().writer().println(Arrays.toString((errorLog + message).getBytes(StandardCharsets.UTF_8)));
                    break;
                case WARNING:
                    System.console().writer().println(Arrays.toString((warningLog + message).getBytes(StandardCharsets.UTF_8)));
                    break;
                case INFO:
                    System.console().writer().println(Arrays.toString((infoLog + message).getBytes(StandardCharsets.UTF_8)));
                    break;
            }
        }

        enum LogLevel{
            INFO,
            WARNING,
            ERROR,
            CRITICAL
        }
    }

    public static class WorldBuilderUtil{

        static String DELIMITER = ",";
        static String INTERSECTION_DELIMITER = ":";
        static String VALUE_DELIMITER = "-";

        static String directionToLetter(UpdateManager.Direction dir){
            switch (dir){
                case north: return "n";
                case east: return "e";
                case south: return "s";
                case west: return "w";
                default: return "";
            }
        }

        static UpdateManager.Direction letterToDirection(String letter){
            switch (letter){
                case "n": return UpdateManager.Direction.north;
                case "e": return UpdateManager.Direction.east;
                case "s": return UpdateManager.Direction.south;
                case "w": return UpdateManager.Direction.west;
                default: return UpdateManager.Direction.none;
            }
        }

        public static DirNum stringToDirNum(String s){
            String[] parts = s.split(VALUE_DELIMITER);
            UpdateManager.Direction direction = letterToDirection(parts[0]);
            int nodes = Integer.parseInt(parts[1]);
            return new DirNum(direction,nodes);
        }

        public static String dirNumToString(DirNum dn){
            StringBuilder sb = new StringBuilder();
            sb.append(directionToLetter(dn.dir));
            sb.append(VALUE_DELIMITER);
            sb.append(dn.num);
            return sb.toString();
        }

        public static DirNum[] stringToDirNumArray(String s){
            List<DirNum> dirNums = new ArrayList<>();
            String[] parts = s.split(INTERSECTION_DELIMITER);
            for(String part:parts){
                dirNums.add(stringToDirNum(part));
            }
            return dirNums.toArray(dirNums.toArray(new DirNum[0]));
        }

        public static String dirNumArrayToString(DirNum[] dirNums){
            StringBuilder sb = new StringBuilder();
            for(DirNum dn:dirNums){
                sb.append(dirNumToString(dn));
                sb.append(INTERSECTION_DELIMITER);
            }
            sb.deleteCharAt(sb.length()-1);
            return sb.toString();
        }

        public static String[] lineToParts(String s){
            return s.split(DELIMITER);
        }

        public static String partsToLine(String[] parts){
            StringBuilder sb = new StringBuilder();
            for(String part:parts){
                sb.append(part);
                sb.append(DELIMITER);
            }
            sb.deleteCharAt(sb.length()-1);
            return sb.toString();
        }

        public static class DirNum{
            UpdateManager.Direction dir;
            int num;

            public DirNum(){
                dir = UpdateManager.Direction.none;
                num = 0;
            }

            public DirNum(UpdateManager.Direction d, int n){
                dir = d;
                num = n;
            }

            public UpdateManager.Direction getDir() {
                return dir;
            }

            public int getNum() {
                return num;
            }
        }

    }

    public static class FileManager{

        public static boolean writeFile(String fileName, String[] lines) {
            try {
                File file = new File(fileName);
                FileWriter fileWriter = new FileWriter(file);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                for(String line:lines){
                    bufferedWriter.write(line);
                }
                bufferedWriter.flush();
                bufferedWriter.close();
            } catch (IOException e){
                e.printStackTrace();
                return false;
            }
            return true;
        }

        public static String[] readFile(String filename){
            List<String> lines = new ArrayList<>();
            try{
                File file = new File(filename);
                FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                while(bufferedReader.ready()){
                    lines.add(bufferedReader.readLine());
                }
                bufferedReader.close();
                fileReader.close();
                return lines.toArray(new String[0]);
            }
            catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
    }



}
