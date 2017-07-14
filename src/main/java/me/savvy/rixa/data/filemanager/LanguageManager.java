package me.savvy.rixa.data.filemanager;

import lombok.Getter;
import me.savvy.rixa.data.locale.Language;
import me.savvy.rixa.data.thunderbolt.Thunderbolt;
import me.savvy.rixa.data.thunderbolt.exceptions.FileLoadException;
import me.savvy.rixa.data.thunderbolt.io.ThunderFile;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Timber on 5/31/2017.
 */
public class LanguageManager {

    @Getter
    private File file;
    @Getter
    private JSONObject jsonObject;

    public LanguageManager(File file) {
        this.file = file;
        if (!(file.exists())) {
            FileWriter fileWriter = null;
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
                jsonObject = new JSONObject();

                for(Language language: Language.values()) {
                    jsonObject.put(language.getKey(), language.getDefaultValue());
                }
                fileWriter = new FileWriter(file);
                fileWriter.write(jsonObject.toString());
                System.out.println("Successfully generated language file.");
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                if(fileWriter != null) {
                    try {
                        fileWriter.flush();
                        fileWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return;
        }
        String jsonTxt = null;
        try {
            jsonTxt = IOUtils.toString(new FileReader(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        jsonObject = new JSONObject(jsonTxt);
    }
}
