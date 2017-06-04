package me.savvy.rixa.data.filemanager;

import me.savvy.rixa.data.locale.Language;
import me.savvy.rixa.data.thunderbolt.Thunderbolt;
import me.savvy.rixa.data.thunderbolt.exceptions.FileLoadException;
import me.savvy.rixa.data.thunderbolt.io.ThunderFile;

import java.io.IOException;

/**
 * Created by Timber on 5/31/2017.
 */
public class LanguageManager {
    private ThunderFile tf;
    public LanguageManager() {
        tf = null;
        try {
            if(!Thunderbolt.load("language", "Rixa/language")) {
                tf = Thunderbolt.get("language");
                addDefaults();
            }
        } catch (FileLoadException | IOException e) {
            e.printStackTrace();
        }
    }

    public ThunderFile getLanguage() {
        return tf;
    }

    private void addDefaults() throws IOException {
        for(Language language: Language.values()) {
            tf.set(language.getKey(), language.getDefaultValue());
        }
        tf.save();
    }
}
