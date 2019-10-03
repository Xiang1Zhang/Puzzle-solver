/*
 * Copyright (c) 2016 Dirk Koelewijn. All Rights Reserved.
 */

package utils;

import java.io.PrintStream;
import java.util.Date;

import static utils.ConsoleColor.*;

/**
 * Debug utility class.
 *
 * @author Dirk Koelewijn
 * @version 1.0
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class Debug {
    //Print stream
    private final static PrintStream PRINT_STREAM = System.out;
    //Include booleans
    public static boolean INCLUDE_TIMESTAMP = true;
    public static boolean INCLUDE_CLASS = true;
    public static boolean INCLUDE_METHOD = true;
    public static boolean INCLUDE_TYPE = true;
    //Default DebugTypes
    public static DebugType DT_FATAL = new DebugType(0, "FATAL ERROR", Foreground.WHITE, Background.RED);
    public static DebugType DT_ERROR = new DebugType(1, "ERROR", Foreground.RED);
    public static DebugType DT_WARNING = new DebugType(2, "WARNING", Foreground.YELLOW);
    public static DebugType DT_MINOR_WARNING = new DebugType(3, "MINOR WARNING", Foreground.MAGENTA);
    public static DebugType DT_TEST_WARNING = new DebugType(2, "TEST WARNING", Foreground.WHITE, Background.GREEN);
    public static DebugType DT_INFO = new DebugType(4, "INFO", Foreground.BLUE);
    public static DebugType DT_DEBUG = new DebugType(5, "DEBUG", Foreground.WHITE, Background.BLUE);
    public static DebugType DT_DEBUG_LOW = new DebugType(6, "DEBUG (LOW)", Foreground.WHITE, Background.CYAN);
    //Debug level
    public static int DEBUG_LEVEL = DT_DEBUG_LOW.getPriority();
    public static boolean PRINT_COLORS = true;

    //Instance variables
    private Class curClass = null;

    public Debug(Class curClass) {
        this.curClass = curClass;
    }

    public void print(DebugType debugType, String method, String message) {
        if (debugType.getPriority() <= DEBUG_LEVEL)
            PRINT_STREAM.println(getDebugString(debugType, method, message));
    }

    public String getDebugString(DebugType debugType, String method, String message) {
        String msg = (INCLUDE_TIMESTAMP ? new Date() + "\t" : "") +
                (INCLUDE_TYPE ? debugType.getName().toUpperCase() + ": " : "") +
                message +
                (INCLUDE_CLASS ? " [" + this.curClass.getName() : "") +
                (INCLUDE_CLASS && INCLUDE_METHOD ? "." + method + "]" : (INCLUDE_CLASS ? "]" : ""));

        return "" + (PRINT_COLORS ? colored(msg, debugType.getColor(), debugType.getBgColor()) : msg);
    }

    public static class DebugType {
        private int priority;
        private String name;
        private String color;
        private String bgColor;

        public DebugType(int priority, String name, String color) {
            this.priority = priority;
            this.name = name;
            this.color = color;
            this.bgColor = NONE;
        }

        public DebugType(int priority, String name, String color, String bgColor) {
            this.priority = priority;
            this.name = name;
            this.color = color;
            this.bgColor = bgColor;
        }

        public int getPriority() {
            return this.priority;
        }

        public String getName() {
            return this.name;
        }

        public String getColor() {
            return this.color;
        }

        public String getBgColor() {
            return this.bgColor;
        }
    }
}


