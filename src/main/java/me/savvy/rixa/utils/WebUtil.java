package me.savvy.rixa.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by savit on 7/11/2017.
 */
public class WebUtil {

    public static String getWebPage(String url) throws IOException {
        URL searchURL = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) searchURL.openConnection();
        return getWebPage(conn);
    }

    public static String getWebPage(HttpURLConnection conn) throws IOException {
        StringBuilder sb = new StringBuilder();
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");
        conn.setRequestProperty("Referer", "http://www.google.com");

        int response = conn.getResponseCode();
        if (response == 403) {
            System.out.println("Quota Exceeded");
        }
        else if (response != 200) {
            System.out.println("DEBUG: Response code: " + response);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = in.readLine()) != null) {
            sb.append(line).append("\n");
        }
        in.close();

        return sb.toString();
    }
}
