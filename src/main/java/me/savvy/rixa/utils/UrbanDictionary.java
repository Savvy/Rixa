package me.savvy.rixa.utils;

import lombok.Getter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by savit on 6/24/2017.
 */
public class UrbanDictionary {
    
    @Getter
    private String wordToSearch;
    @Getter
    private String definition;
    @Getter
    private String permaLink;
    public UrbanDictionary(String wordToSearch) {
        this.wordToSearch = wordToSearch;
    }

    public boolean search() throws IOException {
        URL url = new URL("http://api.urbandictionary.com/v0/define?term=" + wordToSearch);
        InputStream in = url.openStream();
        Scanner scan = new Scanner(in);
        String jsonString = "";
        while(scan.hasNext()){
            jsonString += scan.next() + " ";
        }
        scan.close();
        try {
            JSONObject obj = new JSONObject(jsonString.trim());
            JSONArray array = obj.getJSONArray("list");
            JSONObject newObj = array.getJSONObject(0);
            this.wordToSearch = newObj.getString("word");
            this.permaLink = newObj.getString("permalink");
            this.definition = newObj.getString("definition");
            return true;
        } catch(JSONException ex) {
            return false;
        }
    }
    
}
