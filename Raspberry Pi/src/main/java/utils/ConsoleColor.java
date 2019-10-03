/*
 * Copyright (c) 2016 Dirk Koelewijn. All Rights Reserved.
 */

package utils;

/**
 * Class which holds the console colors to display colored text in the console.
 *
 * @author Dirk (created on 15-10-2016)
 * @version 1.0
 */
@SuppressWarnings("WeakerAccess")
public class ConsoleColor {
    public final static String RESET = "\033[0m";
    public final static String NONE = "";

    public static String colored(String string, String color) {
        return color + string + (color.equals(NONE) ? NONE : RESET);
    }

    public static String colored(String string, String colorForeground, String colorBackground) {
        return colorBackground + colored(string, colorForeground);
    }

    @SuppressWarnings("unused")
    public static class Background {
        public final static String WHITE = "\033[40m";
        public final static String RED = "\033[1;41m";
        public final static String GREEN = "\033[1;42m";
        public final static String YELLOW = "\033[1;43m";
        public final static String BLUE = "\033[1;44m";
        public final static String MAGENTA = "\033[1;45m";
        public final static String CYAN = "\033[1;46m";
        public final static String GREY = "\033[1;47m";
    }

    @SuppressWarnings("unused")
    public static class Foreground {
        public final static String WHITE = "\033[30m";
        public final static String RED = "\033[31m";
        public final static String GREEN = "\033[1;32m";
        public final static String YELLOW = "\033[1;33m";
        public final static String BLUE = "\033[1;34m";
        public final static String MAGENTA = "\033[1;35m";
        public final static String CYAN = "\033[1;36m";
        public final static String GREY = "\033[1;37m";
    }
}
