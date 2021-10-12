package com.trafficAnalysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Util {

    public static String getMillis(String num){
        switch(num.length()){
            case 1: return "00"+num;
            case 2: return "0"+num;
            default: return num;
        }
    }

    public static String getMillis(int num){
        String numString = ""+num;
        switch(numString.length()){
            case 1: return "00"+numString;
            case 2: return "0"+numString;
            default: return numString;
        }
    }

    public static class Logging{

        public static ZoneId zoneId = ZoneId.systemDefault();

        static String criticalLog = "CRITICAL:";
        static String errorLog = "ERROR:";
        static String warningLog = "WARNING:";
        static String infoLog = "INFO:";

        public static void log(String message, LogLevel level) {
            if(Main.logToFile){
                logToFile(message,level);
            }
            if(!Main.runInEditor) {
                String timeStamp = getTimestamp().split("\\.")[0];
                switch (level) {
                    case CRITICAL:
                        System.console().writer().println("[" + timeStamp + "]" + criticalLog + message);
                        break;
                    case ERROR:
                        System.console().writer().println("[" + timeStamp + "]" + errorLog + message);
                        break;
                    case WARNING:
                        System.console().writer().println("[" + timeStamp + "]" + warningLog + message);
                        break;
                    case INFO:
                        System.console().writer().println("[" + timeStamp + "]" + infoLog + message);
                        break;
                }
            }
        }

        public static void logToFile(String message, LogLevel level){
            String timeStamp = getTimestamp().split("\\.")[0];
            switch (level) {
                case CRITICAL:
                    Util.FileManager.writeFile("output/"+Main.globalUUID+"/log.txt","[" + timeStamp + "]" + criticalLog + message,false);
                    break;
                case ERROR:
                    Util.FileManager.writeFile("output/"+Main.globalUUID+"/log.txt","[" + timeStamp + "]" + errorLog + message,false);
                    break;
                case WARNING:
                    Util.FileManager.writeFile("output/"+Main.globalUUID+"/log.txt","[" + timeStamp + "]" + warningLog + message,false);
                    break;
                case INFO:
                    Util.FileManager.writeFile("output/"+Main.globalUUID+"/log.txt","[" + timeStamp + "]" + infoLog + message,false);
                    break;
            }
        }

        static String getTimestamp(){
            return ZonedDateTime.now(zoneId).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
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

        public static String DEFAULT_DELIMITER = ",";

        public static boolean createFolder(String folder){
            File file = new File(folder);
            if(file.exists() && file.isDirectory()){
                return true;
            }
            else{
                return file.mkdir();
            }
        }

        public static boolean writeFile(String fileName, String[] lines, boolean overwrite) {
            try {
                File file = new File(fileName);
                FileWriter fileWriter = new FileWriter(file, !overwrite);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                for(String line:lines){
                    bufferedWriter.write(line);
                    bufferedWriter.newLine();
                }
                bufferedWriter.flush();
                bufferedWriter.close();
            } catch (IOException e){
                e.printStackTrace();
                return false;
            }
            return true;
        }

        public static boolean writeFile(String fileName, String line, boolean overwrite){
            try {
                File file = new File(fileName);
                FileWriter fileWriter = new FileWriter(file, !overwrite);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write(line);
                bufferedWriter.newLine();
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
                return readLines(lines, file);
            }
            catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        public static String[] readFile(File file){
            List<String> lines = new ArrayList<>();
            try{
                return readLines(lines, file);
            }
            catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        private static String[] readLines(List<String> lines, File file) throws IOException {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while(bufferedReader.ready()){
                lines.add(bufferedReader.readLine());
            }
            bufferedReader.close();
            fileReader.close();
            return lines.toArray(new String[0]);
        }
    }

    public static class ArgumentHandler{

        public static void getOptions(Argument[] options){
            printIntro();
            for(Argument option:options){
                printOption(option);
            }
            System.console().flush();
        }

        private static void printIntro() {
            System.console().writer().println("\nTraffic Analysis Software for use with paper.\n");
            System.console().writer().println("Argument format: java -jar traffic-analysis.jar --option=\"value\"\n\n");
        }

        public static void printOption(String letter, String option, String info, String values){
            System.console().writer().println("(-"+letter+"),(--"+option+"), "+info+". values: "+values+"\n");
        }

        public static void printOption(Argument argument){
            System.console().writer().println("(-"+argument.letter+"),(--"+argument.name+"), "+argument.information+". values: "+argument.values+"\n");
        }

        public static double searchArgDouble(String letter, String name, String[] args){
            for(String arg:args){
                String[] parts = arg.split("=");
                if(parts.length == 1){
                    break;
                }
                if(parts[0].equals("-"+letter) || parts[0].equals("--"+name)){
                    if(parts[1].startsWith("\"")){
                        parts[1] = parts[1].substring(1, parts[1].length()-1);
                    }
                    return Double.parseDouble(parts[1]);
                }
            }
            return 0;
        }

        public static double searchArgDouble(Argument argument, String[] args){
            return searchArgDouble(argument.letter,argument.name,args);
        }

        public static String searchArgString(String letter, String name, String[] args){
            for(String arg:args){
                String[] parts = arg.split("=");
                if(parts.length == 1){
                    break;
                }
                if(parts[0].equals("-"+letter) || parts[0].equals("--"+name)){
                    if(parts[1].startsWith("\"")){
                        parts[1] = parts[1].substring(1, parts[1].length()-1);
                    }
                    return parts[1];
                }
            }
            return "";
        }

        public static String searchArgString(Argument argument, String[] args){
            return searchArgString(argument.letter,argument.name,args);
        }

        public static Boolean searchArgBoolean(String letter, String name, String[] args){
            for(String arg:args){
                String[] parts = arg.split("=");
                if(parts.length == 1){
                    break;
                }
                if(parts[0].equals("-"+letter) || parts[0].equals("--"+name)){
                    if(parts[1].startsWith("\"")){
                        parts[1] = parts[1].substring(1, parts[1].length()-1);
                    }
                    switch (parts[1].toUpperCase()){
                        case "TRUE": return true;
                        case "FALSE": return false;
                    }
                }
            }
            return false;
        }

        public static Boolean searchArgBoolean(Argument argument, String[] args){
            return searchArgBoolean(argument.letter,argument.name,args);
        }

        public static Boolean searchArgNone(String letter, String name, String[] args){
            for(String arg:args){
                String[] parts = arg.split("=");
                if(parts[0].equals("-"+letter) || parts[0].equals("--"+name)){
                    return true;
                }
            }
            return false;
        }

        public static Boolean searchArgNone(Argument argument, String[] args){
            return searchArgNone(argument.letter,argument.name,args);
        }

        public static long searchArgLong(String letter, String name, String[] args){
            for(String arg:args){
                String[] parts = arg.split("=");
                if(parts.length == 1){
                    break;
                }
                if(parts[0].equals("-"+letter) || parts[0].equals("--"+name)){
                    if(parts[1].startsWith("\"")){
                        parts[1] = parts[1].substring(1, parts[1].length()-1);
                    }
                    return Long.parseLong(parts[1]);
                }
            }
            return 0;
        }

        public static Long searchArgLong(Argument argument, String[] args){
            return searchArgLong(argument.letter,argument.name,args);
        }

        public static class Argument{
            String letter;
            String name;
            String information;
            String values;

            public Argument(String letter, String name, String information, String values){
                this.letter = letter;
                this.name = name;
                this.information = information;
                this.values = values;
            }
        }

    }

}
