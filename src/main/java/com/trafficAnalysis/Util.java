package com.trafficAnalysis;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Util {

    public static class Logging{
        static String criticalLog = "CRITICAL:";
        static String errorLog = "ERROR:";
        static String warningLog = "WARNING:";
        static String infoLog = "INFO:";

        public static void log(String message, LogLevel level) {
            switch (level) {
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



}
