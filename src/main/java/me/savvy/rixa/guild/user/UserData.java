package me.savvy.rixa.guild.user;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.core.entities.User;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by savit on 7/14/2017.
 */
public class UserData {

    @Getter private static Map<String, UserData> userData = new HashMap<>();
    @Getter private User user;
    @Getter @Setter private String status;
    @Getter @Setter  private int level;
    @Getter @Setter private int experience;

    public UserData(User user) {
        this.user = user;
    }
}
