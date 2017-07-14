package me.savvy.rixa.data.filemanager;

import lombok.Getter;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Timber on 6/4/2017.
 */
public class ConfigManager {

    @Getter
    private File file;
    @Getter
    private JSONObject jsonObject;

    public ConfigManager(File file) {
        this.file = file;
        if (!(file.exists())) {
            FileWriter fileWriter = null;
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
                jsonObject = new JSONObject();
                jsonObject
                        .put("secretKey", "YOUR_TOKEN_HERE")
                        .put("botGame", "Rixa 2.0 | http://rixa.io/invite");
                JSONObject obj = new JSONObject();
                obj.put("hostName", "localhost")
                        .put("password", "password")
                        .put("databaseName", "rixa")
                        .put("userName", "rixa_users")
                        .put("portNumber", "3306");
                jsonObject.put("sql", obj);
                JSONArray botAdmins = new JSONArray();
                botAdmins.put("YOUR_USER_ID_HERE")
                        .put("OTHER_ADMINS")
                        .put("REMOVE_IF_YOU_DONT_WANT");
                obj.put("botAdmins", botAdmins);
                fileWriter = new FileWriter(file);
                fileWriter.write(jsonObject.toString());
                System.out.println("Successfully generated configuration file.");
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
