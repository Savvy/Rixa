package test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.concurrent.ThreadLocalRandom;

public class Test {

    private File file, file1, expBox;
    // https://api.forismatic.com/api/1.0/?method=getQuote&format=jsonp&lang=en&jsonp=? quotes
    public Test() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        System.out.println(Arrays.toString(ge.getAvailableFontFamilyNames()));
        file = new File("C:\\Users\\savit\\Desktop\\profile.png");
        file1 = new File("C:\\Users\\savit\\Desktop\\atheron-text.jpg");
        expBox = new File("C:\\Users\\savit\\Desktop\\exp-box.jpg");
        try {
            test(new File("C:\\Users\\savit\\Desktop\\newImg.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        coverIMG();
    }

    private java.util.List<String> addLinebreaks(String input, int maxLineLength) {
        StringTokenizer tok = new StringTokenizer(input, " ");
        java.util.List<String> list = new ArrayList<>();
      //  StringBuilder output = new StringBuilder(input.length());
        int lineLen = 0;
        while (tok.hasMoreTokens()) {
            String word = tok.nextToken();

            if (lineLen + word.length() > maxLineLength) {
                list.add(":newLine:");
                lineLen = 0;
            }
            list.add(word + " ");
            lineLen += word.length();
        }
        return list;
    }

    /**
     * This method turns squared images and resizes
     * @throws IOException
     */
    public void test(File outputFile) throws IOException {
        BufferedImage img = null;
        img = ImageIO.read(file1);
        int width = img.getWidth();
        BufferedImage circleBuffer = new BufferedImage(width, width, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = circleBuffer.createGraphics();
        g2.setClip(new Ellipse2D.Float(0, 0, width, width));
        g2.drawImage(img, 0, 0, width, width, null);
        circleBuffer = resize(circleBuffer, 340, 340);
        ImageIO.write(circleBuffer,"png", outputFile);
    }

    /**
     * This method resizes
     * @param img
     * @param newW
     * @param newH
     * @return
     */
    public BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }

    /**
     * This method turns original image into image WITH profile pic
     * @return File
     */
    private void coverIMG() {
            try {
                BufferedImage img = null;
                try {
                    img = ImageIO.read(file);
                } catch (IOException ex) {
                    return;
                }

                Graphics2D graphics2D = (Graphics2D) img.getGraphics();
                graphics2D.setFont(new Font("Lato Hairline", Font.PLAIN, 75));
                // Profile Pic
                graphics2D.drawImage(ImageIO.read(new File("C:\\Users\\savit\\Desktop\\newImg.png")), 50, 43, null);
                String name = "itsTheSavvySavageINHD";
                // 900 is pretty much the beginning for 10 string chars
                int x = 1075;
                if (name.length() > 10 && name.length() < 20) {
                    x = 1180;
                } else if (name.length() > 20) {
                    graphics2D.setFont(new Font("Lato Hairline", Font.PLAIN, 50));
                    name = name.substring(0, 17) + "...";
                    //x = 1180;
                }
                x = x - graphics2D.getFontMetrics().stringWidth(name);
                graphics2D.drawString(name, x, 182);
                graphics2D.setFont(new Font("Lato Hairline", Font.PLAIN, 50));
                Random r = ThreadLocalRandom.current();
                graphics2D.drawString(r.nextBoolean() ? "Premium User" : "Regular User", 725, 245);
                graphics2D.setFont(new Font("Lato Hairline", Font.PLAIN, 75));
                graphics2D.drawString("24", 1280, 355);

                graphics2D.setFont(new Font("Lato Hairline", Font.PLAIN, 50));

                graphics2D.drawString("Total Experience", 330, 450);
                graphics2D.drawString("Global Rank", 330, 550);
                graphics2D.drawString("Tokens", 330, 650);


                graphics2D.drawString("24,061", 1140, 450);
                graphics2D.drawString("#3", 1240, 550);
                graphics2D.drawString("3,461", 1170, 650);

                graphics2D.setFont(new Font("Lato Light", Font.PLAIN, 50));
                graphics2D.drawString("5800 / 6000", 660, 345);
                graphics2D.setFont(new Font("Lato Light", Font.PLAIN, 30));
                java.util.List<String> list = addLinebreaks("How far you go in life depends on you being tender with the young, compassionate with the aged, " +
                        "sympathetic with the striving and tolerant of the weak and the strong. Because someday in life you will have been all of these.", 60);
                int i = 750;
                StringBuilder b = new StringBuilder();
                for (String string : list ) {
                    if (string.equalsIgnoreCase(":newLine:")) {
                        graphics2D.drawString(b.toString().trim(), 330, i);
                        i += 40;
                        b = new StringBuilder();
                        continue;
                    }
                    b.append(string);
                }

                graphics2D.setFont(new Font("Lato Light", Font.PLAIN, 25));
                graphics2D.drawString("- George Washington Carver",330, 900);

                BufferedImage badgeOne = ImageIO.read(new File("C:\\Users\\savit\\Desktop\\Badges\\patreon.png"));
                graphics2D.drawImage(badgeOne, 1231, 924, 61, 71, null);

                BufferedImage badgeTwo = ImageIO.read(new File("C:\\Users\\savit\\Desktop\\Badges\\contributor-staff.png"));
                graphics2D.drawImage(badgeTwo, 981, 922, 61, 71, null);

                BufferedImage badgeThree = ImageIO.read(new File("C:\\Users\\savit\\Desktop\\Badges\\contributor-user.png")); // correct
                graphics2D.drawImage(badgeThree, 1105, 922, 61, 71, null);
                /*for (int ii = 0; ii < 10; ii++) {
                    graphics2D.drawImage(ImageIO.read(expBox), (350 + (ii * 80)), 345, null);
                }*/
                File outputfile = new File("C:\\Users\\savit\\Desktop\\color.png");
                ImageIO.write(img, "png", outputfile);
            } catch (Exception ignored) {
            }
        }

    public static void main(String[] args) {
        new Test();
    }
}
