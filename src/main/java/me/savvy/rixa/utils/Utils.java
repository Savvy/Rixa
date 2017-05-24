package me.savvy.rixa.utils;

/**
 * Created by Timber on 5/23/2017.
 */
public class Utils {

    public static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }
}
