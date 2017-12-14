package io.rixa.bot.utils;

public class Utils {

    public static boolean isInteger(String s) {
        return isInteger(s,10);
    }

    public static boolean isInteger(String s, int radix) {
        if(s.isEmpty()) return false;
        for(int i = 0; i < s.length(); i++) {
            if(i == 0 && s.charAt(i) == '-') {
                if(s.length() == 1) return false;
                else continue;
            }
            if(Character.digit(s.charAt(i),radix) < 0) return false;
        }
        return true;
    }

    public static long toMilliSec(String s) {
        // This is not my regex :P | From: http://stackoverflow.com/a/8270824
        String[] sl = s.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");

        long i = Long.parseLong(sl[0]);
        switch (sl[1]) {
            case "seconds":
            case "second":
            case "sec":
            case "s":
                return i * 1000;
            case "minutes":
            case "min":
            case "minute":
            case "m":
                return i * 1000 * 60;
            case "hours":
            case "hour":
            case "hr":
            case "h":
                return i * 1000 * 60 * 60;
            case "days":
            case "day":
            case "dy":
            case "d":
                return i * 1000 * 60 * 60 * 24;
            case "weeks":
            case "week":
            case "wk":
            case "w":
                return i * 1000 * 60 * 60 * 24 * 7;
            case "months":
            case "month":
            case "mnth":
            case "mo":
                return i * 1000 * 60 * 60 * 24 * 30;
            default:
                return -1;
        }
    }
}
