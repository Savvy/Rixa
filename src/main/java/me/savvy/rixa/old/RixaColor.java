package me.savvy.rixa.old;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class RixaColor {

    private static RixaColor instance;
    private File file;

    public RixaColor() {
        instance = this;
        this.file = new File("Rixa/color.jpg");
    }

    public File coverIMG(Color color) {
        int rgb = color.getRGB();
        try {
            BufferedImage img = null;
            try {
                img = ImageIO.read(file);
            } catch (IOException ex) {
                return null;
            }

            for (int i = 0; i < 100; i++) {
                for (int j = 0; j < 100; j++) {
                    img.setRGB(i, j, rgb);
                }
            }

            File outputfile = new File("Rixa/color.jpg");
            ImageIO.write(img, "jpg", outputfile);
            return outputfile;
        } catch (Exception ignored) {}
        return null;
    }

    public static RixaColor getInstance() {
        return (instance == null ? new RixaColor() : instance);
    }
}
