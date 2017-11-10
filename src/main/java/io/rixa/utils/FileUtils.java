package io.rixa.utils;

import io.rixa.Rixa;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class FileUtils {

    /*
    Method borrowed from https://hub.spigotmc.org/stash/projects/SPIGOT/repos/bukkit/browse/src/main/java/org/bukkit/plugin/java/JavaPlugin.java
     */
    public static void saveResource(String resourcePath, boolean replace) throws IOException{
        if (resourcePath == null || resourcePath.equals("")) {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }

        resourcePath = resourcePath.replace('\\', '/');
        InputStream in = getResource(resourcePath);
        if (in == null) {
            throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found in " + "file");
        }

        File outFile = new File(Rixa.getInstance().getDefaultPath(), resourcePath);
        int lastIndex = resourcePath.lastIndexOf('/');
        File outDir = new File(Rixa.getInstance().getDefaultPath(), resourcePath.substring(0, lastIndex >= 0 ? lastIndex : 0));

        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        if (!outFile.exists() || replace) {
            OutputStream out = new FileOutputStream(outFile);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } else {
            System.out.println("Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
        }
    }

    /*
    Method borrowed from https://hub.spigotmc.org/stash/projects/SPIGOT/repos/bukkit/browse/src/main/java/org/bukkit/plugin/java/JavaPlugin.java
     */
    public static InputStream getResource(String filename) throws IOException {
        if (filename == null) {
            throw new IllegalArgumentException("Filename cannot be null");
        }
        URL url = FileUtils.class.getClassLoader().getResource(filename);
        if (url == null) {
            return null;
        }
        URLConnection connection = url.openConnection();
        connection.setUseCaches(false);
        return connection.getInputStream();
    }
}
