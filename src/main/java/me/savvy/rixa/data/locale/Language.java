package me.savvy.rixa.data.locale;

import lombok.Getter;

/**
 * Created by Timber on 5/31/2017.
 */
public enum Language {

    NO_PERMISSION("noPermission", "I do not have permission for this!"),
    NO_PERMISSION_FOR("noPermissionFor", "Sorry I do not have permission for {0}");
    
    @Getter
    private final String key;
    @Getter
    private final String defaultValue;

    Language(String key, String defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }
    
}
