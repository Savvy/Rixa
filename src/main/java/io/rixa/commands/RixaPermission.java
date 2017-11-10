package io.rixa.commands;

public enum RixaPermission {
    NONE;

    public static RixaPermission fromString(String string) {
        for (RixaPermission value : values()) {
            if (value.toString().equalsIgnoreCase(string)) {
                return value;
            }
        }
        return null;
    }
}
