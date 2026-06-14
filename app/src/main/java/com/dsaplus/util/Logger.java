package com.dsaplus.util;

public class Logger {

    public enum Level { DEBUG, INFO, WARN, ERROR }

    private static Level currentLevel = Level.DEBUG;

    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String CYAN = "\u001B[36m";

    private Logger() {}

    public static void setLevel(Level level) {
        currentLevel = level;
    }

    public static boolean isDebugEnabled() {
        return currentLevel.compareTo(Level.DEBUG) <= 0;
    }

    public static void debug(String tag, String msg) {
        log(Level.DEBUG, CYAN, tag, msg);
    }

    public static void info(String tag, String msg) {
        log(Level.INFO, GREEN, tag, msg);
    }

    public static void warn(String tag, String msg) {
        log(Level.WARN, YELLOW, tag, msg);
    }

    public static void error(String tag, String msg) {
        log(Level.ERROR, RED, tag, msg);
    }

    private static void log(Level level, String color, String tag, String msg) {
        if (level.compareTo(currentLevel) < 0) return;
        System.out.println(color + "[" + padLevel(level) + "] [" + tag + "] " + msg + RESET);
    }

    private static String padLevel(Level level) {
        switch (level) {
            case DEBUG: return "DEBUG";
            case INFO:  return "INFO ";
            case WARN:  return "WARN ";
            case ERROR: return "ERROR";
            default:    return "     ";
        }
    }
}
