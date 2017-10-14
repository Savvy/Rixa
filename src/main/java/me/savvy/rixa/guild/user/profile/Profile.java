package me.savvy.rixa.guild.user.profile;

import me.savvy.rixa.guild.RixaGuild;
import me.savvy.rixa.guild.management.Guilds;
import me.savvy.rixa.guild.user.UserData;
import me.savvy.rixa.modules.levels.LevelsModule;
import net.dv8tion.jda.core.entities.Member;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.concurrent.ThreadLocalRandom;

public class Profile {

    String mainPath;
    private static Profile instance;
    private File mainProfile;
    public Profile() {
        instance = this;
        mainPath = "Rixa/profile/";
        mainProfile = new File(mainPath + "profile.png");
    }

    /*
    This method draws final image.
     */
    public File get(Member member) throws IOException {
        if (member == null || member.getGuild() == null) return null;
        RixaGuild rixaGuild = Guilds.getGuild(member.getGuild());
        LevelsModule levelsModule = (LevelsModule) rixaGuild.getModule("Levels");
        UserData userData = levelsModule.getUserData(member.getUser().getId());
        BufferedImage img = ImageIO.read(mainProfile);

        Graphics2D graphics2D = (Graphics2D) img.getGraphics();
        graphics2D.setFont(new Font("Lato Hairline", Font.PLAIN, 75));

        // Profile Pic
        graphics2D.drawImage(
                ImageIO.read(
                        refactorImage(new URL(member.getUser().getAvatarUrl())
                                , new File(mainPath + "profileImg.png")
                        )
                ), 50, 43, null);

        // All things related to userName
        String name = member.getEffectiveName();
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

        // Premium / Regular User
        graphics2D.drawString(r.nextBoolean() ? "Premium User" : "Regular User", 725, 245);

        // Exp Bar
        graphics2D.setFont(new Font("Lato Light", Font.PLAIN, 50));
        graphics2D.drawString(userData.getRemainingExperience() + "/" + userData.getNeededXP
                (userData.getLevelFromExperience(userData.getExperience())).intValue(), 660, 345);

        // User Level
        graphics2D.setFont(new Font("Lato Hairline", Font.PLAIN, 75));
        graphics2D.drawString(String.valueOf(userData.getLevel()), 1280, 355);

        // Statistic Names
        graphics2D.setFont(new Font("Lato Hairline", Font.PLAIN, 50));
        graphics2D.drawString("Total Experience", 330, 450);
        graphics2D.drawString("Global Rank", 330, 550);
        graphics2D.drawString("Tokens", 330, 650);

        // Statistic Values
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
        graphics2D.drawString(numberFormat.format(userData.getExperience()), 1140, 450);
        graphics2D.drawString("#" + userData.getLevel(), 1240, 550);
        graphics2D.drawString("WIP", 1170, 650);


        // Quote
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

        // Quote Author
        graphics2D.setFont(new Font("Lato Light", Font.PLAIN, 25));
        graphics2D.drawString("- George Washington Carver",330, 900);

        // Badges
        BufferedImage badgeOne = ImageIO.read(new File(mainPath + "badges/patreon.png"));
        graphics2D.drawImage(badgeOne, 1231, 924, 61, 71, null);

        BufferedImage badgeTwo = ImageIO.read(new File(mainPath + "badges/contributor-staff.png"));
        graphics2D.drawImage(badgeTwo, 981, 922, 61, 71, null);

        BufferedImage badgeThree = ImageIO.read(new File(mainPath + "badges/contributor-user.png")); // correct
        graphics2D.drawImage(badgeThree, 1105, 922, 61, 71, null);

        File outputfile = new File(mainPath + "finalImage.png");
        ImageIO.write(img, "png", outputfile);
        return outputfile;
    }


    private File refactorImage(URL input, File outputFile) throws IOException {
        final HttpURLConnection connection = (HttpURLConnection) input.openConnection();
        connection.setRequestProperty(
                "User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");
        BufferedImage img = ImageIO.read(connection.getInputStream());
        int width = img.getWidth();
        BufferedImage circleBuffer = new BufferedImage(width, width, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = circleBuffer.createGraphics();
        g2.setClip(new Ellipse2D.Float(0, 0, width, width));
        g2.drawImage(img, 0, 0, width, width, null);
        circleBuffer = resize(circleBuffer, 340, 340);
        ImageIO.write(circleBuffer,"png", outputFile);
        return outputFile;
    }

    /**
     * This method resizes
     * @param img
     * @param newW
     * @param newH
     * @return
     */
    private BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return dimg;
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

    public static Profile getInstance() {
        return (instance == null) ? new Profile() : instance;
    }
}
